package com.wakzz.common.utils;

import com.wakzz.common.context.ProtoType;
import com.wakzz.common.model.ProtoBody;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;

public class ProtoBodyUtils {

    public static ProtoBody valueOf(String body) {
        byte[] bytes = body == null ? null : body.getBytes(StandardCharsets.UTF_8);
        return valueOf(ProtoType.String, bytes);
    }

    public static ProtoBody valueOf(byte[] body) {
        return valueOf(ProtoType.Binary, body);
    }

    public static ProtoBody valueOf(ProtoType type, byte[] body) {
        ProtoBody protoBody = new ProtoBody();
        protoBody.setStart(new byte[]{(byte) 0x55, (byte) 0x77, (byte) 0x66, (byte) 0x88});
        protoBody.setHeader((byte) 0x00);
        protoBody.setType(type.getValue());
        protoBody.setVersion(0x0000);
        protoBody.setLength(body == null ? 0 : body.length);
        if (body != null) {
            protoBody.setBody(body);
        }
        return protoBody;
    }

    public static byte[] toBytes(ProtoBody protoBody) {
        long bodyLength = protoBody.getBody() == null ? 0 : protoBody.getBody().length;
        if (bodyLength != protoBody.getLength()){
            throw new IllegalArgumentException("ProtoBody参数错误:body长度与length不一致");
        }

        ByteBuf out = Unpooled.buffer();
        // start
        out.writeBytes(protoBody.getStart());
        // header
        out.writeByte(protoBody.getHeader());
        // type
        out.writeByte(protoBody.getType());
        // order
        out.writeShort(protoBody.getVersion());
        // length
        out.writeInt((int) protoBody.getLength());
        // body
        if (protoBody.getBody() != null) {
            out.writeBytes(protoBody.getBody());
        }
        return out.array();
    }
}
