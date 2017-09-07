package com.beiying.fitmanager;

import android.app.Activity;
import android.app.Application;

import com.beiying.fitmanager.core.ContextContainer;
import com.beiying.fitmanager.core.LeCoreManager;
import com.beiying.fitmanager.manager.LeFileManager;

public class BYBasicContainer extends ContextContainer {
	protected static BYMainActivity sMainActivity;

	public static void notifyAppStart(Application application) {
		applicationStart(application);
		LeFileManager.init(application);
	}

	public static void switchActivity(Activity activity) {
		LeCoreManager.init(LeApplicationHelper.PACKAGE, activity, null,
				null,
				true);
		if (activity instanceof BYMainActivity) {
			sMainActivity = (BYMainActivity) activity;
		}
	}
}
