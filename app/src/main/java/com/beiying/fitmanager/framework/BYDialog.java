package com.beiying.fitmanager.framework;

import android.content.Context;
import android.view.View;

import com.beiying.fitmanager.core.ui.LeViewGroup;

public class BYDialog extends LeViewGroup {

	public BYDialog(Context context) {
		super(context);
	}
	
	public void setContentView(View view) {
		removeAllViews();
		addView(view);
	}

}
