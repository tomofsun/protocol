package com.wakzz.common.handler;

import com.wakzz.common.model.DefaultFuture;
import com.wakzz.common.model.ProtoBody;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class FutureClientHandler extends SimpleChannelInboundHandler<ProtoBody> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtoBody protoBody) {
        handleRead(ctx, protoBody);
    }

    private void handleRead(ChannelHandlerContext ctx, ProtoBody protoBody) {
        DefaultFuture.received(protoBody);
        ctx.fireChannelRead(protoBody);
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