package com.wakzz.common.encoder;

import com.wakzz.common.coder.ProtoCoder;
import com.wakzz.common.coder.ProtoCoderFactory;
import com.wakzz.common.context.ProtoSerializer;
import com.wakzz.common.context.ProtoType;
import com.wakzz.common.context.ProtoVersion;
import com.wakzz.common.model.ProtoBody;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.ObjectUtils;

@ChannelHandler.Sharable
public class ProtoBodyEncoder extends MessageToByteEncoder<ProtoBody> {

    private static final String PROTO_VERSION_KEY = "_PROTO_VERSION_KEY_";

    private ProtoVersion defaultProtoVersion;

    public ProtoBodyEncoder() {
        this(ProtoVersion.V1);
    }

    public ProtoBodyEncoder(ProtoVersion defaultProtoVersion) {
        this.defaultProtoVersion = defaultProtoVersion;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ProtoBody body, ByteBuf out) {
        // 获取当前连接使用的协议版本,由第一次通信时设置
        Attribute<ProtoVersion> attribute = ctx.channel().attr(AttributeKey.valueOf(PROTO_VERSION_KEY));
        ProtoVersion protoVersion = ObjectUtils.defaultIfNull(attribute.get(), defaultProtoVersion);

        ProtoSerializer protoSerializer = ProtoSerializer.valueOf(body.getSerializer());
        ProtoType protoType = ProtoType.valueOf(body.getType());

        ProtoCoder protoCoder = ProtoCoderFactory.getProtoCoder(protoVersion);
        protoCoder.encode(protoType, protoSerializer, body.getBody(), out);
    }

}
