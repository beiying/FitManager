package com.beiying.net.http;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by beiying on 19/4/7.
 */

public abstract class AbstractHttpRequest implements HttpRequest {
    private static final String GZIP = "gzip";
    private HttpHeader mHeader = new HttpHeader();
    private ZipOutputStream mOutputStream;

    @Override
    public HttpHeader getHeaders() {
        return mHeader;
    }

    @Override
    public OutputStream getBody() {
        OutputStream body = getBodyOutStream();
        if (isGzip()) {
            mOutputStream = new ZipOutputStream(body);

        }
        return body;
    }

    private boolean isGzip() {
        String contentEncoding = getHeaders().get("Content-Encoding");
        if (GZIP.equals(contentEncoding)) {
            return true;
        }
        return false;
    }

    @Override
    public HttpResponse excute() throws IOException {
        if (mOutputStream != null) {
            mOutputStream.close();
        }
        HttpResponse response = executeInternal(mHeader);
        return response;
    }

    protected abstract HttpResponse executeInternal(HttpHeader header) throws IOException;

    protected abstract OutputStream getBodyOutStream();
}
