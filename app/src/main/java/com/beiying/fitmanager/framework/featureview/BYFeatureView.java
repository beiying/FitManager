package com.beiying.fitmanager.framework.featureview;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import com.beiying.fitmanager.R;
import com.beiying.fitmanager.core.BYSafeRunnable;
import com.beiying.fitmanager.core.ContextContainer;
import com.beiying.fitmanager.core.LeLog;
import com.beiying.fitmanager.core.ui.LeView;
import com.beiying.fitmanager.core.utils.BYAndroidUtils;
import com.beiying.fitmanager.framework.DrawerLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BYFeatureView extends LeView {
	private static final boolean DEBUG_ORENTATION = false;

	private static final int DOUBLE_CLICK_TIME = 400;

	private List<BYViewContainer> mChildList;
	private Map<BYViewContainer, BYFeatureData> mViewDataMap;

	private FeatureAnimController mAnimController;
	private FeatureAnimController.AnimViewSet mPrimaryViewSet;

	private int mRequestOrientation;

	private long mLastDownTime = 0;

	BYFeatureListener mListener;

	public BYFeatureView(Context context) {
		super(context);
		initListener();

		mChildList = new ArrayList<BYViewContainer>();
		mViewDataMap = new HashMap<BYViewContainer, BYFeatureData>();

		mAnimController = new FeatureAnimController(this);

		mRequestOrientation = ContextContainer.sActivity.getRequestedOrientation();

		setWillNotDraw(false);

		setContentDescription("feature view");
		setBackgroundResource(R.drawable.featureview_bg);
	}

	public void initViewSet(View... views) {
		if (views != null && views.length > 0) {
			mPrimaryViewSet = new FeatureAnimController.AnimViewSet(views).setLeader(views[0]);
		}
	}

	private void initListener() {
		mListener = new BYFeatureListener() {
			//ViewContainer手势滑动开始的时候
			@Override
			public void onContainerTriggered(View top, FeatureAnimController.AnimViewSet secondTop) {
				beforeConnectedAnim();

				final FeatureAnimController.AnimViewSet fSecondTop = secondTop == null ? mPrimaryViewSet : secondTop;
				fSecondTop.setVisibility(View.VISIBLE, "onTriggered");
			}

			//ViewContainer手势滑动的时候
			@Override
			public void onContainerScrolled(float ratio, View top, FeatureAnimController.AnimViewSet secondTop) {
				animateWhileScrolling(secondTop, ratio);
			}

			//ViewContainer手势滑动结束的时候
			@Override
			public void onContainerScrollEnd(boolean shouldBack, View top, FeatureAnimController.AnimViewSet secondTop) {
				afterConnectedAnim();

				if (shouldBack) {
					backFeatureView(false, true);
				} else {
					secondTop = secondTop == null ? mPrimaryViewSet : secondTop;
					secondTop.setVisibility(View.GONE, "onScrolled");
				}
			}

			@Override
			public void beforeConnectedAnim() {

			}

			@Override
			public void afterConnectedAnim() {

			}

			@Override
			public void beforeTrigger(View top, BYSafeRunnable runnable) {

			}

			@Override
			public void afterScroll(View top, BYSafeRunnable runnable) {

			}
		};
	}

	public BYFeatureListener getFeatureListener() {
		return mListener;
	}

	public FeatureAnimController getAnimController() {
		return mAnimController;
	}

	public void showFetureView(View view, final BYFeatureCallback callback, boolean shouldLockScreen,
							   int requestedOrientation, Object data) {
		if (getVisibility() != View.VISIBLE) {
			enter();
			addViewWithData(view, callback, shouldLockScreen, requestedOrientation, data);

			final boolean translucent = callback == null ? false : callback.shouldTranslucent();
			final BYSafeRunnable endTask = new BYSafeRunnable() {

				@Override
				public void runSafely() {
					realShow(translucent, true, canAnimateConnected(callback));
					post(new BYSafeRunnable() {

						@Override
						public void runSafely() {
							if (callback != null) {
								callback.onShowAnimEnd();
							}
						}
					});
				}
			};
			if (!canAnimateConnected(callback)) {
				setVisibility(View.VISIBLE);
			}
			if (callback != null) {
				if (canAnimateConnected(callback)) {
					connectedAnimateShow(endTask);
				} else if (callback.getShowAnimation(this) != null) {
					normlAnimateShow(callback.getShowAnimation(this), endTask);
				} else {
					realShow(translucent, false, false);
				}
			} else {
				realShow(translucent, false, false);
			}
		} else {
			addViewWithData(view, callback, shouldLockScreen, data);
		}
	}

	public void realShow(boolean translucent, boolean onAnimationEnd, boolean connectedAnim) {
		hideBelow(translucent, onAnimationEnd);
		mAnimController.afterAnimation(connectedAnim, true);
	}

	public boolean backFeatureView(boolean animate, boolean slideBack) {
		if (getVisibility() != View.VISIBLE) {
			return false;
		}

		if (isAnimating() || isTopTouching()) {
			return true;
		}

		if (shouldExit(slideBack)) {
			return exitFeatureView(animate);//featureview退出
		} else {
			return backView(animate);//featureview返回上一级
		}
	}

	public boolean exitFeatureView(boolean animate) {
		if (getVisibility() != View.VISIBLE) {
			return false;
		}
		if (isAnimating()) {
			return true;
		}

		final BYFeatureCallback callback = getTopViewCallback();
		BYSafeRunnable endTask = new BYSafeRunnable() {

			@Override
			public void runSafely() {
				realExit(canAnimateConnected(callback));
			}
		};
		if (callback != null && animate) {
			if (canAnimateConnected(callback)) {
				connectedAnimateHide(endTask);
			} else if (callback.getHideAnimation(this) != null) {
				normalAniamteHide(callback.getHideAnimation(this), endTask);
			} else {
				endTask.runSafely();
			}
		} else {
			endTask.runSafely();
		}
		return true;
	}

	public boolean backView(boolean animate) {
		if (mChildList == null || mChildList.size() == 0) {
			return false;
		}
		BYViewContainer child = mChildList.get(mChildList.size() - 1);
		BYFeatureCallback callback = mViewDataMap.get(child).mCallback;
		if (callback != null) {
			if (callback.shouldConsumeKeyBack()) {
				callback.onKeyBack();
				return true;
			}
		}
		BYViewContainer topView = null;
		if (mChildList.size() - 2 >= 0) {
			topView = mChildList.get(mChildList.size() - 2);
			if (topView != null && topView.getVisibility() != View.VISIBLE) {
				topView.setVisibility(View.VISIBLE);
			}
		}
		boolean shouldLockScreen = removeChild(topView, animate);
		if (topView != null) {
			if (mViewDataMap.get(topView).mShouldLockScreen) {
				if (!shouldLockScreen) {
					BYAndroidUtils.lockScreen();
				}
			} else {
				if (shouldLockScreen) {
					BYAndroidUtils.unlockScreen(mRequestOrientation);
				}
			}
			return true;
		} else {
			if (shouldLockScreen) {
				BYAndroidUtils.unlockScreen(mRequestOrientation);
			}
			return false;
		}
	}

	public boolean backToFirstFullScreen(int index) {
		if (getVisibility() != View.VISIBLE) {
			return false;
		}

		if (isAnimating() || isTopTouching()) {
			return true;
		}
		backToIndexView(index);
		return true;
	}

	public boolean backToIndexView(int index) {
		if (mChildList == null || mChildList.size() == 0 || index > mChildList.size()) {
			return false;
		}

		for (int removeIndex = mChildList.size() - 1; removeIndex > index - 1; removeIndex--) {
			BYViewContainer child = mChildList.get(removeIndex);
			BYFeatureCallback callback = mViewDataMap.get(child).mCallback;
			reallyRemove(callback, child, mAnimController.canAnimateConnected(callback));
			mChildList.remove(removeIndex);
			mViewDataMap.remove(child);
		}

		BYViewContainer topView = null;
		if (mChildList.size() - index >= 0) {
			topView = mChildList.get(index - 1);
			if (topView != null && topView.getVisibility() != View.VISIBLE) {
				topView.setVisibility(View.VISIBLE);
			}
		}
		return true;
	}

	public void enter() {
		recordOrentation();
	}

	public void exit() {
		backAllView();
		ContextContainer.sActivity.setRequestedOrientation(mRequestOrientation);
		if (DEBUG_ORENTATION) {
			LeLog.i("CW", "exit:" + mRequestOrientation);
		}
	}

	public void recordOrentation() {
		mRequestOrientation = ContextContainer.sActivity.getRequestedOrientation();
		if (DEBUG_ORENTATION) {
			LeLog.i("CW", "enter:" + mRequestOrientation);
		}
	}

	public boolean canAnimateConnected(BYFeatureCallback callback) {
		return mAnimController.canAnimateConnected(callback);
	}

	public void connectedAnimateShow(BYSafeRunnable endTask) {
		mAnimController.connectedAnimateShow(this, mPrimaryViewSet, endTask);
	}

	public void connectedAnimateHide(BYSafeRunnable endTask) {
		mAnimController.connectedAnimateHide(this, mPrimaryViewSet, endTask);
	}

	public void normalAniamteHide(Animation hideAnim, final BYSafeRunnable endTask) {
		mAnimController.normalAnimate(this, hideAnim, endTask);
	}

	public void normlAnimateShow(Animation showAnim, final BYSafeRunnable endTask) {
		mAnimController.normalAnimate(this, showAnim, endTask);
	}

	private void hideBelow(boolean translucent, boolean onAnimationEnd) {
		if (getCurrentTargetView() != null) {
			if (!translucent) {
				mPrimaryViewSet.setVisibility(View.GONE, "hideBelow anim(" + onAnimationEnd + ")");
			}
		}
	}

	private void addViewWithData(View child, BYFeatureCallback callback, boolean shouldLockScreen, Object data) {
		addViewWithData(child, callback, shouldLockScreen, ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED, data);
	}

	private void addViewWithData(View child, final BYFeatureCallback callback, boolean shouldLockScreen,
								int requestedOrientation, Object data) {
		if (child == null) {
			return;
		}
		BYViewContainer topView = null;
		if (mChildList != null && mChildList.size() > 0) {
			topView = mChildList.get(mChildList.size() - 1);
		}
		if (topView != null) {
//			if (topView.sameTypeShowing(child)) {
//				return;
//			}
		}

		if (shouldLockScreen) {
			if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
				BYAndroidUtils.lockScreenAsPortait();
			} else if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
				BYAndroidUtils.lockScreenAsLandscape();
			} else {
				BYAndroidUtils.lockScreen();
			}
		} else {
			BYAndroidUtils.unlockScreen(mRequestOrientation);
		}

		BYFeatureData featureData = new BYFeatureData(callback, shouldLockScreen, data);
		final BYViewContainer container = addChild(child, featureData);

		if (topView != null) {
			final View fTopView = topView;
			BYSafeRunnable endTask = new BYSafeRunnable() {

				@Override
				public void runSafely() {
					hideTopView(fTopView, mAnimController.canAnimateConnected(callback));
					if (callback != null) {
						callback.onShowAnimEnd();
					}
				}
			};
			if (mAnimController.canAnimateConnected(callback)) {
				container.setVisibility(View.INVISIBLE);
			}
			if (callback != null) {
				if (mAnimController.canAnimateConnected(callback)) {
					mAnimController.connectedAnimateShow(container, new FeatureAnimController.AnimViewSet(fTopView), endTask);
				} else if (callback.getShowAnimation(this) != null) {
					mAnimController.normalAnimate(container, callback.getShowAnimation(this), endTask);
				} else {
					endTask.runSafely();
				}
			} else {
				endTask.runSafely();
			}
		}
	}

	private void hideTopView(final View topView, boolean connectedAnim) {
		topView.setVisibility(View.GONE);
		mAnimController.afterAnimation(connectedAnim, true);
	}

	private BYViewContainer addChild(View childView, BYFeatureData featureData) {
		BYViewContainer container = new BYViewContainer(this, childView, featureData.mCallback, getTopView());
		mChildList.add(container);
		mViewDataMap.put(container, featureData);

		super.addView(container);

		return container;
	}

	private boolean removeChild(BYViewContainer nextView, boolean animate) {
		final BYViewContainer child = mChildList.remove(mChildList.size() - 1);
		final BYFeatureCallback callback = mViewDataMap.get(child).mCallback;
		boolean shouldLockScreen = mViewDataMap.get(child).mShouldLockScreen;

		mViewDataMap.remove(child);
		BYSafeRunnable endTask = new BYSafeRunnable() {

			@Override
			public void runSafely() {
				reallyRemove(callback, child, mAnimController.canAnimateConnected(callback));
			}
		};
		if (nextView == null) {
			endTask.runSafely();
			return shouldLockScreen;
		}
		if (callback != null && animate) {
			if (mAnimController.canAnimateConnected(callback)) {
				mAnimController.connectedAnimateHide(child, new FeatureAnimController.AnimViewSet(nextView), endTask);
			} else if (callback.getHideAnimation(this) != null) {
				mAnimController.normalAnimate(child, callback.getHideAnimation(this), endTask);
			} else {
				endTask.runSafely();
			}
		} else {
			endTask.runSafely();
		}
		return shouldLockScreen;
	}

	private void reallyRemove(BYFeatureCallback callback, BYViewContainer child, boolean connectedAnim) {
		if (callback != null) {
			callback.beforeViewExit(child.getTargetView());
		}
		removeView(child);

		mAnimController.afterAnimation(connectedAnim, false);
	}

	private void realExit(boolean connectedAnim) {
		hideFeatureView();
		exit();
		mAnimController.afterAnimation(connectedAnim, false);
	}

	public boolean backAllView() {
		if (mChildList == null || mChildList.size() == 0) {
			return false;
		}
		for (int index = mChildList.size() - 1; index >= 0; index--) {
			BYViewContainer child = mChildList.get(index);
			BYFeatureCallback callback = mViewDataMap.get(child).mCallback;
			reallyRemove(callback, child, mAnimController.canAnimateConnected(callback));
		}
		mChildList.clear();
		mViewDataMap.clear();

		return true;
	}

	private boolean hideFeatureView() {
		if (getVisibility() != View.VISIBLE) {
			return false;
		}

		removeAllViews();
		setVisibility(View.GONE);

		mPrimaryViewSet.setVisibility(View.VISIBLE, "hideFeature");

		return true;
	}

	@Override
	public void addView(View child, int index, ViewGroup.LayoutParams params) {
		BYFeatureData featureData = mViewDataMap.get(child);
		if (featureData != null && featureData.mCallback != null) {
			featureData.mCallback.onAddView();
		}
		super.addView(child, index, params);
	}

	@Override
	public void removeView(View view) {
		super.removeView(view);
		BYFeatureData featureData = mViewDataMap.get(view);
		if (featureData != null && featureData.mCallback != null) {
			featureData.mCallback.onRemoveView();
		}
	}

	public void animateWhileScrolling(FeatureAnimController.AnimViewSet secondTop, float ratio) {
		secondTop = secondTop == null ? mPrimaryViewSet : secondTop;
		mAnimController.animateWhileScrolling(secondTop, ratio);
	}

	public boolean onMenu() {
		if (isShowing()) {
			BYViewContainer topView = getTopView();
			if (topView != null) {
				BYFeatureCallback callback = mViewDataMap.get(topView).mCallback;
				if (callback != null) {
					callback.onMenu();
				}
			}
			return true;
		}
		return false;
	}

	private boolean shouldExit(boolean slideBack) {
		if (isFirstViewShowing()) {
			BYViewContainer view = mChildList.get(0);
			BYFeatureData featureData = mViewDataMap.get(view);
			if (featureData.mCallback != null) {
				return !featureData.mCallback.shouldConsumeKeyBack() || slideBack;
			}
		}
		return false;
	}

	private boolean isFirstViewShowing() {
		if (mChildList != null && mChildList.size() == 1) {
			return true;
		}
		return false;
	}

	private boolean isShowing() {
		if (getVisibility() == View.VISIBLE) {
			return true;
		}
		return false;
	}

	public boolean isTopTouching() {
		BYViewContainer top = getTopView();
		return top != null && top.mIsTouching;
	}

	private BYViewContainer getTopView() {
		if (mChildList != null && mChildList.size() > 0) {
			return mChildList.get(mChildList.size() - 1);
		}
		return null;
	}

	Object getTopViewData() {
		BYViewContainer topView = getTopView();
		if (topView == null) {
			return null;
		}
		return mViewDataMap.get(topView).mData;
	}

	public BYFeatureCallback getTopViewCallback() {
		BYViewContainer topView = getTopView();
		if (topView == null) {
			return null;
		}
		return mViewDataMap.get(topView).mCallback;
	}

	public View getCurrentTargetView() {
		if (mChildList != null && mChildList.size() > 0) {
			return mChildList.get(mChildList.size() - 1).getTargetView();
		}
		return null;
	}

	public boolean isAnimating() {
		return mAnimController.mAnimating;
	}

	public void unfreezeTopContainer() {
		if (mChildList != null && mChildList.size() > 0) {
			BYViewContainer topContainer = mChildList.get(mChildList.size() - 1);
			if (topContainer != null) {
				topContainer.unfreeze();
			}
		}
	}

	public int getIndex(BYViewContainer container) {
		if (mChildList == null) {
			return -1;
		}
		return mChildList.indexOf(container);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int childCnt = getChildCount();
		for (int index = 0; index < childCnt; index++) {
			View child = getChildAt(index);
			if (child.getVisibility() == View.VISIBLE) {
				child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}

	public static BYFeatureCallback createDefaultCallback() {
		return new DefaultCallback();
	}

	public int getContentPaddingBottom(boolean keyboardVisible, int keyboardHeight) {
		if (getVisibility() == View.VISIBLE) {
			BYViewContainer topView = getTopView();
			if (topView != null) {
				BYFeatureCallback callback = mViewDataMap.get(topView).mCallback;
				if (callback != null) {
					return callback.getContentPaddingBottom(keyboardVisible, keyboardHeight);
				}
			}
		}
		return 0;
	}

	public boolean isKeyboardShowing() {
		return ((DrawerLayout.LayoutParams) getLayoutParams()).bottomMargin != 0;
	}

	public enum STATES {
		RESET,
		START,
		SCROLLING
	}

	public static class DefaultCallback implements BYFeatureCallback {

		@Override
		public void onMenu() {

		}

		@Override
		public boolean shouldConsumeKeyBack() {
			return false;
		}

		@Override
		public void onKeyBack() {

		}

		@Override
		public void beforeViewExit(View view) {

		}

		public void onShowAnimEnd() {

		}

		public Animation getShowAnimation(BYFeatureView featureView) {
			return featureView.mAnimController.getShowAnimationSet();
		}

		public Animation getHideAnimation(BYFeatureView featureView) {
			return featureView.mAnimController.getExitAnimationSet();
		}

		@Override
		public boolean shouldTranslucent() {
			return false;
		}

		@Override
		public void onAddView() {

		}

		@Override
		public void onRemoveView() {

		}

		@Override
		public int getContentPaddingBottom(boolean keyboardVisible, int keyboardHeight) {
			return keyboardVisible ? keyboardHeight : 0;
		}

		@Override
		public boolean useConnectedAnim() {
			return true;
		}

		@Override
		public boolean disableConnectedAnimTemporarily(final boolean keyboarding, final  MotionEvent event, final View view) {
			return false;
		}

		@Override
		public void onReset() {
			LeLog.i("CW", "Feature reset");
		}
	}

}
