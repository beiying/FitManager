package com.beiying.fitmanager.core.ui;

import android.content.Context;

public class LePopContent extends LeView {
	
	protected int mWidth;
	protected int mHeight;
	
	protected PopCallbak mCommonCallback;

	public LePopContent(Context context) {
		super(context);
	}
	
	public int getContentWidth() {
		return getMeasuredWidth();
	}
	
	public int getContentHeight() {
		return getMeasuredHeight();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		
	}

	public interface PopCallbak {
		void onDismiss();
	}
}
