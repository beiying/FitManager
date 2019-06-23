package com.beiying.fitmanager.framework.featureview;

import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;

/**
 * Created by beiying on 18/3/20.
 */

public interface BYFeatureCallback {
    /**
     * 视图返回前处理
     *
     * @param view
     */
    void beforeViewExit(View view);

    /**
     * 是否需要处理返回事件，不做任何逻辑处理
     * @return
     */
    boolean shouldConsumeKeyBack();

    /**
     * 处理返回键事件
     *
     * @return
     */
    void onKeyBack();

    /**
     * 处理菜单键事件
     *
     * @return
     */
    void onMenu();
    /**

     * 显示动画完成回调
     */
    void onShowAnimEnd();

    /**
     * 获取显示动画
     *
     * @return
     */
    Animation getShowAnimation(BYFeatureView featureView);

    /**
     * 获取隐藏动画
     *
     * @return
     */
    Animation getHideAnimation(BYFeatureView featureView);

    /**
     * 是否需要透明，显示背景
     * @return
     */
    boolean shouldTranslucent();

    /**
     * 添加到视图树
     */
    void onAddView();

    /**
     * 从视图树移除
     */
    void onRemoveView();

    /**
     * 输入法显示/隐藏时的底部padding
     */
    int getContentPaddingBottom(boolean keyboardVisible, int keyboardHeight);

    /**
     * 是否使用联动动画。如果为ture，则{@link #getShowAnimation(BYFeatureView)}、{@link #getHideAnimation(BYFeatureView)}和
     * {@link #shouldTranslucent()}不再起作用
     *
     * @return
     */
    boolean useConnectedAnim();

    /**
     * 如果使用联动动画，是否需要临时禁止联动操作
     * @return
     */
    boolean disableConnectedAnimTemporarily(final boolean keyboarding, final MotionEvent event, View view);

    /**
     * 滑动后弹回回调(未返回)
     */
    void onReset();
}
