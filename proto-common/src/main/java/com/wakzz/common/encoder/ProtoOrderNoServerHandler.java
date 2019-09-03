package com.wakzz.common.encoder;

import com.wakzz.common.model.ProtoBody;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.util.List;

public class ProtoOrderNoServerHandler extends MessageToMessageCodec<ProtoBody, ProtoBody> {

    private static final String ORDER_NO_KEY = "_server_order_no_";

    @Override
    protected void encode(ChannelHandlerContext ctx, ProtoBody msg, List<Object> out) {
        Integer orderNo = getNextOrderNo(ctx);
        msg.setOrderNo(orderNo);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ProtoBody msg, List<Object> out) {

    }

    private Integer getNextOrderNo(ChannelHandlerContext ctx) {
        Attribute<Integer> orderNoAttr = ctx.channel().attr(AttributeKey.valueOf(ORDER_NO_KEY));
        int orderNo = orderNoAttr.get() == null ? 0 : orderNoAttr.get() + 1;
        orderNoAttr.set(orderNo);
        return orderNo;
    }

}
