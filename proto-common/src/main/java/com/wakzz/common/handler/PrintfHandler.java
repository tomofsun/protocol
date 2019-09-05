package com.wakzz.common.handler;

import com.wakzz.common.model.ProtoBody;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class PrintfHandler extends SimpleChannelInboundHandler<ProtoBody> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtoBody protoBody) {
        log.info("protoBody: {}", new String(protoBody.getBody()));
        ctx.fireChannelRead(protoBody);
    }

}
