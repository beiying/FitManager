package com.beiying.fitmanager.framework.featureview;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.beiying.fitmanager.R;
import com.beiying.fitmanager.core.BYSafeRunnable;
import com.beiying.fitmanager.core.LeLog;
import com.beiying.fitmanager.core.ui.LeGallery;
import com.beiying.fitmanager.core.ui.LeUI;
import com.beiying.fitmanager.core.utils.LeMachineHelper;
import com.beiying.fitmanager.framework.BYControlCenter;

/**
 * FeatureView中子View的容器
 *
 * Created by beiying on 18/3/21.
 */

public class BYViewContainer extends LeGallery {
    private static final boolean DEBUG_STATE = false;
    private static final boolean DEBUG = false;

    private FrameLayout mTransluncentView;
    private BYFeatureView mFeatureView;
    private View mTargetView;
    private BYFeatureCallback mCallback;

    private BYFeatureView.STATES mState = BYFeatureView.STATES.RESET;

    private FeatureAnimController.AnimViewSet mSecondTopSet;
    boolean mMotionEnd;
    boolean mIsTouching;

    public BYViewContainer(BYFeatureView featureView, View targetView, BYFeatureCallback callback, BYViewContainer secondTop) {
        super(featureView.getContext(), new DecelerateInterpolator(1.5f));
        mFeatureView = featureView;
        mCallback = callback;

        forbitDriftOver();

        if (mFeatureView.canAnimateConnected(callback)) {
            connectedInit(mFeatureView.getContext(), secondTop);
            freeze();
        }

        addTarget(targetView);

        setContentDescription("feature layer");
    }

    private BYViewContainer addTarget(View targetView) {
        mTargetView = targetView;
        targetView.setVisibility(View.VISIBLE);
        addView(LeUI.removeFromParent(mTargetView));

        setDefaultScreen(getChildCount() - 1);

        return this;
    }


    public View getTargetView() {
        return mTargetView;
    }
    private boolean mKeyboarding;
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mFeatureView.isAnimating()) {
            return true;
        }
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mKeyboarding = mFeatureView.isKeyboardShowing();
        } else if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
            mKeyboarding = false;
        }
        boolean disallow = false;
        /**
         * 在使用联动动画的情况下，Container会统一处理Touch事件。
         * 存在以下几种情况，Container不处理Touch事件：
         * 1. 输入键盘存在时；
         * 2. 不使用联动动画；
         * 3. 使用联动动画但临时禁止联动动画处理
         * 4. TargetView需要优先处理Touch事件时
         */
        if (mKeyboarding || ev.getPointerCount() > 1
                || (mCallback != null && (!mCallback.useConnectedAnim() || mCallback.useConnectedAnim()
                && mCallback.disableConnectedAnimTemporarily(mKeyboarding, ev, mTargetView)))) {
            disallow = true;
        }
        requestDisallowInterceptTouchEvent(disallow);

        if (ev.getAction() == MotionEvent.ACTION_DOWN && !isScrolling()) {
            mMotionEnd = false;
            logState("motionend:" + mMotionEnd);
        } else if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
            mMotionEnd = true;
            checkStateOnTouchEnd();
            logState("motionend:" + mMotionEnd);
        }
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mIsTouching = true;
        } else if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
            mIsTouching = false;
        }
        boolean res = super.dispatchTouchEvent(ev);
        return res;
    };

    private boolean inTransluncentArea(float motionX) {
        return motionX < (mTransluncentView.getMeasuredWidth() + 20 - getScrollX());
    }

    private void checkStateOnTouchEnd() {
        logState("scrolling:" + isScrolling());
        logState("scrolled:" + getScrollX());
        if (isResetState()) {
            scrollEndAction(false);
        }
    }

    private boolean isResetState() {
        if (mTransluncentView == null) {
            return false;
        }
        return getScrollX() == mTransluncentView.getMeasuredWidth();
    }

    private void connectedInit(Context context, BYViewContainer secondTop) {
        mTransluncentView = new FrameLayout(context);
        try {
            if (LeMachineHelper.isMachineAPad()) {
                mTransluncentView.setBackgroundColor(0x0);
            } else {
                mTransluncentView.setBackgroundResource(R.drawable.feature_container_bg);
            }
        } catch (Exception e) {
            LeLog.e(e);
            //find exception create bitmap width and height must be > 0
        }
        mTransluncentView.setClickable(false);
        addView(mTransluncentView);

        mSecondTopSet = secondTop == null ? null : new FeatureAnimController.AnimViewSet(secondTop);

        setFocusableInTouchMode(true);

        addGalleryChangeListener(new LeGalleryAdapter() {

            private boolean mResetCalled;

            @Override
            public void onXChange(int delta) {
                super.onXChange(delta);
                logState("state:" + mState);

                if (mState == BYFeatureView.STATES.RESET) {
                    mState = BYFeatureView.STATES.START;
                } else if (mState == BYFeatureView.STATES.START) {
                    mState = BYFeatureView.STATES.SCROLLING;
                }
                if (mState == BYFeatureView.STATES.START) {
                    if (mFeatureView.getFeatureListener() != null) {
                        mFeatureView.getFeatureListener().onContainerTriggered(BYViewContainer.this, mSecondTopSet);
                    }
                    mResetCalled = false;
                }
            }

            @Override
            public void onGalleryScrolled(int l, int t, int oldl, int oldt) {
                super.onGalleryScrolled(l, t, oldl, oldt);
                final boolean fMotionEnd = mMotionEnd;
                postScroll(l, fMotionEnd);
            }

            private void postScroll(final int l, final boolean motionEnd) {
                BYControlCenter.getInstance().postToUiThread(new BYSafeRunnable() {

                    @Override
                    public void runSafely() {
                        if (mTransluncentView == null) {
                            return;
                        }

                        if (mState == BYFeatureView.STATES.START) {
                            mState = BYFeatureView.STATES.SCROLLING;
                        }
                        float ratio = 0f;
                        if (mFeatureView.getAnimController().mEnableScrollRatio) {
                            int scrolled = mTransluncentView.getMeasuredWidth() - l;
                            ratio = (float) scrolled / mTransluncentView.getMeasuredWidth() * 3f / 2;
                            if (mState == BYFeatureView.STATES.SCROLLING) {
                                if (mTransluncentView != null) {
                                    mTransluncentView.setAlpha(1 - ratio);
                                }
                                if (mFeatureView.getFeatureListener() != null) {
                                    mFeatureView.getFeatureListener().onContainerScrolled(ratio, BYViewContainer.this, mSecondTopSet);
                                }
                            }
                        }
                        //LeLog.i("CW", "State: " + mState + " " + motionEnd);
                        if (mState == BYFeatureView.STATES.SCROLLING && motionEnd) {
                            if (!mFeatureView.getAnimController().mEnableScrollRatio) {
                                int scrolled = mTransluncentView.getMeasuredWidth() - l;
                                ratio = (float) scrolled / mTransluncentView.getMeasuredWidth() * 3f / 2;
                            }
                            boolean back = shouldBack(ratio);
                            if (shouldIgnoreTouch(ratio)) {
                                mDownFinishScroller = false;
                            }
                            //logState("motionend ratio(" + ratio + ")");
								/*if (back) {
									ControlCenter.getInstance().hideInput();
								}*/
                            if (shouldReset(ratio) || back) {
                                scrollEndAction(back);
                                if (!back) {
                                    if (!mResetCalled) {
                                        LeLog.i("CW", "Feature onReset");
                                        mCallback.onReset();
                                        mResetCalled = true;
                                    }
                                }
                            }
                        } else if (mState == BYFeatureView.STATES.RESET) {
                            if (!mResetCalled) {
                                LeLog.i("CW", "Feature onReset");
                                mCallback.onReset();
                                mResetCalled = true;
                            }
                        }
                    }
                }, 50);
            }

            private boolean shouldReset(float ratio) {
                return ratio <= 0;
            }

            private boolean shouldBack(float ratio) {
                return ratio >= 1;
            }

            private boolean shouldIgnoreTouch(float ratio) {
                return ratio > 0.5;
            }
        });
    }

    private void scrollEndAction(boolean back) {
        mState = BYFeatureView.STATES.RESET;
        if (mFeatureView.getFeatureListener() != null) {
            mFeatureView.getFeatureListener().onContainerScrollEnd(back, BYViewContainer.this, mSecondTopSet);
        }
    }

    private void logState(String msg) {
        if (DEBUG_STATE) {
            LeLog.i("CW", msg);
        }
    }

    private boolean mDownFinishScroller = true;
    protected void forceFinishScroller() {
        if (mDownFinishScroller) {
            super.forceFinishScroller();
        }
    }

    protected void touchEndSnap(int screen, float velocityX) {
        snapToScreen(screen);
    }


    boolean sameTypeShowing(View targetView) {
        return mTargetView.getClass().getName().equals(targetView.getClass().getName());
    }

    @Override
    public String toString() {
        if (DEBUG) {
            return "container(" + mFeatureView.getIndex(this) + ")";
        }
        return super.toString();
    }

}
