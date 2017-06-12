/** 
 * Filename:    LeslideController.java
 * Description:  
 * Copyright:   Lenovo PCL Copyright(c)2013 
 * @author:     chenwei27 
 * @version:    1.0
 * Create at:   2013-11-7 下午4:33:48
 * 
 * Modification History: 
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------ 
 * 2013-11-7     chenwei27    1.0         1.0 Version 
 */
package com.beiying.fitmanager.core.ui;

import android.view.MotionEvent;
import android.view.VelocityTracker;

import com.beiying.fitmanager.core.LeLog;


public class LeSlideController {
	
	private static final boolean DEBUG = false;
	
	private static final int MAX_TOLERABLE_DX = 5;
	private static final int MAX_TOLERABLE_DY = 5;
	
	private static final int SLIDE_VELOCITY = 500;//3500;
	
	private boolean mMovingFlag;

	private int mLastX;
	private int mLastY;
	private int mStartX;
	private int mStartY;
	private boolean mMoved = false;
	private boolean mMoveHandled;
	private boolean mHasSecondPoint = false;
	
	private boolean mMoveEnabled;

	private boolean mXOrientation;
	
	private VelocityTracker mVelocityTracker;
	
	private boolean mLocked;
	
	public LeSlideController(boolean xOrientation) {
		mXOrientation = xOrientation;
		
		mVelocityTracker = VelocityTracker.obtain();
	}
	
	public synchronized void onTouchEvent(MotionEvent event) {
		int ea = event.getAction();
		if (ea == MotionEvent.ACTION_DOWN) {
			resetStates();
			
			if (DEBUG) {
				LeLog.i("cw slide reset states");
			}
		}
		if (mLocked) {
			return ;
		}
		if (event.getPointerCount() > 1) {
			mHasSecondPoint = true;
		}
		if (mHasSecondPoint) {
			return ;
		}
		
		mVelocityTracker.addMovement(event);
		
		switch (ea) {
			case MotionEvent.ACTION_DOWN:
				mLastX = (int) event.getRawX();
				mStartX = (int) event.getRawX();
				mLastY = (int) event.getRawY();
				mStartY = (int) event.getRawY();
				onTouchDown();
				if (DEBUG) {
					LeLog.i("cw slide down");
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if (!mMoveEnabled) {
					break;
				}
				if (mMoveHandled) {
					break;
				}
				int dx = (int) event.getRawX() - mLastX;
				int dy = (int) event.getRawY() - mLastY;

				if (mXOrientation && (Math.abs(dx) < MAX_TOLERABLE_DX/* || Math.abs(dx) < Math.abs(dy)*/)) {
					break;
				}
				if (!mXOrientation && (Math.abs(dy) < MAX_TOLERABLE_DY/* || Math.abs(dy) < Math.abs(dx)*/)) {
					break;
				}
				if (!isVelocityQuickEnough()) {
					//LeLog.i("cw slide too slow");
					break;
				}
				int currX = (int) event.getRawX();
				int currY = (int) event.getRawY();
				mMoveEnabled = couldMove(mStartX, mStartY, currX, currY);
				//LeLog.i("cw slide moveenable:" + mMoveEnabled);
				if (!mMoveEnabled) {
					break;
				}
				mLastX = currX;
				mLastY = currY;
				
				mMoved = true;
				realMove();

				if (onMove(mStartX, mStartY, mLastX, mLastY)) {
					mMoveHandled = true;
				}
				if (DEBUG) {
					LeLog.i("cw slide move");
				}
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:

				mLastX = (int) event.getRawX();
				mLastY = (int) event.getRawY();

				onMoveCompleted(mMoved, mStartX, mStartY, mLastX, mLastY);
				if (DEBUG) {
					LeLog.i("cw slide move completed:" + mMoved);
				}
				mMoved = false;
				mMovingFlag = false;
				if (DEBUG) {
					LeLog.i("cw slide up");
				}
				break;
			default:
				break;
		}
	}
	
	private boolean isVelocityQuickEnough() {
		final VelocityTracker velocityTracker = mVelocityTracker;
		velocityTracker.computeCurrentVelocity(1000);
		float velocity;
		if (mXOrientation) {
			velocity = velocityTracker.getXVelocity();
		} else {
			velocity = velocityTracker.getYVelocity();
		}
		float vValue = Math.abs(velocity);
		//LeLog.i("cw slide v:" + vValue);
		if (vValue > SLIDE_VELOCITY) {
			return true;
		}
		return false;
	}
	
	protected void resetStates() {
		mMoved = false;
		mMoveHandled = false;
		mMovingFlag = false;
		mMoveEnabled = true;
		mHasSecondPoint = false;
	}

	private void realMove() {
		if (!mMovingFlag) {
			mMovingFlag = true;

			onMoveStart();
		}
	}

	protected void onTouchDown() {

	}

	protected void onMoveStart() {

	}

	protected boolean onMove(int startX, int startY, int lastX, int lastY) {
		//LeLog.i("cw slide start:" + startX + " ; now:" + lastX);
		return false;
	}

	protected void onMoveCompleted(boolean moved, int startX, int startY, int endX, int endY) {

	}
	
	protected boolean couldMove(int startX, int startY, int lastX, int lastY) {
		return true;
	}
	
	public boolean isMoving() {
		return mMovingFlag;
	}
	
	public synchronized void lock() {
		mLocked = true;
	}
	
	public synchronized void unlock() {
		mLocked = false;
	}
}
