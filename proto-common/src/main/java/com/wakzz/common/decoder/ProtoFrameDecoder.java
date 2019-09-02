package com.wakzz.common.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class ProtoFrameDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Object decode = decode(ctx, in);
        if (decode != null) {
            out.add(decode);
        }
    }

    private Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        // 完整报文至少32个字节
        if (in.readableBytes() < 32) {
            return null;
        }
        return null;
    }

    /**
     * 获取当前请求报文的字节长度
     */
    private long getFrameLength(ByteBuf in) {
        boolean isBigEndian = isBigEndian(in);
        return (isBigEndian ? in.getInt(2) : in.getIntLE(2)) & 0x0FFFFL;
    }

    /**
     * 报文是否是大端
     */
    private boolean isBigEndian(ByteBuf in) {
        byte header = in.getByte(4);
        return (byte) (header& 0x1) == 0x0;
    }
}
