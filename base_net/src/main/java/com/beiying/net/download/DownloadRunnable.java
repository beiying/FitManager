package com.beiying.net.download;

import android.os.Process;

import com.beiying.net.FileStorageManager;
import com.beiying.net.HttpManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.Response;

/**
 * Created by beiying on 19/4/7.
 */

public class DownloadRunnable implements Runnable {
    private long mStart;
    private long mEnd;
    private String mUrl;

    private DownloadCallback mCallback;

    public DownloadRunnable(String url, long start, long end, DownloadCallback callback) {
        mUrl = url;
        mStart = start;
        mEnd = end;
        mCallback = callback;
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        Response response = HttpManager.getInstance().syncRequestByRange(mUrl, mStart, mEnd);
        if (!response.isSuccessful()) {
            mCallback.onFailed(-1, "网络出问题了");
            return;
        }

        File file = FileStorageManager.getInstance().getFileByName(mUrl);
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rwd");
            randomAccessFile.seek(mStart);
            byte[] buffer = new byte[1024 * 500];
            int len;
            InputStream in = response.body().byteStream();
            while((len = in.read(buffer, 0, buffer.length)) != -1) {
                randomAccessFile.write(buffer, 0, len);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
