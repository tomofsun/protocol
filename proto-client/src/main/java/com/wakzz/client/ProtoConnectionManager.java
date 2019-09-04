package com.wakzz.client;

import com.wakzz.common.decoder.ProtoFrameDecoder;
import com.wakzz.common.encoder.ProtoBodyEncoder;
import com.wakzz.common.handler.HeartbeatHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.LinkedBlockingQueue;

public class ProtoConnectionManager {

    private String host;
    private int port;
    private Bootstrap bootstrap;
    private LinkedBlockingQueue<ProtoConnection> pool = new LinkedBlockingQueue<>();

    public ProtoConnectionManager(String host, int port) {
        this.host = host;
        this.port = port;
        initBootstrap();
    }

    private void initBootstrap() {
        bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup())
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
    }

    public ProtoConnection getConnection() throws InterruptedException {
        return pool.take();
    }

    public void addConnection(ProtoConnection connection) {
        pool.add(connection);
    }

}
