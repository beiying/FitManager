package com.beiying.fitmanager;

import com.beiying.fitmanager.core.LeLog;

/**
 * Created by beiying on 2019/6/12.
 */

public class BYLaunchTimer {
    private static  long sTime;

    public static void startRecord() {
        sTime = System.currentTimeMillis();
    }

    public static void endRecord() {
        endRecord("");
    }

    public static void endRecord(String tag) {
        long cost = System.currentTimeMillis() - sTime;
        LeLog.e(tag + "cost:" + cost);
    }
}
