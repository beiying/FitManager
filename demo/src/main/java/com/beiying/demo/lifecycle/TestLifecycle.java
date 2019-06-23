package com.beiying.demo.lifecycle;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.os.Handler;

/**
 * Created by beiying on 19/2/27.
 */

public class TestLifecycle implements LifecycleObserver {
    private boolean enable = false;
    private Lifecycle lifecycle;

    public TestLifecycle(Context context, Lifecycle lifecycle, Handler.Callback callback) {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void start() {

    }

    public void enable() {
        enable = true;
        if (lifecycle.getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {

        }
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void stop() {

    }
}
