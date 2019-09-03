package com.wakzz.server;

import com.wakzz.common.model.ProtoBody;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class EchoServerInHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (!(msg instanceof ProtoBody)) {
            return;
        }
        ProtoBody protoBody = (ProtoBody) msg;
        System.out.println(protoBody);
        // 接收并打印客户端的信息
        log.info("Client said: {}", protoBody);
        // 释放资源，这行很关键
        ReferenceCountUtil.safeRelease(msg);

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