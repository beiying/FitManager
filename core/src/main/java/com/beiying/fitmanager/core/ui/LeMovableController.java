package com.beiying.fitmanager.core.ui;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

public class LeMovableController {
	
	private static final int DEFAULT_ACTIVE_SIZE = 300;

	private static final int MAX_TOLERABLE_DX = 5;
	private static final int MAX_TOLERABLE_DY = 5;
	
	private Rect mActiveRect;

	private boolean mMovingFlag;

	private LeOnMovableClickListener mListener;

	private int mLayoutX;
	private int mLayoutY;
	
	private boolean mLockX;
	private boolean mLockY;
	
	protected Point mOriginalLayoutPoint;

	private int lastX;
	private int lastY;
	private int lX;
	private int lY;
	private int dx;
	private int dy;
	private boolean mMoved = false;
	
	private Context mContext;
	
	public LeMovableController(Context context) {
		mContext = context;
		mActiveRect = new Rect(0, 0, DEFAULT_ACTIVE_SIZE, DEFAULT_ACTIVE_SIZE);
	}
	
	public void onTouch(MotionEvent event) {
		View targetView = getTargetView();
		
		onTouch(targetView, event);
	}
	
	public void onTouch(View v, MotionEvent event) {
		final float density = mContext.getResources().getDisplayMetrics().density;
		int ea = event.getAction();

		switch (ea) {
			case MotionEvent.ACTION_DOWN:
				lastX = (int) event.getRawX();
				lastY = (int) event.getRawY();
				onTouchDown(v);
				//LeLog.i("cw movable down x:" + mLayoutX + "; y:" + mLayoutY);
				break;
			case MotionEvent.ACTION_MOVE:
				dx = (int) event.getRawX() - lastX;
				dy = (int) event.getRawY() - lastY;
				
				if (v == null) {
					break;
				}
				int l = v.getLeft();
				int b = v.getBottom();
				int r = v.getRight();
				int t = v.getTop();
				int width = v.getMeasuredWidth();
				int height = v.getMeasuredHeight();
				if (!mLockX) {
					if (Math.abs(dx) < MAX_TOLERABLE_DX * density) {
						break;
					}
					if (!couldMove() || Math.abs(dx) < Math.abs(dy)) {
						break;
					}
					mMoved = true;
					realMove();
					l += dx;
					r += dx;
					if (l < mActiveRect.left) {
						l = mActiveRect.left;
						r = l + width;
					}
					if (r > mActiveRect.right) {
						r = mActiveRect.right;
						l = r - width;
					}
					
				}
				if (!mLockY) {
					if (Math.abs(dy) < MAX_TOLERABLE_DY * density) {
						break;
					}
					mMoved = true;
					if (!couldMove() || Math.abs(dy) < Math.abs(dx)) {
						break;
					}
					realMove();
					t += dy;
					b += dy;
					if (t < mActiveRect.top) {
						t = mActiveRect.top;
						b = t + height;
					}
					if (b > mActiveRect.bottom) {
						b = mActiveRect.bottom;
						t = b - height;
					}
					
				}
				v.layout(l, t, r, b);
				
				v.postInvalidate();

				lastX = (int) event.getRawX();
				lastY = (int) event.getRawY();
				lX = l;
				lY = t;
				
				onMove(lX, lY);
				
				//LeLog.i("cw movable move x:" + lX + "; y:" + lY);
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				if (mMoved) {
					setPoint(lX, lY);

					mMoved = false;
					onMoveCompleted(mLayoutX, mLayoutY);
				} else {
					if (mListener != null && couldClick()) {
						dx = (int) event.getRawX() - lastX;
						dy = (int) event.getRawY() - lastY;
						if (Math.abs(dx) < 3 && Math.abs(dy) < 3) {
							mListener.onClick(this);
						}
					}
				}
				onTouchEnd();
				mMovingFlag = false;
				//LeLog.i("cw movable up x:" + mLayoutX + "; y:" + mLayoutY);
				break;
			default:
				break;
		}
	}
	
	private void realMove() {
		if (!mMovingFlag) {
			mMovingFlag = true;
			
			onMoveStart();
		}
	}
	
	public void notifiedByView(View view) {
		if (mOriginalLayoutPoint == null) {
			mOriginalLayoutPoint = new Point(view.getLeft(), view.getTop());
		}
	}

	private void setPoint(int x, int y) {
		mLayoutX = x;
		mLayoutY = y;
	}
	
	protected boolean couldClick() {
		return true;
	}
	
	protected boolean couldMove() {
		return true;
	}
	
	protected View getTargetView() {
		return null;
	}
	
	protected void onTouchDown(View view) {
		
	}
	
	protected void onMoveStart() {
		
	}
	
	protected void onMove(int dx, int dy) {
		
	}

	protected void onMoveCompleted(int x, int y) {

	}
	
	protected void onTouchEnd() {
		
	}
	
	public boolean getMovingFlag() {
		return mMovingFlag;
	}
	
	public void lockX() {
		mLockX = true;
	}
	
	public void lockY() {
		mLockY = true;
	}
	
	public void setActiveRect(Rect rect) {
		mActiveRect = rect;
	}
	
	public void setOnClickListener(LeOnMovableClickListener listener) {
		mListener = listener;
	}
	
	public Rect getActiveRect() {
		return mActiveRect;
	}

	public interface LeOnMovableClickListener {
		void onClick(LeMovableController movableController);
	}
}
