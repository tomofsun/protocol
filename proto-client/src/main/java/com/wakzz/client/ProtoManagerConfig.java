package com.wakzz.client;

import lombok.Data;

@Data
public class ProtoManagerConfig {

    private int initialCount = 0;
    private int minConnectionCount = 0;
    private int maxConnectionCount = Integer.MAX_VALUE;
    private long maxWait = 0L;
    private boolean testOnBorrow = false;
    private boolean testOnReturn = false;



}
