package com.wakzz.common.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class PrintfHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object object) {
        log.info("request object: {}", object);
        ctx.fireChannelRead(object);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        log.info("PrintfHandler:channelReadComplete");
        ctx.fireChannelReadComplete();
    }

}
