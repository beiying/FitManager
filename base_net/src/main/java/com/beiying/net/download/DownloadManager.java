package com.beiying.net.download;

import android.support.annotation.NonNull;

import com.beiying.net.HttpManager;

import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by beiying on 19/4/7.
 */

public class DownloadManager {
    private static final int CORE_THREAD = 2;
    private static final int MAX_THREAD = 2;
    private static final int THREAD_TIME_OUT = 60;
    private static DownloadManager sInstance;
    private HashSet<DownloadTask> mTasks;
    private ThreadPoolExecutor mThreadPool;

    private DownloadManager() {

    }

    public static DownloadManager getInstance() {
        if (sInstance == null) {
            synchronized (DownloadManager.class) {
                sInstance = new DownloadManager();
            }
        }
        return sInstance;
    }


    public void init(DownloadConfig config) {
        mThreadPool = new ThreadPoolExecutor(config.getCoreThreadSize(), config.getMaxThreadSize(), THREAD_TIME_OUT, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new ThreadFactory() {
            private AtomicInteger mNumber = new AtomicInteger(1);

            @Override
            public Thread newThread(@NonNull Runnable runnable) {
                Thread thread = new Thread(runnable, "downalod thread #" + mNumber.getAndIncrement());

                return thread;
            }
        });
    }
    public void finishTask(DownloadTask task) {
        mTasks.remove(task);
    }

    public void download(final String url , final DownloadCallback callback) {

        final DownloadTask downloadTask = new DownloadTask(url, callback);
        if (mTasks.contains(downloadTask)) {
            callback.onFailed(-1, "任务已经执行了");
            return;
        }
        mTasks.add(downloadTask);

        HttpManager.getInstance().asyncRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                finishTask(downloadTask);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {

                }
                long length = response.body().contentLength();
                if (length <= -1) {
                    if (callback != null) {
                        callback.onFailed(-1, "content lenght -1");
                    }
                    return;
                }

                processDownload(url, length, callback);
            }
        });
    }

    private void processDownload(String url, long length, DownloadCallback callback) {
        long threadDownloadSize = length / MAX_THREAD;
        for (int i = 0;i < MAX_THREAD;i++) {
            long start = i * threadDownloadSize;
            long end = 0;
            if (i == MAX_THREAD - 1) {
                end = length - 1;
            } else {
                end = (i + 1) * threadDownloadSize - 1;
            }
            mThreadPool.execute(new DownloadRunnable(url, start, end, callback));
        }
    }

}
