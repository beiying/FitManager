package com.beiying.plugincore;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * 加载
 * */
public class ProxyActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //通过反射把插件的Activity对象化
        String className = getIntent().getStringExtra("className");
        try {
            Class<?> clazz = PluginManager.getInstance().getDexClassLoader().loadClass(className);
            Object newActivityInstance = clazz.newInstance();
            if (newActivityInstance instanceof PluginInterface) {
                PluginInterface pluginInterface = (PluginInterface) newActivityInstance;
                pluginInterface.attachPlugin(this);
                Bundle bundle = new Bundle();
                pluginInterface.onPluginCreate(bundle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Resources getResources() {
        return PluginManager.getInstance().getResources();
    }

    @Override
    public ClassLoader getClassLoader() {
        return PluginManager.getInstance().getDexClassLoader();
    }

    @Override
    public Resources.Theme getTheme() {
        Resources.Theme theme = PluginManager.getInstance().getResources().newTheme();
        theme.setTo(super.getTheme());
        return theme;
    }

    /**
     * 所有插件的Activity的跳转都必须先跳转到ProxyActivity
     * */
    @Override
    public void startActivity(Intent intent) {
        String className = intent.getStringExtra("className");
        Intent m = new Intent(this, ProxyActivity.class);
        m.putExtra("className", className);
        super.startActivity(intent);
    }
}
