/** 
 * Filename:    LeMovableButton.java
 * Description:  
 * Copyright:   Lenovo PCL Copyright(c)2013 
 * @author:     chenwei27 
 * @version:    1.0
 * Create at:   2013-8-12 下午1:38:04
 * 
 * Modification History: 
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------ 
 * 2013-8-12     chenwei27    1.0         1.0 Version 
 */
package com.beiying.fitmanager.core.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewGroup;

import com.beiying.fitmanager.core.LeLog;


public class LeMovableButton extends LeIconButton {
	
	private static final boolean DEBUG = false;

	protected static final long LONG_CLICK_TIME = 500;

	private static final int UI_PADDING_X = 10;
	private static final int UI_PADDING_Y = 10;

	private static final int MAX_TOLERABLE_DX = 5;
	private static final int MAX_TOLERABLE_DY = 5;
	
	private static final int MOVE_LOCK_DISTANCE = 20;

	//竖屏portrait坐标
	protected int mPLayoutX = -1;
	protected int mPLayoutY = -1;

	//横屏landscape坐标
	protected int mLLayoutX = -1;
	protected int mLLayoutY = -1;

	//真正布局坐标
	public int mLayoutX;
	public int mLayoutY;
	
	int mDownX;
	int mDownY;

	int startX;
	int startY;
	int lastX;
	int lastY;
	int lX = 0;
	int lY = 0;
	boolean mMoved = false;

	protected Rect mAcitiveRect;
	protected boolean mAutoAdjust;

	private boolean mFreeLayoutFlag;

	private boolean mMovingFlag;
	
	private boolean mMoveEnabled;
	private int mMoveLockDistance;

	protected long mStartTime;
	private long mEndTime;

	private LeOnMovableClickListener mClickListener;
	private LeOnMovableLongClickListener mLongClickListener;
	
	protected VelocityTracker mVelocityTracker;
	
	private boolean mEnabled = true;

	public LeMovableButton(Context context, Rect rect) {
		super(context);

		mAcitiveRect = rect;

		mFreeLayoutFlag = true;

		if (mAcitiveRect == null) {
			mAutoAdjust = true;
			DisplayMetrics dm = getResources().getDisplayMetrics();
			int paddingX = 0;
			int paddingY = 0;
			int width = dm.widthPixels;
			int height = dm.heightPixels - 75;

			mAcitiveRect = new Rect(paddingX, paddingY, paddingX + width, paddingY + height);
		}
		
		initResources(context);

		mVelocityTracker = VelocityTracker.obtain();
	}
	
	@Override
	protected void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
		if (mAutoAdjust) {
			int l = mAcitiveRect.top;
			int t = mAcitiveRect.left;
			int r = l + mAcitiveRect.height();
			int b = t + mAcitiveRect.width();
			
			mAcitiveRect.left = l;
			mAcitiveRect.top = t;
			mAcitiveRect.right = r;
			mAcitiveRect.bottom = b;
		}
	}
	
	private void initResources(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		mMoveLockDistance = (int) (MOVE_LOCK_DISTANCE * dm.density);
	}
	
	public void enable(boolean enable) {
		mEnabled = enable;
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if (!mEnabled) {
			return super.dispatchTouchEvent(event);
		}
		int ea = event.getAction();
		if (ea == MotionEvent.ACTION_DOWN) {
			mVelocityTracker.clear();
		}
		mVelocityTracker.addMovement(event);
		switch (ea) {
			case MotionEvent.ACTION_DOWN:
				mStartTime = System.currentTimeMillis();
				startX = getLeft();
				startY = getTop();
				mDownX = lastX = (int) event.getRawX();
				mDownY = lastY = (int) event.getRawY();
				mMoveEnabled = false;
				break;
			case MotionEvent.ACTION_MOVE:
				int rawX = (int) event.getRawX();
				int rawY = (int) event.getRawY();
				int dx = rawX - lastX;
				int dy = rawY - lastY;
				if (DEBUG) {
					LeLog.i("movable dx dy:(" + dx + ", " + dy + ")");
				}
				lastX = rawX;
				lastY = rawY;

				int movedX = rawX - mDownX;
				int movedY = rawY - mDownY;
				long now = System.currentTimeMillis();
				long dTime = now - mStartTime;
				onTouching(dTime);
				if (!mMoveEnabled) {
					if (Math.abs(dx) < MAX_TOLERABLE_DX && Math.abs(dy) < MAX_TOLERABLE_DY) {
						break;
					}
					if (Math.max(Math.abs(movedX), Math.abs(movedY)) < mMoveLockDistance) {
						break;
					}
				}
				mMoveEnabled = true;

				mMovingFlag = true;

				int l = getLeft() + dx;
				int b = getBottom() + dy;
				int r = getRight() + dx;
				int t = getTop() + dy;
				int width = getMeasuredWidth();
				int height = getMeasuredHeight();
				if (l < mAcitiveRect.left) {
					l = mAcitiveRect.left;
					r = l + width;
				}

				if (t < mAcitiveRect.top) {
					t = mAcitiveRect.top;
					b = t + height;
				}

				if (r > mAcitiveRect.right) {
					r = mAcitiveRect.right;
					l = r - width;
				}

				if (b > mAcitiveRect.bottom) {
					b = mAcitiveRect.bottom;
					t = b - height;
				}
				layout(l, t, r, b);

				lX = l;
				lY = t;
				postInvalidate();

				mLayoutX = lX;
				mLayoutY = lY;
				dx = lX - startX;
				dy = lY - startY;
				if (!mMoved) {
					onMoveStart(mLayoutX, mLayoutY, dx, dy);
				} else {
					onMove(mLayoutX, mLayoutY, dx, dy);
				}
				mMoved = true;

				// LeLog.i("cw movable move x:" + lX + "; y:" + lY);
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				mEndTime = System.currentTimeMillis();
				if (mMoved) {
					setPoint(lX, lY, "action_up");

					mMoved = false;

					dx = mLayoutX - startX;
					dy = mLayoutY - startY;
					onMoveCompleted(mLayoutX, mLayoutY, dx, dy);
				} else {

					if (isLongClick()) {
						if (mLongClickListener != null) {
							mLongClickListener.onLongClick(this);
						}
					} else {
						if (mClickListener != null) {
							mClickListener.onClick(LeMovableButton.this);
						}
					}
				}
				mMovingFlag = false;
				break;

			default:
				break;
		}
		return super.dispatchTouchEvent(event);
	}

	private boolean isLongClick() {
		long touchTime = mEndTime - mStartTime;
		if (touchTime > LONG_CLICK_TIME) {
			return true;
		}
		return false;
	}

	/**
	 * 强行设置坐标
	 * 
	 * @param x
	 * @param y
	 */
	public void setPoint(int x, int y, String tag) {
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			mLayoutX = mPLayoutX = x;
			mLayoutY = mPLayoutY = y;
		} else {
			mLayoutX = mLLayoutX = x;
			mLayoutY = mLLayoutY = y;
		}
		if (DEBUG) {
			LeLog.i("CW", "<<" + tag + ">> force set point:(" + mLayoutX + ", " + mLayoutY + ")");
		}
	}

	public int getOrientationX() {
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			return mPLayoutX;
		} else {
			return mLLayoutX;
		}
	}

	public int getOrientationY() {
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			return mPLayoutY;
		} else {
			return mLLayoutY;
		}
	}

	public void adaptScreen( Point defaultPoint) {
		setLayoutPoint();

		if (!mAcitiveRect.contains(mLayoutX, mLayoutY)) {
			if (defaultPoint != null) {
				setPoint(defaultPoint.x, defaultPoint.y, "adaptScreen");
			} else {
				resetPostion();
				setLayoutPoint();
			}
		}
	}

	private void setLayoutPoint() {
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			mLayoutX = mPLayoutX;
			mLayoutY = mPLayoutY;
		} else {
			mLayoutX = mLLayoutX;
			mLayoutY = mLLayoutY;
		}
	}

	protected void resetPostion() {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		int paddingRight = (int) (UI_PADDING_X * dm.density);
		int paddingBottom = (int) (UI_PADDING_Y * dm.density);

		setPoint(mAcitiveRect.right - paddingRight - getMeasuredWidth(), mAcitiveRect.bottom - paddingBottom
				- getMeasuredHeight(), "resetPostion");
	}

	protected void onMoveStart(int lastX, int lastY, int dx, int dy) {

	}
	
	protected void onTouching(long time) {
		
	}

	protected void onMove(int lastX, int lastY, int dx, int dy) {

	}

	protected void onMoveCompleted(int lastX, int lastY, int dx, int dy) {

	}

	public void layoutInParent(ViewGroup parent) {
		if (parent == null) {
			return;
		}
		int offsetX, offsetY;
		if (mFreeLayoutFlag) {
			offsetX = mLayoutX;
			offsetY = mLayoutY;
		} else {
			DisplayMetrics dm = parent.getResources().getDisplayMetrics();
			int paddingRight = (int) (UI_PADDING_X * dm.density);
			int paddingBottom = (int) (UI_PADDING_Y * dm.density);

			offsetX = parent.getMeasuredWidth() - paddingRight - getMeasuredWidth();
			offsetY = parent.getMeasuredHeight() - paddingBottom - getMeasuredHeight();
		}
		layout(offsetX, offsetY, offsetX + getMeasuredWidth(), offsetY + getMeasuredHeight());

		if (DEBUG) {
			LeLog.i("CW", "layoutx:" + offsetX + "; layouty:" + offsetY);
		}
	}

	public boolean getMovingFlag() {
		return mMovingFlag;
	}

	public void disableFreeMove() {
		mFreeLayoutFlag = false;
	}

	public void enableFreeMove() {
		mFreeLayoutFlag = true;
	}
	
	public boolean inActiveRect() {
		if (DEBUG) {
			LeLog.i("active rect: [" + mAcitiveRect.left + ", " + mAcitiveRect.top + ", "
					+ mAcitiveRect.right + ", " + mAcitiveRect.bottom + "]-(" + mLayoutX + ", " + mLayoutY
					+ ")");
		}
		return mAcitiveRect.contains(mLayoutX, mLayoutY);
	}

	public void setActiveRect(Rect rect, Point defaultPoint) {
		mAcitiveRect = rect;

		adaptScreen(defaultPoint);
	}
	
	public void setActiveRect(Rect rect) {
		setActiveRect(rect, null);
	}

	public void setOnMovableClickListener(LeOnMovableClickListener listener) {
		mClickListener = listener;
	}
	
	public void setOnMovableLongClickListener(LeOnMovableLongClickListener listener) {
		mLongClickListener = listener;
	}
	
	public void setLockDistance(int lock) {
		mMoveLockDistance = lock;
	}

	public interface LeOnMovableClickListener {
		void onClick(LeMovableButton movableButton);
	}

	public interface LeOnMovableLongClickListener {
		void onLongClick(LeMovableButton movableButton);
	}
}
