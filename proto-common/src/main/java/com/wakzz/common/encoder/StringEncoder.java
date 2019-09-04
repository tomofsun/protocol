package com.wakzz.common.encoder;

import com.wakzz.common.model.ProtoBody;
import com.wakzz.common.utils.ProtoBodyUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class StringEncoder extends MessageToByteEncoder<String> {

    @Override
    protected void encode(ChannelHandlerContext ctx, String body, ByteBuf out) {
        ProtoBody protoBody = ProtoBodyUtils.valueOf(body);
        byte[] bytes = ProtoBodyUtils.toBytes(protoBody);
        out.writeBytes(bytes);
    }

}
