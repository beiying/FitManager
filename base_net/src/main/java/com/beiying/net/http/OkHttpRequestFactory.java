package com.beiying.net.http;

import java.net.URI;

import okhttp3.OkHttpClient;

/**
 * Created by beiying on 19/4/7.
 */

public class OkHttpRequestFactory implements HttpRequestFactory{

    private OkHttpClient mClient;

    public void setReadTimeOut(int readTimeOut) {

    }

    public void setWriteTimeout(int writeTimeout) {

    }

    @Override
    public HttpRequest createHttpRequest(URI uri, HttpMethod method) {
        return new OkHttpRequest(mClient, method, uri.toString());
    }
}
