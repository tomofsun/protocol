package com.wakzz.common.handler;

import com.wakzz.common.model.ProtoBody;
import com.wakzz.common.utils.ProtoBodyUtils;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class EchoHandler extends SimpleChannelInboundHandler<ProtoBody> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtoBody protoBody) {
        handleRead(ctx, protoBody);
    }

    private void handleRead(ChannelHandlerContext ctx, ProtoBody request) {
        // 向客户端发送消息
        String body = new String(request.getBody());
        ProtoBody response = ProtoBodyUtils.valueOf(request.getId(), body);
        ChannelFuture channelFuture = ctx.writeAndFlush(response);

        channelFuture.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("服务端request: {}, response: {}", body, body);
            } else {
                log.error(future.cause().getMessage(), future.cause().getCause());
            }
        });
        ctx.fireChannelRead(request);
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