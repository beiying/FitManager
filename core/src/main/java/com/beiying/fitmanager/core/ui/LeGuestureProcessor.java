/** 
 * Filename:    LeGuestureProcessor.java
 * Description:  
 * Copyright:   Lenovo PCL Copyright(c)2013 
 * @author:     chenwei27 
 * @version:    1.0
 * Create at:   2014-2-20 下午2:34:50
 * 
 * Modification History: 
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------ 
 * 2014-2-20     chenwei27    1.0         1.0 Version 
 */
package com.beiying.fitmanager.core.ui;

import android.graphics.Point;
import android.view.MotionEvent;

public class LeGuestureProcessor {

	private LeGuestureListener mListener;
	
	private Point mLastDownPoint;
	private Point mLastUpPoint;
	
	public LeGuestureProcessor(LeGuestureListener listener) {
		mListener = listener;
	}
	
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				int x = (int) ev.getRawX();
				int y = (int) ev.getRawY();
				mLastDownPoint = new Point(x, y);
				//LeLog.i("guesture x y:" + x + ";" + y);
				if (mListener != null) {
					mListener.onDown(mLastDownPoint);
				}
				break;
			case MotionEvent.ACTION_MOVE:
				float deltaX = ev.getRawX() - mLastDownPoint.x;
				float deltaY = ev.getRawY() - mLastDownPoint.y;
				if (mListener != null) {
					mListener.onMove(deltaX, deltaY);
				}
				break;
			case MotionEvent.ACTION_UP:
				mLastUpPoint = new Point((int) ev.getRawX(), (int) ev.getRawY());
				break;

			default:
				break;
		}
		return false;
	}

	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return false;
	}

	public boolean onTouchEvent(MotionEvent event) {
		return false;
	}
	
	public Point getLastDownPoint() {
		return mLastDownPoint;
	}
	
	public Point getLastUpPoint() {
		return mLastUpPoint;
	}
	
	public static String motionEventToString(MotionEvent event) {
		String str = "";
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				str = "ACTION_DOWN";
				break;
			case MotionEvent.ACTION_MOVE:
				str = "ACTION_MOVE";
				break;
			case MotionEvent.ACTION_UP:
				str = "ACTION_UP";
				break;
			case MotionEvent.ACTION_CANCEL:
				str = "ACTION_CANCEL";
				break;

			default:
				break;
		}
		return str;
	}
	
	public interface LeGuestureListener {
		void onDown(Point downPoint);
		void onMove(float deltaX, float deltaY);
	}
}
