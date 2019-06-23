package com.beiying.fitmanager;

import android.os.Process;
import android.support.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class BYThreadPoolManager {
    private static final int CPU_THREAD_MAX_COUNT = Math.max(2, Math.min(Runtime.getRuntime().availableProcessors() - 1, 4));
    private ThreadPoolExecutor mIOExecutor = new ThreadPoolExecutor(64, 64,
            30, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), sThreadFactory);
    private ThreadPoolExecutor mCpuExecutor = new ThreadPoolExecutor(CPU_THREAD_MAX_COUNT, CPU_THREAD_MAX_COUNT,
            30, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), sThreadFactory);
    private static ThreadFactory sThreadFactory = new ThreadFactory() {
        private AtomicInteger mThreadIndex = new AtomicInteger();
        @Override
        public Thread newThread(@NonNull Runnable r) {
            Thread thread = new Thread(r, "IOThread-" + mThreadIndex.getAndIncrement());
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            return thread;
        }
    };


    private static ExecutorService sService = Executors.newFixedThreadPool(5, new ThreadFactory() {
        private AtomicInteger mThreadIndex = new AtomicInteger();
        @Override
        public Thread newThread(@NonNull Runnable r) {
            Thread thread = new Thread(r, "BYThread-" + mThreadIndex.getAndIncrement());
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            return thread;
        }
    });

    public static ExecutorService getService() {
        return sService;
    }
}
