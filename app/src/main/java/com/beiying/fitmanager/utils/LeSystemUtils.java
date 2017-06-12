package com.beiying.fitmanager.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import com.beiying.fitmanager.core.LeLog;


public class LeSystemUtils {
	
	private final static String ACTIVITY_URL_FOR_SET_DEFAULT_BROWSER = "http://m.lenovo.com/";
	
	/**
	 * 判断是否为系统应用
	 */
	public static boolean isSystemApplication(Context context) {
		return isSystemApplication(context, context.getPackageName());
	}
	
	/**
	 * 判断是否为系统应用
	 */
	public static boolean isSystemApplication(Context context, String packageName) {
		PackageManager manager = context.getPackageManager();
		try {
			PackageInfo packageInfo = manager.getPackageInfo(packageName, PackageManager.GET_CONFIGURATIONS);
			if(packageInfo != null && 
				(packageInfo.applicationInfo.flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0) {
				return true;
			}
		} catch (Exception e) {
			LeLog.e(e);
		}
		return false;
	}
	
	/**
	 * 判断联想浏览器是不是默认浏览器
	 */
	public static boolean isLenovoBrowserForDefault(final Context context) {
		String defaultPackage = getDefaultBrowserPackage(context);
		if (defaultPackage != null && defaultPackage.equals(context.getPackageName())) {
			return true;
		}
		return false;
	}

	/**
	 * 判断默认浏览器是不是其它浏览器
	 */
	public static boolean isOtherBrowserForDefault(final Context context) {
		String defaultPackage = getDefaultBrowserPackage(context);
		if (defaultPackage != null && !defaultPackage.equals(context.getPackageName())) {
			return true;
		}
		return false;
	}
	
	/**
	 * 获取当前国家
	 */
	public static String getCountry(final Context context) {
		if (context != null) {
			return context.getResources().getConfiguration().locale.getCountry();
		}
		return null;
	}
	
	/**
	 * 获取默认浏览器包名
	 */
	private static String getDefaultBrowserPackage(final Context context) {
		if (context != null) {
			Intent intent = new Intent();
		    intent.setAction(Intent.ACTION_VIEW);
		    intent.setData(Uri.parse(ACTIVITY_URL_FOR_SET_DEFAULT_BROWSER));
			PackageManager pm = context.getPackageManager();
			ResolveInfo info = pm.resolveActivity(intent, 0);
			if (info != null) {
				String packageName = info.activityInfo.packageName;
				if (packageName != null && !packageName.equals("android")) {
					return packageName;
				}
			}
		}
		return null;
	}
}
