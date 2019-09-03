package com.wakzz.common.encoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class StringEncoder extends MessageToByteEncoder<String> {

    @Override
    protected void encode(ChannelHandlerContext ctx, String body, ByteBuf out) {
        Attribute<Integer> orderNoAttr = ctx.channel().attr(AttributeKey.valueOf("_order_no_"));
        int orderNo = orderNoAttr.get() == null ? 0 : orderNoAttr.get() + 1;
        orderNoAttr.set(orderNo);

        // start
        out.writeByte(0x55);
        out.writeByte(0x77);
        out.writeByte(0x66);
        out.writeByte(0x88);
        // header
        out.writeByte(0x00);
        // type
        out.writeByte(0x03);
        // order
        out.writeShort(orderNo);
        // length
        int frameLength = body.length() + 4 + 1 + 1 + 2 + 4;
        out.writeInt(frameLength);
        // body
        out.writeBytes(body.getBytes());
    }

}
