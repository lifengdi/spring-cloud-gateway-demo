package com.lifengdi.gateway.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 守护线程池工厂类
 * @author: Li Fengdi
 * @date: 2020/3/13 17:35
 */
public class DaemonThreadFactory implements ThreadFactory {
    private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
    private final AtomicInteger threadNumber;
    private final String namePrefix;

    public DaemonThreadFactory() {
        this("pool");
    }

    public DaemonThreadFactory(String prefix) {
        this.threadNumber = new AtomicInteger(1);
        this.namePrefix = prefix + "-" + POOL_NUMBER.getAndIncrement() + "-thread-";
    }

    public Thread newThread(Runnable runnable) {
        Thread t = new Thread(runnable, this.namePrefix + this.threadNumber.getAndIncrement());
        t.setDaemon(true);
        return t;
    }
}