package com.beiying.fitmanager.framework;

import android.view.View;

import com.beiying.fitmanager.BYBasicContainer;
import com.beiying.fitmanager.datacollect.BYDataCollectContentView;

public class BYControlCenter extends BYBasicContainer{

	private static BYControlCenter sInstance;
	private BYFrameworkView mFrameworkView;
	
	public synchronized static BYControlCenter getInstance() {
		if (sInstance == null) {
			sInstance = new BYControlCenter();
		}
		return sInstance;
	}
	
	private BYControlCenter() {
	}
	
	public void init(BYFrameworkView frameworkView) {
		mFrameworkView = frameworkView;
	}
	
	public BYFrameworkView getRootView() {
		return mFrameworkView;
	}
	
	public void showNavigationView() {
		mFrameworkView.showNavigationView();
	}
	
	public void hideNavigationView() {
		mFrameworkView.hideNavigationView();
	}

	public void showInMainView(View view) {
		mFrameworkView.getMainContentView().showContentView(view);
	}
	
	public void showInFeatureView(View view) {
		mFrameworkView.showFeatureView(view);
	}
	
}
