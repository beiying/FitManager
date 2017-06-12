package com.beiying.fitmanager.core.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

public class LeBgButton extends LeButton {
	
	protected static final int DEFAULT_FOCUSED_COLOR = 0xff2cadf1;
	
	protected Drawable mNormalBg;
	protected Drawable mPressBg;
	protected Drawable mDisableBg;
	protected Drawable mFocusBg;
	
	public LeBgButton(Context context) {
		super(context);
		
		setWillNotDraw(false);
	}
	
	public void setNormalBgColor(int color) {
		mNormalBg = new ColorDrawable(color);
	}
	
	public void setPressBgColor(int color) {
		mPressBg = new ColorDrawable(color);
	}
	
	public void setDisableBgColor(int color) {
		mDisableBg = new ColorDrawable(color);
	}
	
	public void setFocusBgColor(int color) {
		mFocusBg = new ColorDrawable(color);
	}
	
	public void setNormalBgDrawable(int resId) {
		mNormalBg = getResources().getDrawable(resId);
	}
	
	public void setPressBgDrawable(int resId) {
		mPressBg = getResources().getDrawable(resId);
	}
	
	public void setDisableBgDrawable(int resId) {
		mDisableBg = getResources().getDrawable(resId);
	}
	
	public void setFocusBgDrawable(int resId) {
		mFocusBg = getResources().getDrawable(resId);
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if (!isEnabled()) {
			if (mDisableBg != null) {
				mDisableBg.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
				mDisableBg.draw(canvas);
			}
		} else if (isPressed()) {
			if (mPressBg != null) {
				mPressBg.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
				mPressBg.draw(canvas);
			}
		} else if (isFocused()) {
			if (mFocusBg != null) {
				mFocusBg.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
				mFocusBg.draw(canvas);
			}
		} else {
			if (mNormalBg != null) {
				mNormalBg.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
				mNormalBg.draw(canvas);
			}
		}
	}
}
