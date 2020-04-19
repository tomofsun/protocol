package com.wakzz.common.coder;

import com.wakzz.common.context.Constant;
import com.wakzz.common.context.ProtoType;
import com.wakzz.common.context.ProtoVersion;
import com.wakzz.common.exception.UnknownMagicException;
import com.wakzz.common.model.ProtoBody;
import com.wakzz.common.model.ProtoParams;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;

import java.util.ArrayList;
import java.util.List;

public class ProtoCoderV1 implements ProtoCoder {

    private static final ProtoVersion protoVersion = ProtoVersion.V1;

    @Override
    public byte[] encode(ProtoType protoType, byte[] body, ProtoParams protoParams) {
        ByteBuf out = Unpooled.buffer();
        encode(protoType, body, protoParams, out);
        return out.array();
    }

    @Override
    public void encode(ProtoType protoType, byte[] body, ProtoParams protoParams, ByteBuf out) {
        int bodyLength = body == null ? 0 : body.length;

        // 魔法数
        out.writeBytes(Constant.MAGIC_HEADER);
        // 版本号
        out.writeByte(protoVersion.getVersion());
        // 指令类型
        out.writeByte(protoType.getValue());
        // 数据序列化算法
        out.writeByte(protoParams.getProtoSerializer().getValue());
        // 预留字段
        out.writeByte((byte)0);
        // 报文长度
        out.writeInt(bodyLength);
        // 数据body
        if (body != null) {
            out.writeBytes(body);
        }
        // 校验和 TODO
        out.writeBytes(new byte[4]);
    }

    @Override
    public List<ProtoBody> decode(ByteBuf in) {
        List<ProtoBody> list = new ArrayList<>();
        // 循环读取解决粘包
        while (true) {
            // 完整报文至少16个字节
            // 半包等待后续数据
            if (in.readableBytes() < 16) {
                break;
            }

            // 如果报文开头不是0x55776688,则抛异常断开连接
            byte[] magicHeader = new byte[4];
            in.getBytes(in.readerIndex(), magicHeader);
            if (Constant.MAGIC_HEADER[0] != magicHeader[0] ||
                    Constant.MAGIC_HEADER[1] != magicHeader[1] ||
                    Constant.MAGIC_HEADER[2] != magicHeader[2] ||
                    Constant.MAGIC_HEADER[3] != magicHeader[3]) {
                throw new UnknownMagicException(magicHeader);
            }

            // 检查报文是否接受完整
            long frameLength = getFrameLength(in);
            // TODO 最大请求报文长度
            // TODO 等待超时后抛弃报文
            // 半包等待后续数据
            if (in.readableBytes() < frameLength) {
                break;
            }

            // 魔法数
            in.skipBytes(4);
            // 版本号
            byte version = in.readByte();
            // 指令类型
            byte type = in.readByte();
            // 数据序列化算法
            byte serializer = in.readByte();
            // 预留字段
            byte todo = in.readByte();
            // 报文长度
            int length = in.readInt();
            // 数据
            ByteBuf bodyBuffer = in.readBytes(length);
            byte[] body = ByteBufUtil.getBytes(bodyBuffer);
            ReferenceCountUtil.safeRelease(bodyBuffer);
            // 校验和
            ByteBuf checksumBuffer = in.readBytes(4);
            byte[] checksum = ByteBufUtil.getBytes(checksumBuffer);
            ReferenceCountUtil.safeRelease(checksumBuffer);

            ProtoBody protoBody = new ProtoBody();
            protoBody.setMagic(Constant.MAGIC_HEADER);
            protoBody.setVersion(version);
            protoBody.setType(type);
            protoBody.setSerializer(serializer);
            protoBody.setTodo(todo);
            protoBody.setLength(length);
            protoBody.setBody(body);
            protoBody.setChecksum(checksum);
            // TODO 校验和检查
            list.add(protoBody);
        }
        return list;
    }

    /**
     * 获取当前请求报文的字节长度
     */
    private long getFrameLength(ByteBuf in) {
        long length = in.getInt(in.readerIndex() + 8);
        return length + 16;
    }
}
