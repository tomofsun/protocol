package com.wakzz.server;

import com.wakzz.common.decoder.ProtoBodyDecoder;
import com.wakzz.common.decoder.SerializerDecoder;
import com.wakzz.common.encoder.ProtoBodyEncoder;
import com.wakzz.common.encoder.SerializerEncoder;
import com.wakzz.common.handler.EchoHandler;
import com.wakzz.common.handler.HeartbeatHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class EchoServer {

    public void start(int port) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(port)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new ProtoBodyEncoder());
                            ch.pipeline().addLast(new ProtoBodyDecoder());

                            ch.pipeline().addLast(new HeartbeatHandler());

                            ch.pipeline().addLast(new SerializerEncoder());
                            ch.pipeline().addLast(new SerializerDecoder());

                            ch.pipeline().addLast(new EchoHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind().sync();
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        EchoServer server = new EchoServer();
        server.start(8000);
    }
}