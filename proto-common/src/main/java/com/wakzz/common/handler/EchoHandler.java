package com.wakzz.common.handler;

import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class EchoHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String value) {
        handleRead(ctx, value);
    }

    private void handleRead(ChannelHandlerContext ctx, String value) {
        // 向客户端发送消息
        ChannelFuture channelFuture = ctx.writeAndFlush(value);

        channelFuture.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("服务端request: {}, response: {}", value, value);
            } else {
                log.error(future.cause().getMessage(), future.cause().getCause());
            }
        });
        ctx.fireChannelRead(value);
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