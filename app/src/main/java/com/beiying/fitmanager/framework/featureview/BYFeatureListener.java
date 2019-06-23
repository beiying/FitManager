package com.beiying.fitmanager.framework.featureview;

import android.view.View;

import com.beiying.fitmanager.core.BYSafeRunnable;

/**
 * Created by beiying on 18/3/20.
 */

public interface BYFeatureListener {
    void onContainerTriggered(View top, FeatureAnimController.AnimViewSet secondTop);
    void onContainerScrolled(float ratio, View top, FeatureAnimController.AnimViewSet secondTop);
    void onContainerScrollEnd(boolean shouldBack, View top, FeatureAnimController.AnimViewSet secondTop);
    void beforeConnectedAnim();
    void afterConnectedAnim();

    void beforeTrigger(View top, BYSafeRunnable runnable);
    void afterScroll(View top, BYSafeRunnable runnable);
}
