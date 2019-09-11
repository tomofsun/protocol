package com.wakzz.client;

import com.wakzz.common.context.ProtoType;
import com.wakzz.common.decoder.ProtoFrameDecoder;
import com.wakzz.common.encoder.ProtoBodyEncoder;
import com.wakzz.common.handler.FutureClientHandler;
import com.wakzz.common.handler.HeartbeatHandler;
import com.wakzz.common.handler.SSLClientCodec;
import com.wakzz.common.utils.ProtoBodyUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ProtoConnectionManager implements Closeable {

    private String host;
    private int port;
    private ProtoPoolConfig config;
    private ConnectionManagerStatus status;
    private Bootstrap bootstrap;
    private EventLoopGroup workerGroup;
    private AtomicInteger connectionCount = new AtomicInteger();
    private LinkedBlockingQueue<ProtoConnection> pool = new LinkedBlockingQueue<>();

    public ProtoConnectionManager(String host, int port) {
        this(host, port, new ProtoPoolConfig());
    }

    public ProtoConnectionManager(String host, int port, ProtoPoolConfig config) {
        this.host = host;
        this.port = port;
        this.config = config;
        initBootstrap();
    }

    private void initBootstrap() {
        workerGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new ProtoBodyEncoder());
                        ch.pipeline().addLast(new ProtoFrameDecoder());
                        ch.pipeline().addLast(new SSLClientCodec());
                        if (config.isTestWhileIdle()) {
                            ch.pipeline().addLast(new IdleStateHandler(0, 0, config.getTimeBetweenEvictionRunsSec()));
                        }
                        ch.pipeline().addLast(new HeartbeatHandler());
                        ch.pipeline().addLast(new FutureClientHandler());
                    }
                });
        status = ConnectionManagerStatus.Running;
    }

    private ProtoConnection createConnection() {
        try {
            Channel channel = bootstrap.connect(host, port).sync().channel();
            ProtoConnection connection = new ProtoConnection();
            connection.setChannel(channel);
            connection.setConnectionManager(this);
            connectionCount.incrementAndGet();
            return connection;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从连接池申请连接
     * 如果开启了testOnReturn,则获取连接后发起一次心跳包
     */
    public synchronized ProtoConnection getConnection() throws InterruptedException {
        // 检查连接池是否已经关闭
        if (status != ConnectionManagerStatus.Running) {
            throw new RuntimeException("连接池已关闭");
        }
        // 当连接池没有可以复用的连接且已创建的连接数量未达到上限时允许创建新连接
        if (pool.isEmpty() && connectionCount.intValue() < config.getMaxConnectionCount()) {
            return createConnection();
        }
        ProtoConnection connection;
        try {
            connection = pool.poll(config.getConnectTimeoutMillis(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (connection == null) {
            throw new RuntimeException(new TimeoutException("获取连接超时"));
        }
        if (config.isTestOnBorrow()) {
            Channel channel = connection.getChannel();
            channel.writeAndFlush(ProtoBodyUtils.valueOf(ProtoType.Ping, null))
                    .addListener(future -> {
                        if (!future.isSuccess())
                            closeConnection(connection);
                    }).sync();
            if (!channel.isOpen()) {
                return getConnection();
            }
        }
        return connection;
    }

    /**
     * 向连接池返还连接
     * 如果开启了testOnReturn,则返还连接前发起一次心跳包
     */
    synchronized void release(ProtoConnection connection) throws InterruptedException {
        if (status != ConnectionManagerStatus.Running) {
            closeConnection(connection);
            return;
        }
        if (config.isTestOnReturn()) {
            Channel channel = connection.getChannel();
            channel.writeAndFlush(ProtoBodyUtils.valueOf(ProtoType.Ping, null))
                    .addListener(future -> {
                        if (!future.isSuccess())
                            closeConnection(connection);
                    }).sync();
            if (!channel.isOpen()) {
                return;
            }
        }
        pool.add(connection);
    }

    void closeConnection(ProtoConnection connection) {
        try {
            Channel channel = connection.getChannel();
            connectionCount.addAndGet(-1);
            if (channel.isOpen()) {
                channel.close().sync();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public synchronized void close() {
        this.status = ConnectionManagerStatus.Shutdown;
        Iterator<ProtoConnection> iterator = pool.iterator();
        while (iterator.hasNext()) {
            closeConnection(iterator.next());
            iterator.remove();
        }
        workerGroup.shutdownGracefully();
    }

}
