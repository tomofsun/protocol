package com.wakzz.common.encoder;

import com.wakzz.common.model.ProtoBody;
import com.wakzz.common.utils.ProtoBodyUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@ChannelHandler.Sharable
public class ProtoBodyEncoder extends MessageToByteEncoder<ProtoBody> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ProtoBody body, ByteBuf out) {
        byte[] bytes = ProtoBodyUtils.toBytes(body);
        out.writeBytes(bytes);
    }

}
