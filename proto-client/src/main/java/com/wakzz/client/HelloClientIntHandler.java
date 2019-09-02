package com.wakzz.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
public class HelloClientIntHandler extends ChannelInboundHandlerAdapter {

    // 接收server端的消息，并打印出来
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf result = (ByteBuf) msg;
        String message = result.toString(StandardCharsets.UTF_8);
        log.info("Server said: {}", message);
        ReferenceCountUtil.release(msg);
    }

    // 连接成功后，向server发送消息
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        String msg = "Are you ok?";
        ByteBuf requestBuffer = Unpooled.copiedBuffer(msg.getBytes());
        ctx.write(requestBuffer);
        ctx.flush();
    }
}