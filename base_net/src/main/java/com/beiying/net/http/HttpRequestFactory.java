package com.beiying.net.http;

import java.net.URI;

/**
 * Created by beiying on 19/4/7.
 */

public interface HttpRequestFactory {
    HttpRequest createHttpRequest(URI uri, HttpMethod method);
}
