package com.wakzz.common.decoder;

import com.wakzz.common.coder.ProtoCoder;
import com.wakzz.common.coder.ProtoCoderFactory;
import com.wakzz.common.context.ProtoVersion;
import com.wakzz.common.exception.UnknownProtoVersionException;
import com.wakzz.common.model.ProtoBody;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

@Slf4j
public class ProtoBodyDecoder extends ByteToMessageDecoder {

    private static final String PROTO_VERSION_KEY = "_PROTO_VERSION_KEY_";

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        try {
            List<ProtoBody> list = decode(ctx, in);
            if (!list.isEmpty()) {
                out.addAll(list);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.close();
        }
    }

    private List<ProtoBody> decode(ChannelHandlerContext ctx, ByteBuf in) {
        if (in.readableBytes() < 5) {
            return Collections.emptyList();
        }
        byte version = in.getByte(in.readerIndex() + 4);
        ProtoVersion protoVersion = ProtoVersion.valueOf(version);
        if (protoVersion == null) {
            throw new UnknownProtoVersionException(version);
        }

        ProtoCoder protoCoder = ProtoCoderFactory.getProtoCoder(protoVersion);
        List<ProtoBody> list = protoCoder.decode(in);
        if (!list.isEmpty()) {
            // 第一次通信时,设置协议版本
            Attribute<ProtoVersion> attribute = ctx.channel().attr(AttributeKey.valueOf(PROTO_VERSION_KEY));
            attribute.setIfAbsent(protoVersion);
        }
        return list;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        log.info("ProtoBodyDecoder:channelReadComplete");
        ctx.fireChannelReadComplete();
    }
}
