package com.beiying.plugincore;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;


/**
 * 所有插件APK中的Activity必须实现的接口类，这个类是规定所有插件化框架的标准
 * */
public interface PluginInterface {
    public void attachPlugin(Activity proxy);
    public void onPluginCreate(Bundle savedInstanceState);
    public void onPluginStart();
    public void onPluginResume();
    public void onPluginPause();
    public void onPluginStop();
    public void onPluginDestroy();
    public void onPluginSaveInstanceState(Bundle outState);
    public boolean onPluginTouchEvent(MotionEvent event);
    public void onPluginBackPressed();
}
