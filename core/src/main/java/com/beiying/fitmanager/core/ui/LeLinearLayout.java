package com.beiying.fitmanager.core.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class LeLinearLayout extends LinearLayout implements LeThemable {

	public LeLinearLayout(Context context) {
		super(context);
	}

	public LeLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void onThemeChanged() {
		
	}
	
	@Override
	public void setContentDescription(CharSequence contentDescription) {
		if (Constants.TEST_VIEW_HIERARCHY) {
			super.setContentDescription(contentDescription + String.valueOf(hashCode()));
		}
	}

}
