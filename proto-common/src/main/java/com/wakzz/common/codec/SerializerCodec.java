package com.wakzz.common.codec;

import com.wakzz.common.context.Constant;
import com.wakzz.common.context.ProtoSerializer;
import com.wakzz.common.context.ProtoType;
import com.wakzz.common.exception.UnknownProtoSerializerException;
import com.wakzz.common.model.ProtoBody;
import com.wakzz.common.model.ProtoParams;
import com.wakzz.common.serializer.SerializerFactory;
import com.wakzz.common.serializer.StringSerializer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class SerializerCodec extends MessageToMessageCodec<ProtoBody, Object> {

    private ProtoSerializer protoSerializer;

    public SerializerCodec() {
        this(ProtoSerializer.String);
    }

    public SerializerCodec(ProtoSerializer protoSerializer) {
        this.protoSerializer = protoSerializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        Attribute<ProtoParams> protoParamsAttr = ctx.channel().attr(AttributeKey.valueOf(Constant.ATTRIBUTE_PROTO_PARAMS));

        ProtoParams protoParams = protoParamsAttr.get();
        if (protoParams == null) {
            protoParams = new ProtoParams();
            protoParams.setProtoSerializer(protoSerializer);
            protoParamsAttr.set(protoParams);
        }
        ProtoSerializer protoSerializer = protoParams.getProtoSerializer();
        if (protoSerializer == null) {
            protoParams.setProtoSerializer(this.protoSerializer);
        }

        StringSerializer serializer = SerializerFactory.getSerializer(protoParams.getProtoSerializer());
        byte[] buffer = serializer.serialize(msg);
        ProtoBody body = new ProtoBody();
        body.setType(ProtoType.Body.getValue());
        body.setBody(buffer);
        out.add(body);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ProtoBody msg, List<Object> out) throws Exception {
        try {
            byte[] body = msg.getBody();
            ProtoSerializer protoSerializer = ProtoSerializer.valueOf(msg.getSerializer());
            if (protoSerializer == null) {
                throw new UnknownProtoSerializerException(msg.getSerializer());
            }
            StringSerializer serializer = SerializerFactory.getSerializer(protoSerializer);
            String value = serializer.deserialize(body, String.class);
            out.add(value);
            log.info("requestId: {}, value: {}", msg.getRequestId(), value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.close();
        }
    }
}
