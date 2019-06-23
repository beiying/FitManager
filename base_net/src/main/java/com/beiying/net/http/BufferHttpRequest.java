package com.beiying.net.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by beiying on 19/4/7.
 */

public abstract class BufferHttpRequest extends AbstractHttpRequest{
    private ByteArrayOutputStream mByteArray = new ByteArrayOutputStream();

    @Override
    protected OutputStream getBodyOutStream() {
        return mByteArray;
    }

    @Override
    protected HttpResponse executeInternal(HttpHeader header) throws IOException {
        byte[] data = mByteArray.toByteArray();
        return executeInternal(header, data);
    }

    protected abstract HttpResponse executeInternal(HttpHeader header, byte[] data) throws IOException;
}
