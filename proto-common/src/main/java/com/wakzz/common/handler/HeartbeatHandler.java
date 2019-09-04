package com.wakzz.common.handler;

import com.wakzz.common.context.ProtoType;
import com.wakzz.common.model.ProtoBody;
import com.wakzz.common.utils.ProtoBodyUtils;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class HeartbeatHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ProtoBody protoBody = (ProtoBody) msg;
        if (protoBody.getType() == ProtoType.Pong.getValue()) {
            // 服务端返回的心跳包response,不需要处理
            log.info("接收心跳包pong");
            ReferenceCountUtil.safeRelease(msg);
        } else if (protoBody.getType() == ProtoType.Ping.getValue()) {
            // 客户端请求的心跳包request,返回心跳包response
            log.info("接收心跳包ping");
            ReferenceCountUtil.safeRelease(msg);
            ProtoBody response = ProtoBodyUtils.valueOf(ProtoType.Pong, null);
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        } else {
            super.channelRead(ctx, msg);
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
