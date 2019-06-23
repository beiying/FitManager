package com.beiying.plugincore;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.io.File;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

/**
 * 插件的加载类，用来加载插件的资源：Resource、Activity、
 */
public class PluginManager {
    private static PluginManager sIntance;
    private DexClassLoader mDexClassLoader;//用于加载插件apk中的dex文件
    private Resources mResources;//用于加载插件apk中的资源文件
    private Context mContext;//上下文
    private PackageInfo mPackageInfo;//要加载的插件apk的包信息


    private PluginManager() {

    }

    public void init(Context context) {
        mContext = context;
    }

    public static PluginManager getInstance() {
        if (sIntance == null) {
            synchronized (PluginManager.class) {
                sIntance = new PluginManager();
            }
        }
        return sIntance;
    }

    public void loadPath(String path) {
        //获取当前应用内部的私有存储路径，即当前应用apk的存储路径
        File dexOutFile = mContext.getDir("dex", Context.MODE_PRIVATE);
        mDexClassLoader = new DexClassLoader(path, dexOutFile.getAbsolutePath(), null, mContext.getClassLoader());

        PackageManager packageManager = mContext.getPackageManager();
        mPackageInfo = packageManager.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);

        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = AssetManager.class.getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, path);

            mResources = new Resources(assetManager, mContext.getResources().getDisplayMetrics(),
                    mContext.getResources().getConfiguration());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public DexClassLoader getDexClassLoader() {
        return mDexClassLoader;
    }

    public Resources getResources() {
        return mResources;
    }

    public PackageInfo getPackageInfo() {
        return mPackageInfo;
    }
}
