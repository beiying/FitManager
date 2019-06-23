package com.beiying.fitmanager;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

public class BYLifecycleManager {
    private static BYLifecycleManager sInstance;
    private Context mContext;
    private boolean isFront;


    public static BYLifecycleManager getInstacne() {
        if (sInstance == null) {
            synchronized (BYLifecycleManager.class) {
                sInstance = new BYLifecycleManager();
            }
        }
        return sInstance;
    }


    public void registerActivityLifecycleCallbacks() {
        BYApplication.sInstance.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                isFront = true;
            }

            @Override
            public void onActivityPaused(Activity activity) {
                isFront = false;
            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    public boolean isAppFront() {
        return isFront;
    }
}
