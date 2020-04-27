package com.wakzz.common.context;

/**
 * 报文类型
 */
public enum ProtoType {

    /**
     * 心跳包请求ping
     */
    Ping((byte) 0x00),
    /**
     * 心跳包返回pong
     */
    Pong((byte) 0x01),
    /**
     * 业务数据传输
     */
    Body((byte) 0x02);

    private byte value;

    ProtoType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static ProtoType valueOf(byte value) {
        ProtoType[] arrays = values();
        for (ProtoType type : arrays) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
