package com.beiying.fitmanager.manager;

import android.content.Context;

import com.beiying.fitmanager.R;

public class LeLanguageManager {
	
	private static final String CHINESE = "CN";
	private static final String CHINESE_TW = "CN_TW";
	private static final String ENGLISH = "EN";
	
	private LeLanguageManager(){}
	
	public static String getLanguageString(Context context) {
		if (context == null) {
			return CHINESE;
		}
		return context.getResources().getString(R.string.language);
	}
	
	public static boolean isChinese(Context context) {
		if (getLanguageString(context).equals(CHINESE)) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isChinese_TW(Context context) {
		if (getLanguageString(context).equals(CHINESE_TW)) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isEnglish(Context context) {
		if (getLanguageString(context).equals(ENGLISH)) {
			return true;
		} else {
			return false;
		}
	}
}
