package com.beiying.demo;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by beiying on 19/3/2.
 */

public class CommonUtil {
    private static CommonUtil sInstance;
    private Context mContext;

    private CommonUtil(Context context) {
        mContext = context;
    }

    public static CommonUtil getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new CommonUtil(context);
        }
        return sInstance;
    }

    public void showToast() {
        Toast.makeText(mContext, "加油，曼联", Toast.LENGTH_LONG).show();
    }
}
