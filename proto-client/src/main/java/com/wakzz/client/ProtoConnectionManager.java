package com.wakzz.client;

import com.wakzz.common.decoder.ProtoFrameDecoder;
import com.wakzz.common.encoder.ProtoBodyEncoder;
import com.wakzz.common.handler.HeartbeatHandler;
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
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ProtoConnectionManager implements Closeable {

    private String host;
    private int port;
    private ConnectionManagerStatus status;
    private Bootstrap bootstrap;
    private EventLoopGroup workerGroup;
    private AtomicInteger connectionCount = new AtomicInteger();
    private LinkedBlockingQueue<ProtoConnection> pool = new LinkedBlockingQueue<>();

    public ProtoConnectionManager(String host, int port) {
        this.host = host;
        this.port = port;
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
                        // out
                        ch.pipeline().addLast(new ProtoBodyEncoder());

                        // in
                        ch.pipeline().addLast(new ProtoFrameDecoder());
                        ch.pipeline().addLast(new IdleStateHandler(0, 0, 10));
                        ch.pipeline().addLast(new HeartbeatHandler());
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
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public synchronized ProtoConnection getConnection() {
        if (status != ConnectionManagerStatus.Running){
            throw new RuntimeException("连接池已关闭");
        }
        while (!pool.isEmpty()) {
            ProtoConnection connection = pool.poll();
            if (connection.getChannel().isOpen()) {
                return connection;
            }
        }
        return createConnection();
    }

    synchronized void release(ProtoConnection connection) {
        if (status != ConnectionManagerStatus.Running){
            closeConnection(connection);
            return;
        }
        pool.add(connection);
    }

    private void removeConnection(ProtoConnection connection) {
        pool.remove(connection);
    }

    private void closeConnection(ProtoConnection connection) {
        try {
            Channel channel = connection.getChannel();
            if (channel.isOpen()) {
                channel.close().sync();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    synchronized void closeAndRemoveConnection(ProtoConnection connection) {
        closeConnection(connection);
        removeConnection(connection);
    }

    @Override
    public synchronized void close() {
        this.status = ConnectionManagerStatus.Shutdown;
        Iterator<ProtoConnection> iterator = pool.iterator();
        while (iterator.hasNext()){
            closeConnection(iterator.next());
            iterator.remove();
        }
        workerGroup.shutdownGracefully();
    }
}
