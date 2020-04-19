package com.wakzz.common.context;

/**
 * 协议版本
 */
public enum ProtoVersion {

    V1((byte) 0x00);

    private byte version;

    ProtoVersion(byte version) {
        this.version = version;
    }

    public byte getVersion() {
        return version;
    }

    public static ProtoVersion valueOf(byte value) {
        ProtoVersion[] arrays = values();
        for (ProtoVersion version : arrays) {
            if (version.getVersion() == value) {
                return version;
            }
        }
        return null;
    }

}
