package com.wakzz.common.context;

/**
 * 协议版本
 */
public enum ProtoVersion {

    V1((byte) 0x00);

    private byte value;

    ProtoVersion(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static ProtoVersion valueOf(byte value) {
        ProtoVersion[] arrays = values();
        for (ProtoVersion version : arrays) {
            if (version.getValue() == value) {
                return version;
            }
        }
        return null;
    }

}
