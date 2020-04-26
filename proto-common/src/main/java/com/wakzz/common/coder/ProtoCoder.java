package com.wakzz.common.coder;

import com.wakzz.common.context.ProtoSerializer;
import com.wakzz.common.context.ProtoType;
import com.wakzz.common.model.ProtoBody;
import io.netty.buffer.ByteBuf;

import java.util.List;

public interface ProtoCoder {

    byte[] encode(ProtoType protoType, ProtoSerializer protoSerializer, byte[] body);

    void encode(ProtoType protoType, ProtoSerializer protoSerializer, byte[] body, ByteBuf out);

    List<ProtoBody> decode(ByteBuf in);

}
