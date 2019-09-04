package com.wakzz.client;

import io.netty.channel.Channel;
import lombok.Data;

@Data
public class ProtoConnection implements Cloneable {

    private Channel channel;
    private ProtoConnectionManager connectionManager;

    public void close() throws InterruptedException {
        if (channel.isOpen()){
            channel.close().sync();
        }
    }

    public void release(){
        connectionManager.addConnection(this);
    }

}
