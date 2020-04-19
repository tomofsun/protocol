package com.wakzz.common.decoder;

import com.wakzz.common.context.ProtoSerializer;
import com.wakzz.common.exception.UnknownProtoSerializerException;
import com.wakzz.common.model.ProtoBody;
import com.wakzz.common.serializer.SerializerFactory;
import com.wakzz.common.serializer.StringSerializer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class SerializerDecoder extends MessageToMessageDecoder<ProtoBody> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ProtoBody msg, List<Object> out) {
        try {
            byte[] body = msg.getBody();
            ProtoSerializer protoSerializer = ProtoSerializer.valueOf(msg.getSerializer());
            if (protoSerializer == null) {
                throw new UnknownProtoSerializerException(msg.getSerializer());
            }
            StringSerializer serializer = SerializerFactory.getSerializer(protoSerializer);
            String value = serializer.deserialize(body, String.class);
            out.add(value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.close();
        }
    }
}
