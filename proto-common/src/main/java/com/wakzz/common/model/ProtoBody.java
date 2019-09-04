package com.wakzz.common.model;

import lombok.Data;

@Data
public class ProtoBody {

    /**
     * 报文头(4字节)
     * 固定为0x55776688
     */
    private byte[] start;
    /**
     * 报文特殊信息(1字节)
     * 从右向左第1个bit位 - 0: 大端; 1: 小端
     */
    private byte header;
    /**
     * 报文类型(1字节)
     * 0: 心跳包ping
     * 1: 心跳包pong
     * 2: body为二进制的数据包
     * 3: body为string的数据表
     */
    private byte type;
    /**
     * 版本号(2字节)
     * unsigned short,由于java不支持unsigned,通过int表示
     */
    private int version;
    /**
     * body报文长度(4字节)
     * unsigned int,由于java不支持unsigned,通过long表示
     */
    private long length;
    /**
     * 报文内容
     */
    private byte[] body;
}
