package com.wakzz.common.utils;

import com.wakzz.common.context.ProtoType;
import com.wakzz.common.model.ProtoBody;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;

public class ProtoBodyUtils {

    public static ProtoBody valueOf(String body) {
        return valueOf(ProtoType.String, body.getBytes(StandardCharsets.UTF_8));
    }

    public static ProtoBody valueOf(byte[] body) {
        return valueOf(ProtoType.Binary, body);
    }

    public static ProtoBody valueOf(ProtoType type, byte[] body) {
        ProtoBody protoBody = new ProtoBody();
        protoBody.setStart(new byte[]{(byte) 0x55, (byte) 0x77, (byte) 0x66, (byte) 0x88});
        protoBody.setHeader((byte) 0x00);
        protoBody.setType(type.getValue());
//        protoBody.setOrderNo(orderNo);
        protoBody.setLength(body.length + 4 + 1 + 1 + 2 + 4);
        protoBody.setBody(body);
        return protoBody;
    }

    public static byte[] toBytes(ProtoBody protoBody) {
        ByteBuf out = Unpooled.buffer();
        // start
        out.writeBytes(protoBody.getStart());
        // header
        out.writeByte(protoBody.getHeader());
        // type
        out.writeByte(protoBody.getType());
        // order
        out.writeShort(protoBody.getOrderNo());
        // length
        out.writeInt((int)protoBody.getLength());
        // body
        out.writeBytes(protoBody.getBody());
        return out.array();
    }
}
