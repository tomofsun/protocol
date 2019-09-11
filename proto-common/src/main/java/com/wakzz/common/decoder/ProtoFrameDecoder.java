package com.wakzz.common.decoder;

import com.wakzz.common.model.ProtoBody;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ReferenceCountUtil;

import java.util.ArrayList;
import java.util.List;

public class ProtoFrameDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        List<Object> list = decode(ctx, in);
        if (!list.isEmpty()) {
            out.addAll(list);
        }
    }

    private List<Object> decode(ChannelHandlerContext ctx, ByteBuf in) {
        List<Object> list = new ArrayList<>();

        // 循环读取解决粘包
        while (true) {
            discardingIfIllegalStart(in);

            // 完整报文至少12个字节
            // 半包等待后续数据
            if (in.readableBytes() < 12) {
                break;
            }

            // 是否是大端
            boolean isBigEndian = isBigEndian(in);

            // 检查报文是否接受完整
            long frameLength = getFrameLength(in, isBigEndian);
            // TODO 最大请求报文长度
            // TODO 等待超时后抛弃报文
            // 半包等待后续数据
            if (in.readableBytes() < frameLength) {
                break;
            }

            ByteBuf startBuf = in.readBytes(4);
            byte[] start = ByteBufUtil.getBytes(startBuf);
            ReferenceCountUtil.safeRelease(startBuf);
            byte header = in.readByte();
            byte type = in.readByte();
            int version = (isBigEndian ? in.readShort() : in.readShortLE()) & 0xFFFF;
            int id = (isBigEndian ? in.readInt() : in.readIntLE());
            long length = (isBigEndian ? in.readInt() : in.readIntLE()) & 0x0FFFFL;
            ByteBuf bodyBuf = in.readBytes((int) length);
            byte[] body = ByteBufUtil.getBytes(bodyBuf);
            ReferenceCountUtil.safeRelease(bodyBuf);

            ProtoBody protoBody = new ProtoBody();
            protoBody.setStart(start);
            protoBody.setHeader(header);
            protoBody.setType(type);
            protoBody.setVersion(version);
            protoBody.setId(id);
            protoBody.setLength(length);
            protoBody.setBody(body);
            list.add(protoBody);
        }

        return list;
    }

    /**
     * 抛弃非0x55776688开头的请求报文
     */
    private void discardingIfIllegalStart(ByteBuf in) {
        while (in.readableBytes() >= 4) {
            if (in.getByte(in.readerIndex()) != (byte) 0x55) {
                in.skipBytes(1);
                continue;
            }
            if (in.getByte(in.readerIndex() + 1) != (byte) 0x77) {
                in.skipBytes(2);
                continue;
            }
            if (in.getByte(in.readerIndex() + 2) != (byte) 0x66) {
                in.skipBytes(3);
                continue;
            }
            if (in.getByte(in.readerIndex() + 3) != (byte) 0x88) {
                in.skipBytes(4);
                continue;
            }
            break;
        }
    }

    /**
     * 获取当前请求报文的字节长度
     */
    private long getFrameLength(ByteBuf in, boolean isBigEndian) {
        long length = (isBigEndian ? in.getInt(in.readerIndex() + 8) : in.getIntLE(in.readerIndex() + 8)) & 0x0FFFFL;
        return length + 4 + 1 + 1 + 2 + 4;
    }

    /**
     * 报文是否是大端
     */
    private boolean isBigEndian(ByteBuf in) {
        byte header = in.getByte(in.readerIndex() + 4);
        return (byte) (header & 0x1) == 0x0;
    }
}
