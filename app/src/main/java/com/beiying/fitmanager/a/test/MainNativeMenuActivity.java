package com.beiying.fitmanager.a.test;

import com.beiying.fitmanager.BYBaseActivity;
import com.beiying.fitmanager.framework.BYRootView;

import android.os.Bundle;


public class MainNativeMenuActivity extends BYBaseActivity {

	private BYRootView mRootView = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(isFinishing()) {
			return;
		}
		if(mRootView == null){
			mRootView = new BYRootView(this);
		}
		setContentView(mRootView);
	}
}
