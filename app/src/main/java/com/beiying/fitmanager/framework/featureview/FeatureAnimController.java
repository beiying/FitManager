package com.beiying.fitmanager.framework.featureview;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;

import com.beiying.fitmanager.core.BYSafeRunnable;
import com.beiying.fitmanager.core.LeLog;
import com.beiying.fitmanager.core.utils.LeMachineHelper;
import com.beiying.fitmanager.framework.BYControlCenter;
import com.beiying.fitmanager.framework.ui.BYTheme;

import java.util.ArrayList;
import java.util.List;


public class FeatureAnimController {
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_WEBVIEW_SNAP = false;

    private static final boolean ENABLE_CONNECTED_ANIM = true;
    static final boolean ENABLE_CONNECTED_ANIM_LOWER = false;
    private static final boolean DISABLE_LOW_PERFORMACE_MACHINE = false;
    boolean mEnableScrollRatio = true;

    private static final int ANIMATION_DURATION = 200;

    private AnimationSet sShowAnimationSet;
    private AnimationSet sExitAnimationSet;

    boolean mAnimating;

    private BYFeatureView mFeatureView;

    private boolean mHighPerformaceMachine;

    public FeatureAnimController(BYFeatureView featureView) {
        mFeatureView = featureView;
        mHighPerformaceMachine = LeMachineHelper.isHighSpeedPhone();

        mEnableScrollRatio = LeMachineHelper.isHighSpeedPhone();
    }

    public boolean canAnimateConnected(BYFeatureCallback callback) {
        if (!ENABLE_CONNECTED_ANIM) {
            return false;
        }
        if (callback == null) {
            return false;
        }
        if (DISABLE_LOW_PERFORMACE_MACHINE && !mHighPerformaceMachine) {
            return false;
        }
        return callback.useConnectedAnim() && canSupportPropertyAnimation();
    }

    private boolean canSupportPropertyAnimation() {
        return LeMachineHelper.getSDKVersionInt() >= Build.VERSION_CODES.HONEYCOMB;
    }

    public void animateWhileScrolling(AnimViewSet viewSet, float ratio) {
        if (ENABLE_CONNECTED_ANIM_LOWER) {
            float scaleLine = getAnimScale();
            float scale = (1 - scaleLine) * ratio + scaleLine;
            scale = scale > 1 ? 1 : scale;

            viewSet.setAlpha(scale);
            viewSet.setScale(scale);

//		Animator animator = getConnectedHideLowerAnim(viewSet.mLeader, ratio);
//		viewSet.startAnimator(animator);
//		
//		float transX = getBackFromLeftDestTransX(viewSet.mLeader, ratio);
//		viewSet.setTranslationX(transX);
        }
    }

    private List<BYSafeRunnable> mConnectedHideTaskList = new ArrayList<BYSafeRunnable>();

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void connectedAnimateShow(final View upperView, final AnimViewSet lowerViews, final BYSafeRunnable endTask) {
        beforeAnimation(true);

        upperView.setVisibility(View.VISIBLE);
        Animator upperAnim = getConnectedShowUpperAnim(upperView, new BYSafeRunnable() {

            @Override
            public void runSafely() {
                if (endTask != null) {
                    endTask.runSafely();
                }
            }
        });
        upperAnim.start();

        if (ENABLE_CONNECTED_ANIM_LOWER) {
            if (lowerViews != null && lowerViews.hasView()) {
                Animator scaleAnim = getConnectedShowLowerAnim(lowerViews.mLeader);
                //scaleAnim.setStartDelay(50);

                lowerViews.startAnimator(scaleAnim);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void connectedAnimateHide(View upperView, final AnimViewSet lowerViews, final BYSafeRunnable endTask) {
        beforeAnimation(true);

        final View upperTargetView = upperView;
        Animator hideAnim = getConnectedHideUpperAnim(upperTargetView, new BYSafeRunnable() {

            @Override
            public void runSafely() {
                if (endTask != null) {
                    endTask.runSafely();
                }
            }
        });
        hideAnim.start();
        if (ENABLE_CONNECTED_ANIM_LOWER) {
            if (lowerViews != null && lowerViews.hasView()) {
                Animator scaleUpAnim = getConnectedHideLowerAnim(lowerViews.mLeader, 1);
                scaleUpAnim.setStartDelay(50);

                lowerViews.startAnimator(scaleUpAnim);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public Animator getConnectedShowUpperAnim(View target, final BYSafeRunnable endTask) {
        final float ratio = 0.8f;
        ObjectAnimator oa = ObjectAnimator.ofFloat(target, "translationX", mFeatureView.getMeasuredWidth() * ratio, 0);
        oa.setInterpolator(new DecelerateInterpolator(1.5f));
        oa.setDuration(300);
        oa.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (endTask != null) {
                    endTask.runSafely();
                }
            }
        });
        return oa;
    }

    public Animator getConnectedShowLowerAnim(View target) {
        return getScaleSmallerAnim(target);
        //return getTranslateLeftAnim(target);
    }

    private float getTransLeft(View view) {
        final float delta = 0.3f;
        return delta * view.getMeasuredWidth();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private Animator getTranslateLeftAnim(View view) {
        float distance = getTransLeft(view);
        float startTransX = view.getTranslationX();
        float destTransX = -distance;
        ObjectAnimator oa = ObjectAnimator.ofFloat(view, "translationX", startTransX, destTransX);
        oa.setDuration(400);

        return oa;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private Animator getBackFromLeftAnim(View view, float ratio) {
        float myMoveRatio = ratio;

        float distance = getTransLeft(view);
        float moving = distance * myMoveRatio;
        float startTransX = view.getTranslationX();
        float destTransX = -distance + moving;
        destTransX = destTransX > 0 ? 0 : destTransX;
        long duration = 200;//(long) Math.abs(totalDuratoin * myMoveDelta);
        ObjectAnimator oa = ObjectAnimator.ofFloat(null, "translationX", startTransX, destTransX);
        oa.setDuration(duration);

        return oa;
    }

    private float getBackFromLeftDestTransX(View view, float ratio) {
        float distance = getTransLeft(view);
        float moving = distance * ratio;
        float destTransX = -distance + moving;
        destTransX = destTransX > 0 ? 0 : destTransX;

        return destTransX;
    }

    private float getAnimScale() {
        float nomralScale = 0.95f;
        return nomralScale;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private Animator getScaleSmallerAnim(View target) {
        target.setPivotX(mFeatureView.getMeasuredWidth() / 2);
        target.setPivotY(mFeatureView.getMeasuredHeight() / 2);

        float scale = getAnimScale();
        PropertyValuesHolder pvhAlpha = PropertyValuesHolder.ofFloat("alpha",
                1f, scale);
        PropertyValuesHolder pvhScaleX =
                PropertyValuesHolder.ofFloat("scaleX", 1f, scale);
        PropertyValuesHolder pvhScaleY =
                PropertyValuesHolder.ofFloat("scaleY", 1f, scale);
        final ObjectAnimator changeIn = ObjectAnimator.ofPropertyValuesHolder(
                target, pvhAlpha, pvhScaleX, pvhScaleY).
                setDuration(300);
        return changeIn;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private Animator getScaleBiggerAnim(View target) {
        target.setPivotX(mFeatureView.getMeasuredWidth() / 2);
        target.setPivotY(mFeatureView.getMeasuredHeight() / 2);

        float scale = target.getScaleX();//getAnimScale();
        PropertyValuesHolder pvhAlpha = PropertyValuesHolder.ofFloat("alpha",
                scale, 1f);
        PropertyValuesHolder pvhScaleX =
                PropertyValuesHolder.ofFloat("scaleX", scale, 1f);
        PropertyValuesHolder pvhScaleY =
                PropertyValuesHolder.ofFloat("scaleY", scale, 1f);
        final ObjectAnimator changeIn = ObjectAnimator.ofPropertyValuesHolder(
                target, pvhAlpha, pvhScaleX, pvhScaleY).
                setDuration(300);
        return changeIn;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public Animator getConnectedHideUpperAnim(final View target, final BYSafeRunnable endTask) {
        final float ratio = 0.1f;
        ObjectAnimator oa = ObjectAnimator.ofFloat(target, "translationX", mFeatureView.getMeasuredWidth() * ratio,
                mFeatureView.getMeasuredWidth());
        oa.setInterpolator(new DecelerateInterpolator());
        oa.setDuration(300);
        oa.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (endTask != null) {
                    endTask.runSafely();
                }
                target.setTranslationX(0);
            }
        });
        return oa;
    }

    public Animator getConnectedHideLowerAnim(View view, float ratio) {
        return getScaleBiggerAnim(view);
        //return getBackFromLeftAnim(view, ratio);
    }

    public void beforeAnimation(boolean connectedAnim) {
        mAnimating = true;
        if (connectedAnim) {
            mFeatureView.setBackgroundColor(BYTheme.getBgColor());
            if (mFeatureView.mListener != null) {
                mFeatureView.mListener.beforeConnectedAnim();
            }
        }
    }

    public void afterAnimation(boolean connectedAnim, boolean show) {
        if (connectedAnim) {
            mFeatureView.setBackgroundColor(BYTheme.getColor("common_transparent"));
            if (mFeatureView.mListener != null) {
                mFeatureView.mListener.afterConnectedAnim();
            }
            mFeatureView.unfreezeTopContainer();
        }
        if (!show) {
            BYControlCenter.getInstance().postToUiThread(new BYSafeRunnable() {

                @Override
                public void runSafely() {
                    if (mConnectedHideTaskList != null && mConnectedHideTaskList.size() > 0) {
                        BYSafeRunnable task = mConnectedHideTaskList.remove(mConnectedHideTaskList.size() - 1);
                        if (task != null) {
                            task.runSafely();
                        }
                    }
                }
            }, 33);
        }
        mAnimating = false;
    }

    public void normalAnimate(View view, Animation animation, final BYSafeRunnable endTask) {
        startAnim(view, animation, new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (endTask != null) {
                    endTask.runSafely();
                }
            }
        });
    }

    public void startAnim(View view, Animation animationSet, AnimationListener listener) {
        beforeAnimation(false);

        animationSet.setAnimationListener(listener);
        view.startAnimation(animationSet);
    }

    public AnimationSet getShowAnimationSet() {
        if (sShowAnimationSet == null) {
            AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
            sShowAnimationSet = new AnimationSet(true);
            sShowAnimationSet.addAnimation(alphaAnimation);
            sShowAnimationSet.setDuration(ANIMATION_DURATION);
        }
        return sShowAnimationSet;
    }

    public AnimationSet getExitAnimationSet() {
        if (sExitAnimationSet == null) {
            AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
            sExitAnimationSet = new AnimationSet(true);
            sExitAnimationSet.addAnimation(alphaAnimation);
            sExitAnimationSet.setDuration(ANIMATION_DURATION);
        }
        return sExitAnimationSet;
    }

    public Animation getShowAnimation(BYFeatureView featureView) {
        final long time = 200;
        int width = featureView.getMeasuredWidth();
        int start = (int) (width * 0.9);
        int end = (int) (width * 0.1);
        Animation animation = new TranslateAnimation(start, end, 0, 0);
        animation.setDuration(time);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
        alphaAnimation.setDuration(time);

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(animation);
        animationSet.addAnimation(alphaAnimation);

        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());

        return animationSet;
    }

    public Animation getHideAnimation(BYFeatureView featureView) {
        final long time = 200;
        int width = featureView.getMeasuredWidth();
        int start = (int) (width * 0.1);
        int end = (int) (width * 0.9);
        Animation animation = new TranslateAnimation(start, end, 0, 0);
        animation.setDuration(time);

        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.1f);
        alphaAnimation.setDuration(time);

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(animation);
        animationSet.addAnimation(alphaAnimation);

        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());

        return animation;
        //return featureView.getExitAnimationSet();
    }

    public static class AnimViewSet {
        private List<View> mViewList;
        View mLeader;

        public AnimViewSet(View... views) {
            if (views != null) {
                mViewList = new ArrayList<View>();
                for (View view : views) {
                    mViewList.add(view);
                }
                if (views.length == 1) {
                    mLeader = views[0];
                }
            }
        }

        AnimViewSet setLeader(View leader) {
            mLeader = leader;
            return this;
        }

        boolean hasView() {
            return mViewList != null && mViewList.size() > 0;
        }

        public void setVisibility(int visibility, String invokeTag) {
            if (mViewList != null) {
                for (View view : mViewList) {
                    view.setVisibility(visibility);

                }
            }
            if (DEBUG) {
                LeLog.i(new ViewInfo(mLeader).watchVisibility().invoke(invokeTag));
            }
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        void startAnimator(Animator animator) {
            if (mViewList != null) {
                AnimatorListener listener = new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);

                        if (DEBUG) {
                            LeLog.i(new ViewInfo(mLeader).watchAlpha().watchScaleX().watchScaleY().watchPrivotX().watchPrivotY().invoke("ANIMATING"));
                        }
                    }
                };
                for (final View view : mViewList) {
                    Animator cloneAnim = animator.clone();
                    cloneAnim.setTarget(view);
                    if (view == mLeader) {
                        cloneAnim.addListener(listener);
                    }
                    cloneAnim.start();
                }
            }
        }

        void setTranslationX(float transX) {
            if (mViewList != null) {
                for (View view : mViewList) {
                    view.setTranslationX(transX);
                }
            }
        }

        void setAlpha(float alpha) {
            if (mViewList != null) {
                for (View view : mViewList) {
                    view.setAlpha(alpha);
                }
            }
            if (DEBUG) {
                LeLog.i(new ViewInfo(mLeader).watchAlpha().invoke("SCROLLING"));
            }
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        void setScale(float scale) {
            if (mViewList != null) {
                for (View view : mViewList) {
                    view.setScaleX(scale);
                    view.setScaleY(scale);
                }
            }
            if (DEBUG) {
                LeLog.i(new ViewInfo(mLeader).watchScaleX().invoke("SCROLLING"));
            }
        }
    }

    private static class ViewInfo {

        private View mView;

        private boolean mWatchVisibility;
        private boolean mWatchAlpha;
        private boolean mWatchScaleX;
        private boolean mWatchScaleY;
        private boolean mWatchPrivotX;
        private boolean mWatchPrivotY;

        public ViewInfo(View view) {
            mView = view;
        }

        ViewInfo watchVisibility() {
            mWatchVisibility = true;
            return this;
        }

        ViewInfo watchAlpha() {
            mWatchAlpha = true;
            return this;
        }

        ViewInfo watchScaleX() {
            mWatchScaleX = true;
            return this;
        }

        ViewInfo watchScaleY() {
            mWatchScaleY = true;
            return this;
        }

        ViewInfo watchPrivotX() {
            mWatchPrivotX = true;
            return this;
        }

        ViewInfo watchPrivotY() {
            mWatchPrivotY = true;
            return this;
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        private String invoke(String invokeTag) {
            StringBuilder sb = new StringBuilder();
            sb.append(invokeTag + " ");
            sb.append(mView.getContentDescription() + " ");
            if (mWatchVisibility) {
                sb.append("visible(" + mView.getVisibility() + ") ");
            }
            if (mWatchAlpha) {
                sb.append("alpha(" + mView.getAlpha() + ") ");
            }
            if (mWatchScaleX) {
                sb.append("scaleX(" + mView.getScaleX() + ") ");
            }
            if (mWatchScaleY) {
                sb.append("scaleY(" + mView.getScaleY() + ") ");
            }
            if (mWatchPrivotX) {
                sb.append("privotX(" + mView.getPivotX() + ") ");
            }
            if (mWatchPrivotY) {
                sb.append("privotY(" + mView.getPivotY() + ") ");
            }

            return sb.toString();
        }
    }
}
