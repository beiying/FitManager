package com.beiying.base.modules;

import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.ArrayMap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by cangwang on 2016/12/26.
 */

public class BYModuleManager {
    private List<String> modules = new ArrayList<>();   //模块名字
    protected ArrayMap<String, BYAbsModule> allModules = new ArrayMap<>();   //模块实体

    public List<String> getModuleNames(){
        return modules;
    }

    public void moduleConfig(List<String> modules) {
        this.modules = modules;
    }

    private Handler handler;
    private ExecutorService pool;

    public Handler getHandler(){
        if (handler == null){
            handler = new Handler(Looper.getMainLooper());
        }
        return handler;
    }

    public ExecutorService getPool(){
        if (pool ==null){
            pool = Executors.newSingleThreadExecutor();
        }
        return pool;
    }

    public BYAbsModule getModuleByNames(String name){
        if (!BYModuleUtil.empty(allModules))
            return allModules.get(name);
        return null;
    }

    public void remove(String name){
        if (!BYModuleUtil.empty(allModules)){
            allModules.remove(name);
        }
    }

    public void putModule(String name, BYAbsModule module){
        allModules.put(name,module);
    }

    public void onResume(){
        for (BYAbsModule module:allModules.values())
            if (module !=null)
                module.onResume();
    }

    public void onPause(){
        for (BYAbsModule module:allModules.values())
            if (module !=null)
                module.onPause();
    }

    public void onStop(){
        for (BYAbsModule module:allModules.values())
            if (module !=null)
                module.onStop();
    }

    public void onConfigurationChanged(Configuration newConfig){
        for (BYAbsModule module:allModules.values())
            if (module!=null)
                module.onOrientationChanges(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE);
    }

    public void onDestroy(){
        handler = null;
        pool=null;
        for (BYAbsModule module:allModules.values()) {
            if (module != null) {
                module.onDestroy();
            }
        }
    }
}
