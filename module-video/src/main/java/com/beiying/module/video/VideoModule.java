package com.beiying.module.video;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.beiying.annotation.LayoutLevel;
import com.beiying.annotation.ModuleUnit;
import com.beiying.base.modules.BYBasicModule;
import com.beiying.base.modules.BYModuleContext;
import com.beiying.base.modules.BYModuleImpl;
import com.beiying.fitmanager.core.LeLog;

@ModuleUnit(templet = "top",layoutLevel = LayoutLevel.HIGHT,extralevel = 10)
public class VideoModule extends BYBasicModule implements BYModuleImpl {

	private MVideoRootView mVideoRootView;

	@Override
	public boolean init(BYModuleContext moduleContext, Bundle extend) {
		super.init(moduleContext, extend);
		initViews(moduleContext.getActivity());
		return true;
	}

	private void initViews(Context context) {
		mVideoRootView = new MVideoRootView(context);
		parentTop.addView(mVideoRootView);
	}

	@Override
	public void onLoad(Application app) {
		for (int i=0;i<5;i++){
			LeLog.e("VideoCreate","VideoModule Loading");
		}
	}
}

