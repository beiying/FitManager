package com.beiying.net.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

/**
 * Created by beiying on 19/4/7.
 */

public interface HttpRequest extends Header{

    HttpMethod getMethod();
    URI getUri();
    OutputStream getBody();
    HttpResponse excute() throws IOException;
}
