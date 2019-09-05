package com.wakzz.client;

import io.netty.channel.Channel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;

@Data
@Slf4j
public class ProtoConnection implements Closeable {

    private Channel channel;
    private ProtoConnectionManager connectionManager;

    public void close() {
        connectionManager.closeAndRemoveConnection(this);
    }

    public void release(){
        connectionManager.addConnection(this);
    }

}
