package com.beiying.fitmanager.core.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

public class LeViewGroup extends ViewGroup implements LeThemable {

	public LeViewGroup(Context context) {
		this(context, null);
	}

	public LeViewGroup(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LeViewGroup(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void setPressed(boolean pressed) {
		super.setPressed(pressed);
		invalidate();
	}

	@Override
	public void onThemeChanged() {
		
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		
	}
}
