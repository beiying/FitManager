package com.beiying.plugincore;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

public class BaseActivity extends Activity implements PluginInterface {
    //代理Activity，所有插件中用到上下文的地方都是用代理Activity的上下文来代替
    protected Activity that;
    @Override
    public void attachPlugin(Activity proxy) {
        this.that = proxy;
    }

    @Override
    public void setContentView(View view) {
        if (that == null) {
            //功能是用来安装的
            super.setContentView(view);
        } else {
            //功能是用于宿主应用
            that.setContentView(view);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        that.setContentView(layoutResID);
    }

    @Override
    public <T extends View> T findViewById(int id) {
        return that.findViewById(id);
    }

    @Override
    public Intent getIntent() {
        return that.getIntent();
    }

    @Override
    public ClassLoader getClassLoader() {
        return that.getClassLoader();
    }

    @NonNull
    @Override
    public LayoutInflater getLayoutInflater() {
        return that.getLayoutInflater();
    }

    @Override
    public Resources.Theme getTheme() {
        return that.getTheme();
    }

    @Override
    public void startActivity(Intent intent) {
        Intent m = new Intent();
        m.putExtra("className", intent.getComponent().getClassName());
        that.startActivity(m);
    }

    @Override
    public void onPluginCreate(Bundle savedInstanceState) {

    }

    @Override
    public void onPluginStart() {

    }

    @Override
    public void onPluginResume() {

    }

    @Override
    public void onPluginPause() {

    }

    @Override
    public void onPluginStop() {

    }

    @Override
    public void onPluginDestroy() {

    }

    @Override
    public void onPluginSaveInstanceState(Bundle outState) {

    }

    @Override
    public boolean onPluginTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public void onPluginBackPressed() {

    }
}
