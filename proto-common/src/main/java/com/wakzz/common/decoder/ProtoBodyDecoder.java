package com.wakzz.common.decoder;

import com.wakzz.common.coder.ProtoCoder;
import com.wakzz.common.coder.ProtoCoderFactory;
import com.wakzz.common.context.Constant;
import com.wakzz.common.context.ProtoSerializer;
import com.wakzz.common.context.ProtoVersion;
import com.wakzz.common.exception.UnknownProtoVersionException;
import com.wakzz.common.model.ProtoBody;
import com.wakzz.common.model.ProtoParams;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ProtoBodyDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        try {
            List<ProtoBody> list = decode(ctx, in);
            setProtoParams(list, ctx);
            if (!list.isEmpty()) {
                out.addAll(list);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.close();
        }
    }

    private List<ProtoBody> decode(ChannelHandlerContext ctx, ByteBuf in) {
        List<ProtoBody> list = new ArrayList<>();

        if (in.readableBytes() < 5) {
            return list;
        }
        byte version = in.getByte(in.readerIndex() + 4);
        ProtoVersion protoVersion = ProtoVersion.valueOf(version);
        if (protoVersion == null) {
            throw new UnknownProtoVersionException(version);
        }
        ProtoCoder protoCoder = ProtoCoderFactory.getProtoCoder(protoVersion);
        return protoCoder.decode(in);
    }

    private void setProtoParams(List<ProtoBody> list, ChannelHandlerContext ctx) {
        if (list.isEmpty()) {
            return;
        }
        Attribute<ProtoParams> protoParamsAttr = ctx.channel().attr(AttributeKey.valueOf(Constant.ATTRIBUTE_PROTO_PARAMS));
        ProtoParams protoParams = protoParamsAttr.get();
        if (protoParams == null) {
            protoParams = new ProtoParams();
            protoParams.setProtoVersion(ProtoVersion.valueOf(list.get(0).getVersion()));
            protoParams.setProtoSerializer(ProtoSerializer.valueOf(list.get(0).getSerializer()));
            protoParamsAttr.set(protoParams);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        log.info("ProtoBodyDecoder:channelReadComplete");
        ctx.fireChannelReadComplete();
    }
}
