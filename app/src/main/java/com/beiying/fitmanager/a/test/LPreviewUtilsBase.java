package com.beiying.fitmanager.a.test;

import android.support.v7.app.ActionBarActivity;

public class LPreviewUtilsBase {
	protected ActionBarActivity mActivity;

	LPreviewUtilsBase(ActionBarActivity activity) {
        mActivity = activity;
    }

    public static LPreviewUtilsBase getInstance(ActionBarActivity activity) {
        return new LPreviewUtilsBase(activity);
    }

}
