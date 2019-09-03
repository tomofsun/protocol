package com.wakzz.common.handler;

import com.wakzz.common.model.ProtoBody;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrintfHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        if (!(msg instanceof ProtoBody)) {
            return;
        }

        ProtoBody protoBody = (ProtoBody) msg;
        log.info("protoBody: {}", new String(protoBody.getBody()));
    }

}
