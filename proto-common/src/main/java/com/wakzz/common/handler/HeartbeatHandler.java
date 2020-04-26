package com.wakzz.common.handler;

import com.wakzz.common.context.ProtoSerializer;
import com.wakzz.common.context.ProtoType;
import com.wakzz.common.model.ProtoBody;
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

    private static final String CONNECTION_LAST_READ_TIME_KEY = "_CONNECTION_LAST_READ_TIME_KEY_";

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
        // 更新当前连接的最后通信时间
        updateLastReadTime(ctx);
        if (protoBody.getType() == ProtoType.Pong.getValue()) {
            // 服务端返回的心跳包response,不需要处理
            log.info("接收心跳包pong");
        } else if (protoBody.getType() == ProtoType.Ping.getValue()) {
            // 客户端请求的心跳包request,返回心跳包response
            log.info("接收心跳包ping,回复心跳包pong");
            sendPong(ctx);
        } else {
            ctx.fireChannelRead(protoBody);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 关闭超时的连接
        closeIfTimeout(ctx);
        if (evt instanceof IdleStateEvent) {
            log.info("发送心跳包ping");
            sendPing(ctx);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    private void sendPing(ChannelHandlerContext ctx) {
        ProtoBody request = new ProtoBody();
        request.setSerializer(ProtoSerializer.String.getValue());
        request.setType(ProtoType.Ping.getValue());
        ctx.writeAndFlush(request).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    }

    private void sendPong(ChannelHandlerContext ctx) {
        ProtoBody response = new ProtoBody();
        response.setType(ProtoType.Pong.getValue());
        response.setSerializer(ProtoSerializer.String.getValue());
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    }

    /**
     * 关闭超时的连接
     */
    private void closeIfTimeout(ChannelHandlerContext ctx) {
        if (isTimeout(ctx)) {
            log.info("连接超时,关闭连接");
            ctx.close();
        }
    }

    private boolean isTimeout(ChannelHandlerContext ctx) {
        Attribute<Date> attribute = ctx.channel().attr(AttributeKey.valueOf(CONNECTION_LAST_READ_TIME_KEY));
        Date lastReadTime = attribute.get();
        if (lastReadTime == null) {
            return true;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, -maxIdleSecond);
        return calendar.getTime().after(lastReadTime);
    }

    /**
     * 更新当前连接的最后一次通信时间
     */
    private void updateLastReadTime(ChannelHandlerContext ctx) {
        Attribute<Date> attribute = ctx.channel().attr(AttributeKey.valueOf(CONNECTION_LAST_READ_TIME_KEY));
        attribute.set(new Date());
    }
}
