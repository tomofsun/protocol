package com.wakzz.common.handler;

import com.wakzz.common.model.ProtoBody;
import com.wakzz.common.utils.ProtoBodyUtils;
import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class EchoHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof ProtoBody)) {
            super.channelRead(ctx, msg);
            return;
        }
        ProtoBody request = (ProtoBody) msg;
        ReferenceCountUtil.safeRelease(msg);

        // 向客户端发送消息
        ProtoBody response = ProtoBodyUtils.valueOf(request.getBody());
        ChannelFuture channelFuture = ctx.writeAndFlush(response);

        channelFuture.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()){
                log.info("服务端request: {}, response: {}", new String(request.getBody()), new String(response.getBody()));
            } else {
                log.error(future.cause().getMessage(), future.cause().getCause());
            }
        });
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