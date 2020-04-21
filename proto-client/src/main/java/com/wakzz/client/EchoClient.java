package com.wakzz.client;

import com.wakzz.common.decoder.ProtoBodyDecoder;
import com.wakzz.common.decoder.SerializerDecoder;
import com.wakzz.common.encoder.ProtoBodyEncoder;
import com.wakzz.common.encoder.SerializerEncoder;
import com.wakzz.common.handler.HeartbeatHandler;
import com.wakzz.common.handler.PrintfHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class EchoClient {

    public static void main(String[] args) throws Exception {

        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS));

                        ch.pipeline().addLast(new ProtoBodyEncoder());
                        ch.pipeline().addLast(new ProtoBodyDecoder());

                        ch.pipeline().addLast(new HeartbeatHandler());

                        ch.pipeline().addLast(new SerializerEncoder());
                        ch.pipeline().addLast(new SerializerDecoder());

                        ch.pipeline().addLast(new PrintfHandler());
                    }
                });
        Channel channel = bootstrap.connect("127.0.0.1", 8000).sync().channel();
        channel.writeAndFlush("World").sync();
    }

}
