package com.beiying.fitmanager.framework;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.beiying.fitmanager.BYBasicContainer;
import com.beiying.fitmanager.core.BYSafeRunnable;
import com.beiying.fitmanager.framework.featureview.BYFeatureCallback;

public class BYControlCenter extends BYBasicContainer{

	private static BYControlCenter sInstance;
	private BYFrameworkView mFrameworkView;
	private Handler mHandler;
	
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
	
	public void showInFeatureView(View view, BYFeatureCallback callback) {
		mFrameworkView.showFeatureView(view, callback);
	}

	public boolean backFeatureView() {
		return mFrameworkView.backFeatureView(true, false);
	}

	public boolean onBackPressed() {
		if (backFeatureView()) {
			return true;
		}
		return false;
	}

	public void postToUiThread(BYSafeRunnable runnable) {
		if (mHandler == null) {
			mHandler = new Handler(Looper.getMainLooper());
		}
		mHandler.post(runnable);
	}

	public void postToUiThread(BYSafeRunnable runnable, long delayMillis) {
		if (mHandler == null) {
			mHandler = new Handler(Looper.getMainLooper());
		}
		mHandler.postDelayed(runnable, delayMillis);
	}

}
