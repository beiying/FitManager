/** 
 * Filename:    LePopupContainer.java
 * Description:  
 * Copyright:   Lenovo PCL Copyright(c)2013 
 * @author:     chenwei07 
 * @version:    1.0
 * Create at:   2013-6-26 下午2:04:06
 * 
 * Modification History: 
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------ 
 * 2013-6-26     chenwei07    1.0         1.0 Version 
 */
package com.beiying.fitmanager.core.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.beiying.core.R;
import com.beiying.fitmanager.core.utils.LeMachineHelper;


/**
 * PopupContainer必须是全屏大小的
 */
public class LePopupContainer extends LeViewGroup {

	private static final int UI_DRAG_THRESHOLD = 8;

	private LePopupLayout mPopLayout;

	private Point mLastDownPoint;

	private Point mParabolaStartPoint;

	private Point mParabolaEndPoint;

	protected int mDragThreshold;

	public LePopupContainer(Context context, LePaddingParam paddingParam) {
		super(context);
		
		mPopLayout = new LePopupLayout(getContext(), paddingParam);
		addView(mPopLayout);

		mDragThreshold = LeUI.getDensityDimen(getContext(), UI_DRAG_THRESHOLD);
	}
	
	public View getShowingPopup() {
		if (mPopLayout.isShowing()) {
			return mPopLayout.getPopContent();
		}
		return null;
	}
	
	public void onMove(float deltaX, float deltaY) {
		if (Math.abs(deltaX) > mDragThreshold || Math.abs(deltaY) > mDragThreshold) {
			if (mPopLayout.mListener != null && mPopLayout.mListener.onMove(this)) {
				return ;
			}
			dismissPopups();
		}
	}

	public void showPopup(View view, FrameLayout.LayoutParams lParams, Point pivotePoint, LePopupListener listener) {
		mPopLayout.showPopViewWithAnim(view, lParams, pivotePoint, listener);
	}
	
	public void showPopMenu(LePopMenu popMenu, Point leftTopPoint) {
		if (leftTopPoint == null) {
			if (mLastDownPoint == null) {
				mLastDownPoint = new Point();
				mLastDownPoint.set(LeMachineHelper.getHorizontalResolution() / 2,
						LeMachineHelper.getVerticalResolution() / 2);
			}
			mPopLayout.showPopViewAtFinger(popMenu, mLastDownPoint);
		} else {
			mPopLayout.showPopViewAtFixedPoint(popMenu, leftTopPoint);
		}
	}

	public boolean showParabolaAnimation(final Runnable runnable) {
		if (LeMachineHelper.isMachineAPad() && LeMachineHelper.isShowTab()) {
			return false;
		}

		if (Build.VERSION.SDK_INT < 11) {
			return false;
		}

		PointF startPoint = new PointF(mParabolaStartPoint.x, mParabolaStartPoint.y);
//		LeToolbarView toolbarView = ControlCenter.getInstance().getToolbarView();
//		if (toolbarView == null) {
//			return false;
//		}
//
//		LeMultiWindowButton button = toolbarView.getMultiButton();
//		if (button == null) {
//			return false;
//		}

//		PointF endPoint = getEndPointF(button);
//		final LeParabolaView parabolaView = new LeParabolaView(getContext(), startPoint, endPoint, button);
//		parabolaView.startAnimator(new LeParabolaListener() {
//			@Override
//			public void onShakeStart() {
//				if (runnable != null) {
//					runnable.run();
//				}
//			}
//
//			@Override
//			public void onTotalAnimatorEnd() {
//				removeView(parabolaView);
//			}
//		});
//		addView(parabolaView);

		return true;
	}


	public void showPopMenu(LePopMenu popMenu, Point leftTopPoint, LePopupListener listener) {
		mPopLayout.mListener = listener;
		if (leftTopPoint == null) {
			if (mLastDownPoint == null) {
				mLastDownPoint = new Point();
				mLastDownPoint.set(LeMachineHelper.getHorizontalResolution() / 2,
						LeMachineHelper.getVerticalResolution() / 2);
			}
			mPopLayout.showPopViewAtFinger(popMenu, mLastDownPoint);
		} else {
			mPopLayout.showPopViewAtFixedPoint(popMenu, leftTopPoint);
		}
	}
	
	public boolean dismissPopups() {
		if (mPopLayout != null && mPopLayout.isShowing()) {
			mPopLayout.dismissPopView();
			return true;
		}
		return false;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int height = MeasureSpec.getSize(heightMeasureSpec);
		
		setMeasuredDimension(width, height);
		
		mPopLayout.measure(widthMeasureSpec, heightMeasureSpec);
		for (int i = 0; i< getChildCount(); i++) {
			View child = getChildAt(i);
			if (child instanceof LeParabolaView) {
				child.measure(widthMeasureSpec, heightMeasureSpec);
			}
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int offsetX, offsetY;
		offsetX = 0;
		offsetY = 0;
		LeUI.layoutViewAtPos(mPopLayout, offsetX, offsetY);
		for (int i = 0; i< getChildCount(); i++) {
			View child = getChildAt(i);
			if (child instanceof LeParabolaView) {
				LeUI.layoutViewAtPos(child, 0, 0);
			}
		}
	}
	
	public boolean dispatchTouchEvent(MotionEvent ev) {
	//public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mLastDownPoint = new Point((int) ev.getX(), (int) ev.getY());
				break;

			default:
				break;
		}
		return super.dispatchTouchEvent(ev);
	}

	
	public interface LePopupListener {
		boolean onMove(LePopupContainer popupContainer);
		void onDismiss();
	}
	
	public static class LePaddingParam {
		int mLeftPadding;
		int mRightPadding;
		int mTopPadding;
		int mBottomPadding;
		
		public LePaddingParam(int leftPadding, int rightPadding, int topPadding, int bottomPadding) {
			mLeftPadding = leftPadding;
			mRightPadding = rightPadding;
			mTopPadding = topPadding;
			mBottomPadding = bottomPadding;
		}
	}
	
	class LePopupLayout extends FrameLayout implements LePopContent.PopCallbak {

		public static final int POP_ANIMATION_DURATION = 200;

		// 手指按下的半径
		private static final int DOWN_FINGER_RADIUS = 0;
		private static final int UP_FINGER_RADIUS = 50;

		private LePaddingParam mPaddingParam;

		protected LePopupListener mListener;

		public LePopupLayout(Context context, LePaddingParam paddingParam) {
			super(context);

			mPaddingParam = paddingParam;

			setClickable(true);

			setVisibility(View.INVISIBLE);
		}

		public boolean isShowing() {
			return (getVisibility() == View.VISIBLE);
		}

		public View getPopContent() {
			if (getChildCount() != 0) {
				return getChildAt(0);
			} else {
				return null;
			}
		}

		public void showPopViewWithAnim(View popup, FrameLayout.LayoutParams lParams, Point pivotPoint, LePopupListener listener) {
			mListener = listener;

			realShowPopView(popup, lParams, pivotPoint);

			for (int i = 0; i < getChildCount(); i++) {
				getChildAt(i).clearAnimation();
			}
			AnimationSet animationSet = new AnimationSet(true);
			Animation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
			animationSet.addAnimation(alphaAnimation);

			pivotPoint.x -= lParams.leftMargin;
			pivotPoint.y -= lParams.topMargin;
			Animation scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, pivotPoint.x, pivotPoint.y);
			animationSet.addAnimation(scaleAnimation);
			animationSet.setDuration(POP_ANIMATION_DURATION);

			popup.clearAnimation();
			popup.startAnimation(animationSet);
		}

		public void showPopViewAtFixedPoint(LePopContent popContent, Point fixedPoint) {
			FrameLayout.LayoutParams lParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			lParams.gravity = Gravity.NO_GRAVITY;
			lParams.leftMargin = fixedPoint.x;
			lParams.topMargin = fixedPoint.y;

			showPopViewWithPoint(popContent, lParams, fixedPoint);
		}

		public void showPopViewAtFinger(LePopContent popContent, Point fingerPoint) {
			final DisplayMetrics dm = getResources().getDisplayMetrics();
			final int menuWidth = popContent.getContentWidth();
			final int menuHeight = popContent.getContentHeight();

			final int downFingerRadius = (int) (DOWN_FINGER_RADIUS * dm.density);
			final int upFingerRadius = (int) (UP_FINGER_RADIUS * dm.density);

			FrameLayout.LayoutParams lParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			lParams.gravity = Gravity.NO_GRAVITY;

			if (getMeasuredHeight() - fingerPoint.y - mPaddingParam.mBottomPadding > menuHeight) {
				// 如果下方放得下，放在下方
				lParams.topMargin = fingerPoint.y;
			} else if (fingerPoint.y - mPaddingParam.mTopPadding > menuHeight) {
				// 如果上方放得下，则放在上方
				lParams.topMargin = fingerPoint.y - menuHeight;
			} else {
				// 如果上方下方都放不下
				if (fingerPoint.y - mPaddingParam.mTopPadding >= getMeasuredHeight() - fingerPoint.y - mPaddingParam.mBottomPadding) {
					// 上方空间更大
					lParams.topMargin = mPaddingParam.mTopPadding;
				} else {
					// 下方空间更大
					lParams.topMargin = getMeasuredHeight() - menuHeight - mPaddingParam.mBottomPadding;
				}
			}

			lParams.leftMargin = fingerPoint.x;
			int popMenuWidth = getMeasuredWidth();
			if (popMenuWidth == 0) {
				popMenuWidth = dm.widthPixels;
			}
			if (fingerPoint.x > menuWidth) {
				lParams.leftMargin = fingerPoint.x - menuWidth;
			} else if (lParams.leftMargin + menuWidth < popMenuWidth) {
				lParams.leftMargin = fingerPoint.x;
			} else {
				if (popMenuWidth - fingerPoint.x > fingerPoint.x) {
					lParams.leftMargin = popMenuWidth - menuWidth;
				} else {
					lParams.leftMargin = 0;
				}
			}

			showPopViewWithPoint(popContent, lParams, fingerPoint);
		}

		private void showPopViewWithPoint(LePopContent popContent, FrameLayout.LayoutParams lParams,
				Point position) {
			setCallback(popContent);

			realShowPopView(popContent, lParams, position);
			mParabolaStartPoint = position;
		}

		private void setCallback(LePopContent popContent) {
			popContent.mCommonCallback = this;
		}

		private void realShowPopView(View popContent, FrameLayout.LayoutParams layoutParams, Point position) {
			if (getChildCount() != 0) {
				recycleAndDestoryMenus();
				removeAllViews();
			}

			if (popContent instanceof LePopContent) {
				popContent.setDrawingCacheEnabled(true);
			}
			if (layoutParams != null) {
				addView(popContent, layoutParams);
			} else {
				addView(popContent);
			}

			setVisibility(View.VISIBLE);
		}

		public void dismissPopView() {
			if (mListener != null) {
				mListener.onDismiss();
				mListener = null;
			}

			recycleAndDestoryMenus();
			removeAllViews();
			setVisibility(View.INVISIBLE);

			requestLayout();
		}

		protected void recycleAndDestoryMenus() {
			for (int i = 0; i < getChildCount(); i++) {
				getChildAt(i).clearAnimation();
				if (!(getChildAt(i) instanceof LePopMenu)) {
					continue;
				}
				((LePopMenu) getChildAt(i)).recyclePopMenu();
				getChildAt(i).destroyDrawingCache();
			}
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					dismissPopView();
					break;
				default:
					break;
			}
			return super.onTouchEvent(event);
		}

		@Override
		public void onDismiss() {
			dismissPopView();
		}

	}

	class LeParabolaView extends ViewGroup {
		private static final int MUL = 1;
		public static final int ALPHA_SHOW_DURATION = 135;
		public static final int ALPHA_HIDE_DURATION = 135;
		public static final int ALPHA_HIDE_DELAY = 480;
		public static final int TRANSLATE_DURATION = 580;
		public static final int SHACK_DUTATION = 1600;
		public static final int TRANSLATE_DELAY = 60;
		public static final int TOP_HEIGHT = 40;
		public static final float TIME_TO_REACH_TOP = 0.3f;
		public static final int IMAGE_HEIGHT = 18;

		private ImageView mImageView;
		private LeParabolaListener mListener;

		private AnimatorSet mAnimatorSet;
		private PointF mStartPoint;
		private PointF mEndPoint;
		private PointF mTopPoint;
		private int mHeight;

		public LeParabolaView(Context context, PointF startPoint, PointF endPoint, final View shakeView) {
			super(context);

			initPoint(startPoint, endPoint);

			mImageView = new ImageView(context);
			mImageView.setImageResource(R.drawable.open_in_background_anim);
			addView(mImageView);

			initAnimator(shakeView);

		}

		private void initPoint(PointF startPoint, PointF endPoint) {
			mHeight = LeUI.getDensityDimen(getContext(), IMAGE_HEIGHT);

			float endX = endPoint.x - mHeight / 2f;
			float endY = endPoint.y - mHeight / 2f;
			float startX = startPoint.x - mHeight / 2f;
			float startY = startPoint.y - mHeight / 2f;
			mStartPoint = new PointF(startX, startY);
			mEndPoint = new PointF(endX,endY);

			float middle = mStartPoint.x + (mEndPoint.x - mStartPoint.x) *1f/3f;
			mTopPoint = new PointF(middle, mStartPoint.y - LeUI.getDensityDimen(getContext(), TOP_HEIGHT));
		}

		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		private void initAnimator(final View shakeView) {
			ObjectAnimator showAnimator = ObjectAnimator.ofFloat(mImageView, "alpha", 0f, 1f);
			showAnimator.setDuration(ALPHA_SHOW_DURATION * MUL);

			ObjectAnimator hideAnimator = ObjectAnimator.ofFloat(mImageView, "alpha", 1f, 0f);
			hideAnimator.setStartDelay(ALPHA_HIDE_DELAY * MUL);
			hideAnimator.setDuration(ALPHA_HIDE_DURATION * MUL);

			ObjectAnimator shack = ObjectAnimator.ofFloat(shakeView, "", 0f, 1f);
			shack.setInterpolator(new AccelerateInterpolator());
			shack.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator valueAnimator) {
					//在缩放比为1的抖动动画方程式为：sin(t*T*2Math.PI)/tan(t*0.1f)*0.03 +１：sin函数负责振动，1/tan函数负责衰减
					float cVal = (Float) valueAnimator.getAnimatedValue();
					cVal = (float) ((Math.sin(cVal * 6 * Math.PI)) / Math.tan(cVal + 0.1f) * 0.03f + 1);
					shakeView.setScaleX(cVal);
					shakeView.setScaleY(cVal);
				}
			});
			shack.addListener(new Animator.AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animator) {
					if (mListener != null) {
						mListener.onShakeStart();
					}
				}

				@Override
				public void onAnimationEnd(Animator animator) {

				}

				@Override
				public void onAnimationCancel(Animator animator) {

				}

				@Override
				public void onAnimationRepeat(Animator animator) {

				}
			});
			shack.setDuration(SHACK_DUTATION * MUL);

			ValueAnimator translate = createTranslateInterpolator();

			mAnimatorSet = new AnimatorSet();
			mAnimatorSet.addListener(new Animator.AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animator) {

				}

				@Override
				public void onAnimationEnd(Animator animator) {
					if (mListener != null) {
						mListener.onTotalAnimatorEnd();
					}
				}

				@Override
				public void onAnimationCancel(Animator animator) {

				}

				@Override
				public void onAnimationRepeat(Animator animator) {

				}
			});

			//动画策略：　终点在起点下方：先出现，延迟６０秒后移动，移动快到终点时消失。移动到终点时要放大的Ｖｉｅｗ同时放大；
			//如果终点在起点上方，则没有路径动画
			if (mEndPoint.y > mStartPoint.y) {
				mAnimatorSet.play(showAnimator).with(translate);
				mAnimatorSet.play(translate).with(hideAnimator);
				mAnimatorSet.play(translate).before(shack);
			} else {
				mImageView.setVisibility(View.INVISIBLE);
				mAnimatorSet.play(shack);
			}
		}

		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		private ValueAnimator createTranslateInterpolator() {
			ValueAnimator translate = new ValueAnimator();
			translate.setDuration(TRANSLATE_DURATION * MUL);
			translate.setStartDelay(TRANSLATE_DELAY * MUL);
			translate.setObjectValues(mStartPoint, mEndPoint);
			translate.setInterpolator(new AccelerateInterpolator());
			translate.setEvaluator(new TypeEvaluator<PointF>() {
				@Override
				public PointF evaluate(float fraction, PointF startValue, PointF endValue) {

					//x方向上匀速运动
					//y方向上抛物线公式顶点式a*(Math.pow(t,2)-h)+k = y, 其中h,k是顶点的坐标，
					//分别对应TIME_TO_REACH_TOP和mTopPoint.y， 分别代入起点或者终点坐标，得到两个分段的抛物线；
					PointF point = new PointF();
					point.x = (mEndPoint.x - startValue.x) * fraction + startValue.x;
					if (fraction < TIME_TO_REACH_TOP) {
						point.y = (float) ((startValue.y - mTopPoint.y) / Math.pow(TIME_TO_REACH_TOP, 2) * Math.pow((fraction - TIME_TO_REACH_TOP), 2) + mTopPoint.y);
					} else {
						point.y = (float) ((mEndPoint.y - mTopPoint.y) / Math.pow((1f - TIME_TO_REACH_TOP), 2) * Math.pow((fraction - TIME_TO_REACH_TOP), 2) + mTopPoint.y);
					}
					return point;
				}
			});
			translate.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					PointF point = (PointF) animation.getAnimatedValue();
					mImageView.setX(point.x);
					mImageView.setY(point.y);
				}
			});
			return  translate;
		}

		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		public void startAnimator(LeParabolaListener listener) {
			mListener = listener;
			mAnimatorSet.start();
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			int width = MeasureSpec.getSize(widthMeasureSpec);
			int height = MeasureSpec.getSize(heightMeasureSpec);
			setMeasuredDimension(width, height);
			LeUI.measureExactly(mImageView, mHeight, mHeight);
		}

		@Override
		protected void onLayout(boolean changed, int l, int t, int r, int b) {
			LeUI.layoutViewAtPos(mImageView, Math.round(mStartPoint.x), Math.round(mStartPoint.y));
		}

	}

	interface LeParabolaListener {
		void onShakeStart();
		void onTotalAnimatorEnd();
	}
}
