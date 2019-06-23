package com.beiying.module.video;

import android.app.Application;

import com.beiying.base.modules.BYModuleImpl;
import com.beiying.fitmanager.core.LeLog;

public class VideoModuleCreate implements BYModuleImpl {
    @Override
    public void onLoad(Application app) {
        for (int i=0;i<5;i++){
            LeLog.e("VideoCreate","VideoModule Loaded");
        }
    }
}
