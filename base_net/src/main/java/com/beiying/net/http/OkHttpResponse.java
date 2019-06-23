package com.beiying.net.http;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Response;

/**
 * Created by beiying on 19/4/7.
 */

public class OkHttpResponse extends AbstractHttpResponse {
    private Response mResponse;
    private HttpHeader mHeaders;

    public OkHttpResponse(Response response) {
        mResponse = response;
    }

    @Override
    public HttpHeader getHeaders() {
        if (mHeaders == null) {
            mHeaders = new HttpHeader();
        }
        for (String name : mResponse.headers().names()) {
            mHeaders.put(name, mResponse.headers().get(name));
        }
        return mHeaders;
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.getValue(mResponse.code());
    }

    @Override
    public String getStatusMsg() {
        return mResponse.message();
    }

    @Override
    public void close() throws IOException {
        mResponse.body().close();
    }

    @Override
    protected InputStream getBodyInternal() {
        return mResponse.body().byteStream();
    }
}
