package com.beiying.fitmanager.core.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import com.beiying.fitmanager.core.LeLog;


public class LeToggleButton extends LeView implements LeProcessor.LeProcessListener, LeThemable {
	private static final int MOVE_COUNT_THRESHOLD = 3;
	
	private static final int UI_WIDTH = 80;
	private static final int UI_HEIGHT = 32;
	private static final int UI_CLICK_THRESHOLD = 5;
	private static final int UI_PADDING = 16;
	
	private static final int DURATION = 100;

	private boolean mIsChecked;

	private Drawable mOffBg;
	private Drawable mOnBg;
	private Drawable mOffThumb;
	private Drawable mOnThumb;
	
	private int mDownX;
	private int mDownY;
	private int mMoveX;
	private int mMoveY;
	
	private int mMoveCount = 0;
	private boolean mShouldHandleTouch = true;
	
	private int mPadding;
	
	private LeToggleController mController;
	private LeProcessor mProcessor;
	
	private LeToggleButtonListener mListener;

	public LeToggleButton(Context context) {
		super(context);
		
		mController = new LeToggleController();
		mController.setActionType(LeDrawerAnimationController.ACTION_NONE);
		mController.setStateType(LeDrawerAnimationController.STATE_FOLD);
		mProcessor = new LeProcessor();
		mProcessor.setProcessListener(this);
		
		setWillNotDraw(false);
		initResources();

		mIsChecked = false;
	}
	
	private void initResources() {
		final DisplayMetrics dm = getResources().getDisplayMetrics();
		mPadding = (int) (UI_PADDING * dm.density);
	}
	
	public void setDefault(boolean isChecked) {
		mIsChecked = isChecked;
		if (mIsChecked) {
			mController.setStateType(LeDrawerAnimationController.STATE_EXPAND);
			mProcessor.setCurrProcess(1.0f);
		} else {
			mController.setStateType(LeDrawerAnimationController.STATE_FOLD);
			mProcessor.setCurrProcess(0.0f);
		}
		postInvalidate();
	}

	public void setChecked(boolean isChecked) {
		if (mIsChecked != isChecked) {
			if (isChecked) {
				mController.startShowAnimation();
			} else {
				mController.startDismissAnimation();
			}
		}
	}

	public boolean isChecked() {
		return mIsChecked;
	}
	
	public void setOffBg(Drawable drawable) {
		mOffBg = drawable;
	}
	
	public void setOnBg(Drawable drawable) {
		mOnBg = drawable;
	}
	
	public void setOffThumb(Drawable drawable) {
		mOffThumb = drawable;
	}
	
	public void setOnThumb(Drawable drawable) {
		mOnThumb = drawable;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		checkShouldHandleTouch();
		getParent().requestDisallowInterceptTouchEvent(mShouldHandleTouch);

		final int thumbWidth = mOffThumb.getIntrinsicWidth();
		final int pivotWidth = mOffBg.getIntrinsicWidth() - thumbWidth;
		float process = 0;
		if (mIsChecked) {
			process = 1 - (mDownX - event.getX()) / pivotWidth;
		} else {
			process = (event.getX() - mDownX) / pivotWidth;

		}
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mDownX = (int) event.getX();
			mDownY = (int) event.getY();
			mMoveCount = 0;
			mShouldHandleTouch = true;
			setPressed(true);
			break;
		case MotionEvent.ACTION_MOVE:
			if (mDownX == -1 || mDownY == -1) {
				mDownX = (int) event.getX();
				mDownY = (int) event.getY();
			}
			checkShouldHandleTouch();
			if (!isClick(event) && mShouldHandleTouch && mMoveCount > MOVE_COUNT_THRESHOLD) {
				setPressed(false);
				if (mIsChecked) {
					mController.simulateDismissAnimation(process);
				} else {
					mController.simulateShowAnimation(process);
				}
			}
			if (mMoveCount == MOVE_COUNT_THRESHOLD) {
				mMoveX = (int) event.getX();
				mMoveY = (int) event.getY();
			}
			mMoveCount++;
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			setPressed(false);
			if (isClick(event)) {
				if (mIsChecked) {
					mController.startDismissAnimation();
				} else {
					mController.startShowAnimation();
				}
			} else {
				if (mShouldHandleTouch && mMoveCount > MOVE_COUNT_THRESHOLD) {
					LeLog.e("gyy:Animation");
					if (process < 0.5f) {
						mController.startDismissAnimation(process);
					} else {
						mController.startShowAnimation(process);
					}
				}
			}
			mDownX = -1;
			mDownY = -1;
			mMoveCount = 0;
			mShouldHandleTouch = true;
			break;
		default:
			break;
		} 
		return true;
	}
	
	private void checkShouldHandleTouch() {
		if (mMoveCount < MOVE_COUNT_THRESHOLD) {
			mShouldHandleTouch = true;
		} else {
			if (Math.abs(mDownX - mMoveX) > Math.abs(mDownY - mMoveY)) {
				mShouldHandleTouch = true;
			} else {
				mShouldHandleTouch = false;
			}
		}
	}
	
	
	private boolean isClick(MotionEvent event) {
		final DisplayMetrics dm = getResources().getDisplayMetrics();
		final int clickThreshold = (int) (UI_CLICK_THRESHOLD * dm.density);
		return (Math.abs(event.getX() - mDownX) < clickThreshold && Math.abs(event.getY() - mDownY) < clickThreshold);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (mOffBg == null) {
			final DisplayMetrics dm = getResources().getDisplayMetrics();
			setMeasuredDimension((int) (UI_WIDTH * dm.density), (int) (UI_HEIGHT * dm.density));
		} else {
			setMeasuredDimension(mOffBg.getIntrinsicWidth() + mPadding * 2, mOffBg.getIntrinsicHeight() + mPadding * 2);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		final int thumbWidth = mOffThumb.getIntrinsicWidth();
		final int pivotWidth = mOffBg.getIntrinsicWidth() - thumbWidth;
		final float process = mProcessor.getCurrProcess();
		
		LeUI.drawDrawable(canvas, mOffBg, mPadding, mPadding);
		
		if (process != 0) {
			mOnBg.setBounds(mPadding, mPadding, mPadding + (int) (pivotWidth * process + thumbWidth), mPadding + mOnBg.getIntrinsicHeight());
			mOnBg.draw(canvas);
		}
		
		int offsetX;
		offsetX = (int) (pivotWidth * process);
		if (process == 0) {
			LeUI.drawDrawable(canvas, mOffThumb, offsetX + mPadding, mPadding);
		} 
		
		mOnThumb.setAlpha((int) (255 * process));
		LeUI.drawDrawable(canvas, mOnThumb, offsetX + mPadding, mPadding);
		
		computeAnimation();
	}
	
	@Override
	protected void drawableStateChanged() {
		super.drawableStateChanged();
		if (mOnBg != null) {
			mOnBg.setState(getDrawableState());
			mOffBg.setState(getDrawableState());
		}
	}
	
	private void computeAnimation() {
		if (mProcessor.computeProcessOffset()) {
			invalidate();
		}
	}
	
	@Override
	public void onProcessEnd() {
		if (mProcessor.getCurrProcess() == 0) {
			mController.onDismissAnimationFinished();
		} else {
			mController.onShowAnimationFinished();
		}
	}

	public interface LeToggleButtonListener {
		void onToggleButtonClick(boolean isChecked);
	}

	public void setToggleButtonListener(LeToggleButtonListener listener) {
		mListener = listener;
	}
	
	@Override
	public void onThemeChanged() {
		drawableStateChanged();
	}
	
	private class LeToggleController extends LeDrawerAnimationController {
		
		@Override
		protected void startShowAnimationInner(float process) {
			mProcessor.startProcess(process, 1.0f, (int) (DURATION * (1 - process)));
			invalidate();
		}

		@Override
		protected void startDismissAnimationInner(float process) {
			mProcessor.startProcess(process, 0f, (int) (DURATION * process));
			invalidate();
		}

		@Override
		protected void simulateShowAnimationInner(float process) {
			mProcessor.setCurrProcess(process);
			invalidate();
		}

		@Override
		protected void simulateDismissAnimationInner(float process) {
			mProcessor.setCurrProcess(process);
			invalidate();
		}

		@Override
		protected void showWithoutAnimationInner() {
			mProcessor.setCurrProcess(1f);
			invalidate();
		}

		@Override
		protected void dismissWithoutAnimationInner() {
			mProcessor.setCurrProcess(0f);
			invalidate();
		}

		@Override
		protected void onShowAnimationFinishedInner() {
			mIsChecked = true;
			if (mListener != null) {
				mListener.onToggleButtonClick(mIsChecked);
			}
		}

		@Override
		protected void onDismissAnimationFinishedInner() {
			mIsChecked = false;
			if (mListener != null) {
				mListener.onToggleButtonClick(mIsChecked);
			}
		}
		
	}

}
