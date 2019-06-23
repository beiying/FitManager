package com.beiying.base.modules;

import java.lang.reflect.Method;

/**
 * Created by air on 2016/12/18.
 */

public class BYMethodInfo {
    public String methodName;
    public Method m;
    public boolean single;

    public BYMethodInfo(String methodName, Method m, boolean single) {
        this.methodName = methodName;
        this.m = m;
        this.single = single;
    }
}
