package com.wakzz.client;

import com.wakzz.common.decoder.ProtoFrameDecoder;
import com.wakzz.common.encoder.ProtoBodyEncoder;
import com.wakzz.common.handler.HeartbeatHandler;
import com.wakzz.common.model.ProtoBody;
import com.wakzz.common.utils.ProtoBodyUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

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
                            // out
                            ch.pipeline().addLast(new ProtoBodyEncoder());

                            // in
                            ch.pipeline().addLast(new ProtoFrameDecoder());
                            ch.pipeline().addLast(new IdleStateHandler(0, 0, 10));
                            ch.pipeline().addLast(new HeartbeatHandler());
                        }
                    });

            // Start the client.
            ChannelFuture f = b.connect(host, port).sync();

            ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(1);
            Channel channel = f.channel();

            channel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                @Override
                public void channelRead(ChannelHandlerContext ctx, Object msg) {
                    ProtoBody protoBody = (ProtoBody) msg;
                    String response = new String(protoBody.getBody());
                    queue.add(response);
                    ReferenceCountUtil.release(msg);
//                    ctx.close();
                }
            });
            channel.writeAndFlush(ProtoBodyUtils.valueOf("hello world"));
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