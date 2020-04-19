package com.wakzz.common.encoder;

import com.wakzz.common.coder.ProtoCoder;
import com.wakzz.common.coder.ProtoCoderFactory;
import com.wakzz.common.context.Constant;
import com.wakzz.common.context.ProtoType;
import com.wakzz.common.context.ProtoVersion;
import com.wakzz.common.model.ProtoParams;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

@ChannelHandler.Sharable
public class ProtoBodyEncoder extends MessageToByteEncoder<byte[]> {

    private ProtoVersion version;

    public ProtoBodyEncoder() {
        this(ProtoVersion.V1);
    }

    public ProtoBodyEncoder(ProtoVersion version) {
        this.version = version;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, byte[] body, ByteBuf out) {
        Attribute<ProtoParams> protoParamsAttr = ctx.channel().attr(AttributeKey.valueOf(Constant.ATTRIBUTE_PROTO_PARAMS));

        ProtoParams protoParams = protoParamsAttr.get();
        if (protoParams == null) {
            protoParams = new ProtoParams();
            protoParams.setProtoVersion(version);
            protoParamsAttr.set(protoParams);
        }
        ProtoVersion version = protoParams.getProtoVersion();
        if (version == null) {
            protoParams.setProtoVersion(this.version);
        }

        ProtoCoder protoCoder = ProtoCoderFactory.getProtoCoder(protoParams.getProtoVersion());
        protoCoder.encode(ProtoType.Body, body, protoParams, out);
    }

}
