package com.wakzz.common.coder;

import com.wakzz.common.context.ProtoVersion;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProtoCoderFactory {

    private static final Map<ProtoVersion, ProtoCoder> map = new ConcurrentHashMap<>();

    static {
        map.put(ProtoVersion.V1, new ProtoCoderV1());
    }

    public static ProtoCoder getProtoCoder(ProtoVersion protoVersion) {
        return map.get(protoVersion);
    }

}
