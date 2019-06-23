package com.beiying.thirdlib;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Demo {

    public static void sendRequest(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = client.newCall(request).execute();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }


                @Override
                public void onResponse(Call call, Response response) throws IOException {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static void main(String args[]) {
        rangeRequest();
    }

    public static void formUpload() {
        OkHttpClient client = new OkHttpClient();
        HttpUrl httpUrl = HttpUrl.parse("http://www.baidu.com").newBuilder().addQueryParameter("", "").build();
        String url = httpUrl.toString();
        RequestBody body = RequestBody.create(MediaType.parse("image/jpeg"), new File(""));
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", "beiying")
                .addFormDataPart("", "", body).build();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("User_Agent", "from nate http")
                .post(multipartBody)
                .build();



    }

    public static void cacheRequest() {
        int maxCacheSize = 10 * 1024 *1024;
        Cache cache = new Cache(new File(""), maxCacheSize);
        OkHttpClient cacheClient = new OkHttpClient.Builder().cache(cache).build();//指定缓存，前提是服务器支持缓存
        Request cacheRequest = new Request.Builder()
                .url("")
                .cacheControl(new CacheControl.Builder().maxStale(365, TimeUnit.DAYS).build()).build();
        try {
            Response cacheResponse = cacheClient.newCall(cacheRequest).execute();
            cacheResponse.cacheResponse();
            cacheResponse.networkResponse();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void rangeRequest() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://b-gold-cdn.xitu.io/v3/static/img/weibo.8e2f5d6.svg")
                .addHeader("Range", "bytes=0-2")
                .build();
        try {
            Response response = client.newCall(request).execute();
            System.out.println("Content-Length：" + response.body().contentLength());
            if (response.isSuccessful()) {
                Headers headers = response.headers();
                for (int i = 0;i < headers.size();i++) {
                    System.out.println(headers.name(i) + ":" + headers.value(i));
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void threadPoolTest() {
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(2,4,60, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));

    }

    public static void initDb() {
    }
}
