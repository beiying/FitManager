package com.beiying.fitmanager;

import android.Manifest;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BYNetworkUtil {
    private Context mContext;

    public void getNetStats(int netType, long starTime, long endTime) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        long netDataRx = 0, netDataTx = 0;
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        NetworkStatsManager networkStatsManager = (NetworkStatsManager) mContext.getSystemService(Context.NETWORK_STATS_SERVICE);
        NetworkStats networkStats = null;
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
        try {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            networkStats = networkStatsManager.querySummary(netType, telephonyManager.getSubscriberId(), starTime, endTime);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        while(networkStats.hasNextBucket()) {
            networkStats.getNextBucket(bucket);
            int uid = bucket.getUid();
            if (getUidByPackageName() == uid) {
                netDataRx += bucket.getRxBytes();
                netDataTx += bucket.getTxBytes();
            }
        }
    }

    public void scheduleNetworkTask() {
        Executors.newSingleThreadScheduledExecutor().schedule(new Runnable() {
            @Override
            public void run() {
                getNetStats(NetworkCapabilities.TRANSPORT_WIFI, System.currentTimeMillis() - 30000, System.currentTimeMillis());

            }
        }, 30, TimeUnit.SECONDS);
    }

    private int getUidByPackageName() {
        return 0;
    }
}
