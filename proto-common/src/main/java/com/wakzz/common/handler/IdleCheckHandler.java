package com.wakzz.common.handler;

import io.netty.channel.*;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;

@Slf4j
@ChannelHandler.Sharable
public class IdleCheckHandler extends ChannelInboundHandlerAdapter implements ChannelOutboundHandler {

    private long maxIdleTimeMillis;
    public IdleCheckHandler(long maxIdleTimeMillis){
        this.maxIdleTimeMillis = maxIdleTimeMillis;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleCheckEvent) {
            // TODO 连接空闲时间超过配置,断开连接
            Attribute<Long> lastActiveTimeAttr = ctx.channel().attr(AttributeKey.valueOf("last_active_time"));
            long lastActiveTime = lastActiveTimeAttr.get() == null ? 0 : lastActiveTimeAttr.get();
            if (System.currentTimeMillis() - lastActiveTime > maxIdleTimeMillis) {
                ctx.channel().close().sync();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress,
                     ChannelPromise promise) {
        ctx.bind(localAddress, promise);
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress,
                        SocketAddress localAddress, ChannelPromise promise) {
        ctx.connect(remoteAddress, localAddress, promise);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) {
        ctx.disconnect(promise);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) {
        ctx.close(promise);
    }

    @Override
    public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) {
        ctx.deregister(promise);
    }

    @Override
    public void read(ChannelHandlerContext ctx) {
        ctx.read();
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        ctx.write(msg, promise);
    }

    @Override
    public void flush(ChannelHandlerContext ctx) {
        ctx.flush();
    }

}

class IdleCheckEvent {
}