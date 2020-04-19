package com.wakzz.common.codec;

import com.wakzz.common.context.Constant;
import com.wakzz.common.model.ProtoParams;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.util.List;

public class SSLServerCodec extends MessageToMessageCodec<byte[], byte[]> {

    @Override
    protected void encode(ChannelHandlerContext ctx, byte[] msg, List<Object> out) throws Exception {

    }

    @Override
    protected void decode(ChannelHandlerContext ctx, byte[] msg, List<Object> out) throws Exception {
        Attribute<ProtoParams> protoParamsAttr = ctx.channel().attr(AttributeKey.valueOf(Constant.ATTRIBUTE_PROTO_PARAMS));

        ProtoParams protoParams = protoParamsAttr.get();
        if (protoParams == null) {
            protoParams = new ProtoParams();
            protoParamsAttr.set(protoParams);
        }
        if (protoParams.getSslPubKey() == null) {
            // SSL未生成公钥
        } else if (protoParams.getSslCipher() == null) {
            // 客户端未上送对称加密秘钥
        } else {

        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        super.userEventTriggered(ctx, evt);
    }
}
