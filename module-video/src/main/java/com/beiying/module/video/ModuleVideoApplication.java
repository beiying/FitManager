package com.beiying.module.video;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;

public class ModuleVideoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if(BuildConfig.DEBUG) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(this);
    }
}
