package com.wakzz.client;

import lombok.Data;

@Data
public class ProtoPoolConfig {

    /**
     * 连接池初始化时创建的连接数量
     */
    private int initialCount = 0;
    /**
     * 连接池最小闲置连接数量
     */
    private int minIdleConnectionCount = 0;
    /**
     * 连接池最大连接数量
     */
    private int maxConnectionCount = Integer.MAX_VALUE;
    /**
     * 申请连接时最大等待时间
     */
    private long connectTimeoutMillis = 5 * 1000L;
    /**
     * 发送请求后返回报文最大等待时间
     */
    private long readTimeoutMillis = 5 * 1000L;
    /**
     * 申请连接时是否发送心跳包检测连接是否有效
     */
    private boolean testOnBorrow = false;
    /**
     * 返还连接时是否发送心跳包检测连接是否有效
     */
    private boolean testOnReturn = false;
    /**
     * 是否开启心跳包
     */
    private boolean testWhileIdle = true;
    /**
     * 心跳包检测连接间隔时间
     */
    private int timeBetweenEvictionRunsSec = 60 * 1000;
    /**
     * 连接最大空闲时间
     * 连接空闲时间大于等于该值则断开连接
     */
    private long maxIdleTimeMillis = 30 * 60 * 1000L;
    /**
     * 是否开启SSL
     */
    private boolean enableSSL = false;

}
