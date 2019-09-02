package com.wakzz.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
@ChannelHandler.Sharable
public class EchoServerInHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf result = (ByteBuf) msg;
        String request = result.toString(StandardCharsets.UTF_8);
        // 接收并打印客户端的信息
        log.info("Client said: {}", request);
        // 释放资源，这行很关键
        ReferenceCountUtil.release(msg);

        // 向客户端发送消息
        String response = "I am ok!";
        ByteBuf respBuffer = Unpooled.copyInt(response.length());
        respBuffer.writeBytes(response.getBytes());
        ChannelFuture channelFuture = ctx.writeAndFlush(respBuffer);

        channelFuture.addListener((ChannelFutureListener) future -> System.out.println("发送完成"));
//        channelFuture.addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        super.exceptionCaught(ctx, cause);
    }
}