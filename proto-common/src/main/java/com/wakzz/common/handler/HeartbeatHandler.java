package com.wakzz.common.handler;

import com.wakzz.common.context.Constant;
import com.wakzz.common.context.ProtoType;
import com.wakzz.common.model.ProtoBody;
import com.wakzz.common.model.ProtoParams;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.Date;

@Slf4j
@ChannelHandler.Sharable
public class HeartbeatHandler extends SimpleChannelInboundHandler<ProtoBody> {

    private int maxIdleSecond;
    public HeartbeatHandler(int maxIdleSecond){
        this.maxIdleSecond = maxIdleSecond;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtoBody protoBody) {
        try {
            handleRead(ctx, protoBody);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleRead(ChannelHandlerContext ctx, ProtoBody protoBody) {
        updateLastReadTime(ctx);
        if (protoBody.getType() == ProtoType.Pong.getValue()) {
            // 服务端返回的心跳包response,不需要处理
            log.info("接收心跳包pong");
        } else if (protoBody.getType() == ProtoType.Ping.getValue()) {
            // 客户端请求的心跳包request,返回心跳包response
            log.info("接收心跳包ping,回复心跳包pong");
            ProtoBody response = new ProtoBody();
            response.setType(ProtoType.Pong.getValue());
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        } else {
            ctx.fireChannelRead(protoBody);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (isTimeout(ctx)) {
            // 连接超时,关闭连接
            log.info("连接超时,关闭连接");
            ctx.close();
            return;
        }
        if (evt instanceof IdleStateEvent) {
            log.info("发送心跳包ping");
            ProtoBody request = new ProtoBody();
            request.setType(ProtoType.Ping.getValue());
            ctx.writeAndFlush(request).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    private boolean isTimeout(ChannelHandlerContext ctx) {
        Attribute<ProtoParams> protoParamsAttr = ctx.channel().attr(AttributeKey.valueOf(Constant.ATTRIBUTE_PROTO_PARAMS));
        ProtoParams protoParams = protoParamsAttr.get();
        if (protoParams == null) {
            protoParams = new ProtoParams();
            protoParams.setLastReadTime(new Date());
            protoParamsAttr.set(protoParams);
        }
        Date lastReadTime = protoParams.getLastReadTime();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, -maxIdleSecond);
        return calendar.getTime().after(lastReadTime);
    }

    private void updateLastReadTime(ChannelHandlerContext ctx) {
        Attribute<ProtoParams> protoParamsAttr = ctx.channel().attr(AttributeKey.valueOf(Constant.ATTRIBUTE_PROTO_PARAMS));
        ProtoParams protoParams = protoParamsAttr.get();
        if (protoParams == null) {
            protoParams = new ProtoParams();
            protoParamsAttr.set(protoParams);
        }
        protoParams.setLastReadTime(new Date());
    }
}
