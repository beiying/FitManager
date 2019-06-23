package com.beiying.net;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * Created by beiying on 19/4/7.
 */

public class FileStorageManager {

    private static FileStorageManager sInstance = null;
    private Context mContext;

    private FileStorageManager() {

    }

    public static FileStorageManager getInstance() {
        if (sInstance == null) {
            synchronized (FileStorageManager.class) {
                sInstance = new FileStorageManager();
            }
        }
        return sInstance;
    }

    public void init(Context context) {
        mContext = context;
    }

    public File getFileByName(String url) {
        File parent;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            parent = mContext.getExternalCacheDir();
        } else {
            parent = mContext.getCacheDir();
        }

        File file = new File(parent, CryptoUtils.md5(url));
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;

    }

}
