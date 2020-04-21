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
     * SSL认证-服务端返回客户端非对称加密公钥
     */
    SSL_Certificate((byte) 0x02),
    /**
     * SSL认证-客户端上送对称加密秘钥
     */
    SSL_ExchangeCipher((byte) 0x03),
    /**
     * 业务数据传输
     */
    Body((byte) 0x04);

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
