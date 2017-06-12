package com.beiying.fitmanager.core.ui;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.beiying.core.R;


public class LeButton extends ViewGroup implements LeThemable {
	
	protected int mFocusedTextColor;
	
	protected Paint mFocusedPaint;

	public LeButton(Context context) {
		this(context, null);
	}
	
	public LeButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public LeButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init() {
		setClickable(true);
		setWillNotDraw(false);
		setFocusable(true);
		
		mFocusedTextColor = getResources().getColor(R.color.common_focused);
		mFocusedPaint = new Paint();
		mFocusedPaint.setColor(mFocusedTextColor);
	}
	
	@Override
	public void onThemeChanged() {
		
	}
	
	@Override
	public void setPressed(boolean pressed) {
		super.setPressed(pressed);
		invalidate();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		
	}

}
