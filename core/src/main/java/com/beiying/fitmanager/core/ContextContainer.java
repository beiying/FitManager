/** 
 * Filename:    ContextContainer.java
 * Description:  
 * Copyright:   Lenovo PCL Copyright(c)2013 
 * @author:     chenwei27 
 * @version:    1.0
 * Create at:   2014-2-14 下午2:32:19
 * 
 * Modification History: 
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------ 
 * 2014-2-14     chenwei27    1.0         1.0 Version 
 */
package com.beiying.fitmanager.core;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

public class ContextContainer {
	public static Application sApplication;
	public static Context sContext;
	public static Activity sActivity;
	public static String sPackageName;
	private static int sToken;

	public static void applicationStart(Application application) {
		sApplication = application;
	}

	public static void activityStart(String pkgName, Activity activity) {
		sPackageName = pkgName;
		sContext = sActivity = activity;
		sToken = sContext.hashCode();
	}

	public static boolean checkContext(final Context context) {
		return context == sContext;
	}

	final protected static void recycleContext() {
		if (!matchToken()) {
			return ;
		}
		sContext = null;
		sActivity = null;
	}

	private static boolean matchToken() {
		if (sContext != null && sContext.hashCode() == sToken) {
			return true;
		}
		return false;
	}
}
