package com.wakzz.client;

import com.wakzz.common.model.ProtoBody;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.util.concurrent.*;

@Slf4j
public class ProtoTemplate implements Closeable {

    private ExecutorService executorService;
    private ProtoConnectionManager connectionManager;

    public ProtoTemplate(String host, int port) {
        this(new ProtoConnectionManager(host, port));
    }

    public ProtoTemplate(ProtoConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        this.executorService = Executors.newCachedThreadPool();
    }

    public ProtoBody sendSyncRequest(ProtoBody request) throws Exception {
        ProtoConnection connection = null;
        try {
            connection = connectionManager.getConnection();
            Channel channel = connection.getChannel();
            ArrayBlockingQueue<ProtoBody> queue = new ArrayBlockingQueue<>(1);
            channel.pipeline().addLast(SimpleChannelInboundHandler.class.getName(), new SimpleChannelInboundHandler<ProtoBody>() {
                @Override
                protected void channelRead0(ChannelHandlerContext ctx, ProtoBody protoBody) {
                    channel.pipeline().remove(this);
                    queue.add(protoBody);
                }
            });
            channel.writeAndFlush(request);
            ProtoBody response = queue.poll(5, TimeUnit.SECONDS);
            if (response == null) {
                throw new TimeoutException();
            }
            connection.release();
            return response;
        } catch (Exception e) {
            if (connection != null) {
                connection.close();
            }
            throw e;
        }
    }

    public void sendAsyncRequest(ProtoBody request, Callback callback) {
        executorService.execute(() -> {
            try {
                ProtoBody response = sendSyncRequest(request);
                callback.onSuccess(request, response);
            } catch (Exception e) {
                callback.onFailure(request, e);
            }
        });
    }

    @Override
    public void close() {
        this.connectionManager.close();
        this.executorService.shutdown();
    }
}
