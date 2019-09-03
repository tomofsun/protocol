package com.wakzz.common.decoder;

import com.wakzz.common.model.ProtoBody;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.ArrayList;
import java.util.List;

public class ProtoFrameDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
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
            // 报文长度错误,抛弃报文
            if (frameLength < 12) {
                in.skipBytes(4);
                continue;
            }
            // TODO 最大请求报文长度
            // TODO 等待超时后抛弃报文
            // 半包等待后续数据
            if (in.readableBytes() < frameLength) {
                break;
            }

            byte[] start = ByteBufUtil.getBytes(in.readBytes(4));
            byte header = in.readByte();
            byte type = in.readByte();
            int orderNo = (isBigEndian ? in.readShort() : in.readShortLE()) & 0xFFFF;
            in.skipBytes(4);
            long bodyLength = frameLength - 4 - 1 - 1 - 2 - 4;
            byte[] body = ByteBufUtil.getBytes(in.readBytes((int) bodyLength));

            ProtoBody protoBody = new ProtoBody();
            protoBody.setStart(start);
            protoBody.setHeader(header);
            protoBody.setType(type);
            protoBody.setOrderNo(orderNo);
            protoBody.setLength(frameLength);
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
            if (in.getByte(0) == (byte) 0x55 && in.getByte(1) == (byte) 0x77
                    && in.getByte(2) == (byte) 0x66 && in.getByte(3) == (byte) 0x88) {
                return;
            }
            in.skipBytes(4);
        }
    }

    /**
     * 获取当前请求报文的字节长度
     */
    private long getFrameLength(ByteBuf in, boolean isBigEndian) {
        return (isBigEndian ? in.getInt(8) : in.getIntLE(8)) & 0x0FFFFL;
    }

    /**
     * 报文是否是大端
     */
    private boolean isBigEndian(ByteBuf in) {
        byte header = in.getByte(4);
        return (byte) (header & 0x1) == 0x0;
    }
}
