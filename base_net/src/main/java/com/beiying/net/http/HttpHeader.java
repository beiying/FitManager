package com.beiying.net.http;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by beiying on 19/4/7.
 */

public class HttpHeader implements NameValueMap<String,String> {
    private Map<String, String> mMap = new HashMap<>();


    @Override
    public String get(String key) {
        return null;
    }

    @Override
    public void set(String key, String value) {

    }

    @Override
    public void setAll(Map<String, String> map) {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(Object o) {
        return false;
    }

    @Override
    public boolean containsValue(Object o) {
        return false;
    }

    @Override
    public Object get(Object o) {
        return null;
    }

    @Override
    public Object put(Object o, Object o2) {
        return null;
    }

    @Override
    public Object remove(Object o) {
        return null;
    }

    @Override
    public void putAll(@NonNull Map map) {

    }

    @Override
    public void clear() {

    }

    @NonNull
    @Override
    public Set keySet() {
        return null;
    }

    @NonNull
    @Override
    public Collection values() {
        return null;
    }

    @NonNull
    @Override
    public Set<Entry> entrySet() {
        return null;
    }
}
