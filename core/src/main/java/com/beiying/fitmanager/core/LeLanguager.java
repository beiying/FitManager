package com.beiying.fitmanager.core;

import android.content.Context;

public class LeLanguager {
	
	private static final String CHINESE = "CN";
	private static final String CHINESE_TW = "CN_TW";
	private static final String ENGLISH = "EN";
	
	private LeLanguager(){}
	
	private static String getLanguageString(Context context) {
	    	return context.getResources().getString(LeCoreR.getStringResId("language"));
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
