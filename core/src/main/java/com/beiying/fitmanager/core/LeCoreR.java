package com.beiying.fitmanager.core;


public class LeCoreR extends ContextContainer {

//	public static final class id {
//		 
//	}
//	
//	public static final class string {
//		
//		public static final int language = R.string.language;
//		
//		public static final int phone_or_pad = R.string.phone_or_pad;
//		
//		public static final int date_minute_before = R.string.date_minute_before;
//		public static final int date_hour_before = R.string.date_hour_before;
//		public static final int date_day_before = R.string.date_day_before;
//		public static final int date_month_before = R.string.date_month_before;
//		public static final int date_year_before = R.string.date_year_before;
//		
//	}
	
//	public static final class color {
//		public static final int common_focused = R.color.common_focused;
//	}
	
//	public static final class dimen {
//		public static final int common_title = R.dimen.common_title;
//		public static final int common_unselected_title = R.dimen.common_unselected_title;
//	}
	
	
	public static int getStringResId(String fieldName) {
		if (sContext == null) {
			return 0;
		}
		return sContext.getResources().getIdentifier(fieldName, "string", sPackageName);
	}
	
	public static int getColorResId(String fieldName) {
		if (sContext == null) {
			return 0;
		}
		return sContext.getResources().getIdentifier(fieldName, "color", sPackageName);
	}
	
	public static int getDimenResId(String fieldName) {
		if (sContext == null) {
			return 0;
		}
		return sContext.getResources().getIdentifier(fieldName, "dimen", sPackageName);
	}
}
