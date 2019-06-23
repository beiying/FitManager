package com.beiying.base.modules;

import android.os.Bundle;

/**
 * Created by cangwang on 2016/12/26.
 */

public abstract class BYAbsModule {

    public abstract boolean init(BYModuleContext moduleContext, Bundle extend);

    public abstract void onSaveInstanceState(Bundle outState);

    public abstract void onStart();

    public abstract void onResume();

    public abstract void onPause();

    public abstract void onStop();

    public abstract void onOrientationChanges(boolean isLandscape);

    public abstract void onDestroy();

    public abstract void detachView();

    public abstract void setVisible(boolean visible);
}
