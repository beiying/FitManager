package com.beiying.net.http;

import com.beiying.net.Utils;

import java.net.URI;

/**
 * Created by beiying on 19/4/7.
 */

public class HttpRequestProvider {
    private HttpRequestFactory mHttpRequestFactory;
    private static boolean OKHTTP_REQUEST = Utils.isExist("", HttpRequestProvider.class.getClassLoader());

    public HttpRequestProvider() {
        if (OKHTTP_REQUEST) {
            mHttpRequestFactory = new OkHttpRequestFactory();
        }
    }


    public HttpRequest getHttpRequest(URI uri, HttpMethod httpMethod) {
        return mHttpRequestFactory.createHttpRequest(uri, httpMethod);
    }
}
