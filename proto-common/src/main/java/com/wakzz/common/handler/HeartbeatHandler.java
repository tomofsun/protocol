package com.wakzz.common.handler;

import com.wakzz.common.context.ProtoType;
import com.wakzz.common.model.ProtoBody;
import com.wakzz.common.utils.ProtoBodyUtils;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class HeartbeatHandler extends SimpleChannelInboundHandler<ProtoBody> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtoBody protoBody) {
        try {
            handleRead(ctx, protoBody);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleRead(ChannelHandlerContext ctx, ProtoBody protoBody) {
        if (protoBody.getType() == ProtoType.Pong.getValue()) {
            // 服务端返回的心跳包response,不需要处理
            log.info("接收心跳包pong");
        } else if (protoBody.getType() == ProtoType.Ping.getValue()) {
            // 客户端请求的心跳包request,返回心跳包response
            log.info("接收心跳包ping");
            ProtoBody response = ProtoBodyUtils.valueOf(ProtoType.Pong, null);
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        } else {
            ctx.fireChannelRead(protoBody);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            log.info("发送心跳包ping");
            ProtoBody request = ProtoBodyUtils.valueOf(ProtoType.Ping, null);
            ctx.writeAndFlush(request).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
