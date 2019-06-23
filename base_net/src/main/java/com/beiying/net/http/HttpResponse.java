package com.beiying.net.http;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by beiying on 19/4/7.
 */

public interface HttpResponse extends Header, Closeable {

    HttpStatus getStatus();

    String getStatusMsg();

    InputStream getBody() throws IOException;

    void close() throws IOException;
}
