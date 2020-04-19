package com.wakzz.common.coder;

import com.wakzz.common.context.ProtoType;
import com.wakzz.common.model.ProtoBody;
import com.wakzz.common.model.ProtoParams;
import io.netty.buffer.ByteBuf;

import java.util.List;

public interface ProtoCoder {

    byte[] encode(ProtoType protoType, byte[] body, ProtoParams protoParams);

    void encode(ProtoType protoType, byte[] body, ProtoParams protoParams, ByteBuf out);

    List<ProtoBody> decode(ByteBuf in);

}
