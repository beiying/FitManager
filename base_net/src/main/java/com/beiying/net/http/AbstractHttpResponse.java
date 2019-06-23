package com.beiying.net.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * Created by beiying on 19/4/7.
 */

public abstract class AbstractHttpResponse implements HttpResponse {
    private static final String GZIP = "gzip";
    private InputStream mGzipInputStream;

    @Override
    public InputStream getBody() throws IOException {
        InputStream body = getBodyInternal();
        if (isGzip()) {
            return getBodyGzip(body);
        }
        return body;
    }

    protected abstract InputStream getBodyInternal();

    private InputStream getBodyGzip(InputStream body) throws IOException {
        if (this.mGzipInputStream == null) {
            this.mGzipInputStream = new GZIPInputStream(body);
        }
        return mGzipInputStream;
    }

    private boolean isGzip() {
        String contentEncoding = getHeaders().get("Content-Encoding");
        if (GZIP.equals(contentEncoding)) {
            return true;
        }
        return false;
    }
}
