package com.wakzz.common.encoder;

import com.wakzz.common.model.ProtoBody;
import com.wakzz.common.utils.ProtoBodyUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class ProtoBodyEncoder extends MessageToByteEncoder<ProtoBody> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ProtoBody body, ByteBuf out) {
        Integer orderNo = getNextOrderNo(ctx);
        body.setOrderNo(orderNo);

        byte[] bytes = ProtoBodyUtils.toBytes(body);
        out.writeBytes(bytes);
    }

    private Integer getNextOrderNo(ChannelHandlerContext ctx) {
        Attribute<Integer> orderNoAttr = ctx.channel().attr(AttributeKey.valueOf("_order_no_"));
        int orderNo = orderNoAttr.get() == null ? 0 : orderNoAttr.get() + 1;
        orderNoAttr.set(orderNo);
        return orderNo;
    }

}
