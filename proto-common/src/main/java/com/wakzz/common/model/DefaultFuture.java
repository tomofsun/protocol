package com.wakzz.common.model;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class DefaultFuture {

    private static final int DEFAULT_TIMEOUT = 1000;
    private static final ExecutorService executorService = Executors.newCachedThreadPool();
    private static final Map<Integer, DefaultFuture> FUTURES = new ConcurrentHashMap<>();

//    public static final Timer TIME_OUT_TIMER = new HashedWheelTimer(
//            new NamedThreadFactory("dubbo-future-timeout", true),
//            30,
//            TimeUnit.MILLISECONDS);

    private final int id;
    private final Channel channel;
    private final ProtoBody request;
    private final int timeout;
    private final Lock lock = new ReentrantLock();
    private final Condition done = lock.newCondition();
    private final long start = System.currentTimeMillis();
    private volatile ProtoBody response;
    private volatile Callback callback;

    private DefaultFuture(Channel channel, ProtoBody request, int timeout) {
        this.channel = channel;
        this.request = request;
        this.id = request.getId();
        this.timeout = timeout > 0 ? timeout : DEFAULT_TIMEOUT;
        FUTURES.put(this.id, this);
    }

//    private static void timeoutCheck(DefaultFuture future) {
//        TimeoutCheckTask task = new TimeoutCheckTask(future);
//        TIME_OUT_TIMER.newTimeout(task, future.getTimeout(), TimeUnit.MILLISECONDS);
//    }

    public static DefaultFuture newFuture(Channel channel, ProtoBody request, int timeout) {
        final DefaultFuture future = new DefaultFuture(channel, request, timeout);
//        timeoutCheck(future);
        return future;
    }


    public static void received(ProtoBody response) {
        DefaultFuture future = FUTURES.remove(response.getId());
        if (future != null) {
            future.doReceived(response);
        }
    }

    public void doSend() {
        channel.writeAndFlush(request);
    }

    private void doReceived(ProtoBody res) {
        lock.lock();
        try {
            this.response = res;
            done.signalAll();
        } finally {
            lock.unlock();
        }
        if (callback != null) {
            invokeCallback(callback);
        }
    }

    public void setCallback(Callback callback) {
        if (isDone()) {
            invokeCallback(callback);
        } else {
            boolean isdone = false;
            lock.lock();
            try {
                if (!isDone()) {
                    this.callback = callback;
                } else {
                    isdone = true;
                }
            } finally {
                lock.unlock();
            }
            if (isdone) {
                invokeCallback(callback);
            }
        }
    }

    private void invokeCallback(Callback callback) {
        executorService.execute(() -> {
            try {
                if (System.currentTimeMillis() - start > timeout) {
                    callback.onFailure(request, new TimeoutException());
                    return;
                }
                callback.onSuccess(request, response);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    public ProtoBody get() {
        return get(timeout);
    }

    public ProtoBody get(int timeout) {
        if (timeout <= 0) {
            timeout = 1000;
        }
        if (!isDone()) {
            long start = System.currentTimeMillis();
            lock.lock();
            try {
                while (!isDone()) {
                    done.await(timeout, TimeUnit.MILLISECONDS);
                    if (isDone() || System.currentTimeMillis() - start > timeout) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
            if (!isDone()) {
                throw new RuntimeException(new TimeoutException());
            }
        }
        return response;
    }

    public boolean isDone() {
        return response != null;
    }

//    private static class TimeoutCheckTask implements TimerTask {
//
//        private DefaultFuture future;
//
//        TimeoutCheckTask(DefaultFuture future) {
//            this.future = future;
//        }
//
//        @Override
//        public void run(Timeout timeout) {
//            if (future == null || future.isDone()) {
//                return;
//            }
//            // create exception response.
//            Response timeoutResponse = new Response(future.getId());
//            // set timeout status.
//            timeoutResponse.setStatus(future.isSent() ? Response.SERVER_TIMEOUT : Response.CLIENT_TIMEOUT);
//            timeoutResponse.setErrorMessage(future.getTimeoutMessage(true));
//            // handle response.
//            DefaultFuture.received(future.getChannel(), timeoutResponse);
//
//        }
//    }

}
