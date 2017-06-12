package com.beiying.fitmanager.utils;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Looper;

import com.beiying.fitmanager.BYBasicContainer;
import com.beiying.fitmanager.LeApplicationHelper;
import com.beiying.fitmanager.core.LeLog;


/**
 * 实时网速监控 
 * @author yuchenguang
 */
public class LeSpeedMonitor extends BYBasicContainer {

    private static final String PACKAGE_NAME = LeApplicationHelper.PACKAGE; // 应用程序包名
    private static final int SPEED_MONITOR_TIME_INTERVAL = 1000; // 采样时间间隔(ms)
    private static long mTotalTrafficKb;
    private static int mCurrentSpeedKbps = 0;
    private static boolean mIsRunning;
    
    public static void start() {
        if (mIsRunning) return;
        mIsRunning = true;
        mTotalTrafficKb = refreshTotalTrafficKb();
        postMonitorAction();
    }
    
    public static void stop() {
        mIsRunning = false;
    }
    
    private static void postMonitorAction() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                long newTotalTrafficKb = refreshTotalTrafficKb(); 
                int speedKbps = (int) ((newTotalTrafficKb - mTotalTrafficKb)*1000/SPEED_MONITOR_TIME_INTERVAL);
                mTotalTrafficKb = newTotalTrafficKb;
                if (0 <= speedKbps && speedKbps < 20000) { // 过滤掉异常网速值
                    mCurrentSpeedKbps = speedKbps;
                }
                if (mIsRunning) postMonitorAction(); // 继续下一次采样
            }
        }, SPEED_MONITOR_TIME_INTERVAL);
    }
    
    public static int getCurrentSpeed() {
        return mCurrentSpeedKbps;
    }
    
    private static long refreshTotalTrafficKb(){
        try {
            PackageManager pm = sApplication.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(PACKAGE_NAME, PackageManager.GET_ACTIVITIES);
            if (TrafficStats.getUidRxBytes(ai.uid) == TrafficStats.UNSUPPORTED) {
                return 0L;
            } else {
                return TrafficStats.getTotalRxBytes() / 1024; // 转换为Kb
            }
        } catch (NameNotFoundException e) {
        	LeLog.e(e);
            return 0L;
        }
    }
    
}
