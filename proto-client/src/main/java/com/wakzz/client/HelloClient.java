package com.wakzz.client;

import com.wakzz.common.encoder.ProtoBodyEncoder;
import com.wakzz.common.utils.ProtoBodyUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
public class HelloClient {
    public void connect(String host, int port) throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new ProtoBodyEncoder());
                        }
                    });

            // Start the client.
            ChannelFuture f = b.connect(host, port).sync();

            ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(1);
            Channel channel = f.channel();

            channel.writeAndFlush(ProtoBodyUtils.valueOf("hello world"));
            channel.read().pipeline().addLast(new ChannelInboundHandlerAdapter() {
                @Override
                public void channelRead(ChannelHandlerContext ctx, Object msg) {
                    ByteBuf result = (ByteBuf) msg;
                    String response = result.toString(StandardCharsets.UTF_8);
                    queue.add(response);
                    ReferenceCountUtil.release(msg);
                    ctx.close();
                }
            });
            String response = queue.poll(5, TimeUnit.SECONDS);
            System.out.println("response:" + response);

            // Wait until the connection is closed.
            channel.closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) throws Exception {
        HelloClient client = new HelloClient();
        client.connect("127.0.0.1", 8000);
    }
}