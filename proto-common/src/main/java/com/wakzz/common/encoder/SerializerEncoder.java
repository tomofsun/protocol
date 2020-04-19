package com.wakzz.common.encoder;

import com.wakzz.common.context.Constant;
import com.wakzz.common.context.ProtoSerializer;
import com.wakzz.common.model.ProtoParams;
import com.wakzz.common.serializer.SerializerFactory;
import com.wakzz.common.serializer.StringSerializer;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.util.List;

@ChannelHandler.Sharable
public class SerializerEncoder extends MessageToMessageEncoder<Object> {

    private ProtoSerializer protoSerializer;

    public SerializerEncoder() {
        this(ProtoSerializer.String);
    }

    public SerializerEncoder(ProtoSerializer protoSerializer) {
        this.protoSerializer = protoSerializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object object, List<Object> out) {
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
        byte[] buffer = serializer.serialize(object);
        out.add(buffer);
    }

}
