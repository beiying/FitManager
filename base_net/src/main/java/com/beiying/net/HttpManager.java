package com.beiying.net;

import android.content.Context;

import com.beiying.net.download.DownloadCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by beiying on 19/4/7.
 */

public class HttpManager {
    private static HttpManager sInstance;
    private OkHttpClient mClient;
    private Context mContext;

    private HttpManager() {
        mClient = new OkHttpClient();
    }

    public static HttpManager getInstance() {
        if (sInstance == null) {
            synchronized (HttpManager.class) {
                sInstance = new HttpManager();
            }
        }
        return sInstance;
    }

    public void init(Context context) {
        this.mContext = context;
    }

    public Response syncRequestByRange(String url, long start, long end) {
        Request request = new Request.Builder()
                .addHeader("Range", "bytes=" + start + "-" + end)
                .url(url).build();
        Response response = null;
        try {
            response = mClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public void asyncRequest(String url ,Callback callback) {
        Request request = new Request.Builder().url(url).build();
        mClient.newCall(request).enqueue(callback);
    }

    public void asyncRequest(final String url, final DownloadCallback callback) {
        Request request = new Request.Builder().url(url).build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    if (callback != null) {
                        callback.onFailed(-1, "");
                    }
                    return;
                }
                File file = FileStorageManager.getInstance().getFileByName(url);
                byte[] buffer = new byte[1024 * 500];
                int len;
                FileOutputStream out = new FileOutputStream(file);
                InputStream in = response.body().byteStream();
                while((len = in.read(buffer, 0, buffer.length)) != -1) {
                    out.write(buffer);
                    out.flush();
                }

                if (callback != null) {
                    callback.onSuccess(file);
                }
            }
        });
    }
}
