package com.beiying.fitmanager;

import android.content.Context;
import android.text.TextUtils;

public class LeApplicationHelper {

	public static String PACKAGE = "com.beiying.fitmanager";

	public static void init(Context context) {
		if (context == null) {
			return;
		}
		String pn = context.getApplicationInfo().packageName;
		if (!TextUtils.isEmpty(pn)) {
			PACKAGE = pn;
		}
	}


}
