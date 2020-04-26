package com.wakzz.common.codec;

import com.wakzz.common.context.ProtoSerializer;
import com.wakzz.common.context.ProtoType;
import com.wakzz.common.exception.UnknownProtoSerializerException;
import com.wakzz.common.model.ProtoBody;
import com.wakzz.common.serializer.SerializerFactory;
import com.wakzz.common.serializer.StringSerializer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;

@Slf4j
public class SerializerCodec extends MessageToMessageCodec<ProtoBody, Object> {

    private static final String PROTO_SERIALIZER_KEY = "_PROTO_SERIALIZER_KEY_";

    private ProtoSerializer defaultProtoSerializer;

    public SerializerCodec() {
        this(ProtoSerializer.String);
    }

    public SerializerCodec(ProtoSerializer defaultProtoSerializer) {
        this.defaultProtoSerializer = defaultProtoSerializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        // 获取当前连接使用的报文序列化算法,由第一次通信时设置
        Attribute<ProtoSerializer> attribute = ctx.channel().attr(AttributeKey.valueOf(PROTO_SERIALIZER_KEY));
        ProtoSerializer protoSerializer = ObjectUtils.defaultIfNull(attribute.get(), defaultProtoSerializer);

        StringSerializer serializer = SerializerFactory.getSerializer(protoSerializer);
        byte[] buffer = serializer.serialize(msg);
        ProtoBody body = new ProtoBody();
        body.setType(ProtoType.Body.getValue());
        body.setSerializer(protoSerializer.getValue());
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

            // 第一次通信时,设置连接的序序列化算法
            Attribute<ProtoSerializer> attribute = ctx.channel().attr(AttributeKey.valueOf(PROTO_SERIALIZER_KEY));
            attribute.setIfAbsent(protoSerializer);

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
