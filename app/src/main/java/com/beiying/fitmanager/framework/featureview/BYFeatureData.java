package com.beiying.fitmanager.framework.featureview;

/**
 * Created by beiying on 18/3/20.
 */

public class BYFeatureData {
    public BYFeatureCallback mCallback;
    public boolean mShouldLockScreen;
    public Object mData;

    public BYFeatureData(BYFeatureCallback callback, boolean shouldLockScreen, Object data) {
        mCallback = callback;
        mShouldLockScreen = shouldLockScreen;
        mData = data;
    }
}
