package com.beiying.fitmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;

public class BYWakeLockUtils {
    private static PowerManager.WakeLock sWakeLock;

    public static void aquire(Context context) {
        if (sWakeLock == null) {
            sWakeLock = createWakeLock(context);
        }
        if (sWakeLock != null && !sWakeLock.isHeld()) {
            sWakeLock.acquire();
        }
    }

    public static void release() {
        if (sWakeLock != null && sWakeLock.isHeld()) {
            sWakeLock.release();
            sWakeLock = null;
        }
    }

    @SuppressLint("InvalidWakeLockTag")
    private static PowerManager.WakeLock createWakeLock(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            return powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        }
        return null;
    }
}
