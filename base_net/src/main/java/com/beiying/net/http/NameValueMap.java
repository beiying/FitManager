package com.beiying.net.http;

import java.util.Map;

/**
 * 跟访问HTTP请求头和响应头有关
 * Created by beiying on 19/4/7.
 */

public interface NameValueMap<S, S1> extends Map {
    String get(String key);

    void set(String key, String value);

    void setAll(Map<String, String> map);
}
