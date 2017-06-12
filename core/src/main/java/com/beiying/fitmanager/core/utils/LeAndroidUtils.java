/** 
 * Filename:    LeAndroidProperty.java
 * Description:  
 * Copyright:   Lenovo PCL Copyright(c)2013 
 * @author:     chenwei27 
 * @version:    1.0
 * Create at:   2013-7-12 下午2:53:13
 * 
 * Modification History: 
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------ 
 * 2013-7-12     chenwei27    1.0         1.0 Version 
 */
package com.beiying.fitmanager.core.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.text.format.Time;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.beiying.fitmanager.core.ContextContainer;
import com.beiying.fitmanager.core.LeLog;
import com.beiying.fitmanager.core.LePrimitiveType;
import com.beiying.fitmanager.core.LeSafeRunnable;
import com.beiying.fitmanager.core.data.LeSharedPrefUnit;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

public class LeAndroidUtils extends ContextContainer {
	public static final String INTENT_TYPE_ADD_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
	private static final String DUPILICATE = "duplicate";
	private static final String PASTED_URL = "pasted_url";

	public static Drawable getApkIcon(Context context, String apkPath) {
		PackageInfo info = getApkInfo(context, apkPath);
		if (info != null) {
			ApplicationInfo appInfo = info.applicationInfo;
			appInfo.sourceDir = apkPath;
			appInfo.publicSourceDir = apkPath;

			PackageManager pm = context.getPackageManager();
			return appInfo.loadIcon(pm);
		}
		return null;
	}

	public static PackageInfo getApkInfo(Context context, String apkPath) {
		PackageManager pm = context.getPackageManager();
		PackageInfo info = null;
		try {
			info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
		} catch (Exception e) {
		}
		return info;
	}

	public static void lauchForPackage(String pkgName) {
		PackageManager pm = sContext.getPackageManager();
		try {
			Intent intent = pm.getLaunchIntentForPackage(pkgName);
			sContext.startActivity(intent);
		} catch (Exception e) {
		}
	}
	
	public static Intent getLauchIntent(String pkgName) {
		PackageManager pm = sContext.getPackageManager();
		try {
			Intent intent = pm.getLaunchIntentForPackage(pkgName);
			return intent;
		} catch (Exception e) {
		}
		return null;
	}

	public static int getPackageVersionCode(Context context) {
		String pkgName = context.getPackageName();
		PackageManager pm = context.getPackageManager();
		int versionCode = 0;
		try {
			PackageInfo pinfo = pm.getPackageInfo(pkgName, PackageManager.GET_CONFIGURATIONS);
			versionCode = pinfo.versionCode;
		} catch (Exception e) {
			LeLog.w("Get version code fail: " + e.getMessage());
		}
		return versionCode;
	}
	
	public static boolean isHasNewVersion() {
		boolean showTag = false;
		LeSharedPrefUnit mLeVersionCodePair = new LeSharedPrefUnit(LePrimitiveType.INTEGER, "version_code", 0);
		int newVersionCode = mLeVersionCodePair.getInt();
		if (newVersionCode == 0) {
			showTag = false;
		} else {
			if (newVersionCode > getPackageVersionCode(sContext)) {
				showTag = true;
			} else {
				showTag = false;
			}
		}
		return showTag;
	}

	public static PackageInfo getSignaturePackageInfo(String pkgName) {
		PackageInfo packageInfo = null;
		try {
			packageInfo = sContext.getPackageManager().getPackageInfo(pkgName, PackageManager.GET_SIGNATURES);
		} catch (Exception e1) {
		}
		return packageInfo;
	}

	public static PublicKey getPublicKey(PackageInfo pi) {
		try {
			if (pi.signatures == null || pi.signatures.length == 0) {
				return null;
			}

			byte[] signature = pi.signatures[0].toByteArray();
			CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
			InputStream is = new ByteArrayInputStream(signature);
			X509Certificate cert = (X509Certificate) certFactory.generateCertificate(is);
			is.close();
			return cert.getPublicKey();
		} catch (Exception ex) {

		}
		return null;
	}

	public static List<PackageInfo> getInstalledApps() {
		List<PackageInfo> packages = sContext.getPackageManager().getInstalledPackages(0);
		return packages;
	}

	public byte[] readPrivateFile(Context context, String filename) {
		byte[] exdata = null;
		try {
			if (context != null) {
				FileInputStream fis = context.openFileInput(filename);
				if (fis.available() > 0) {
					exdata = new byte[fis.available()];
					fis.read(exdata);
				}
				fis.close();
			}
		} catch (FileNotFoundException e) {
			LeLog.e(e);
		} catch (IOException e) {
			LeLog.e(e);
		}
		return exdata;
	}

	public void savePrivateFile(Context context, byte[] data, String file) {
		FileOutputStream fos = null;
		try {
			fos = context.openFileOutput(file, Context.MODE_PRIVATE);
			fos.write(data);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					LeLog.e(e);
				}
			}
		}
	}

	public static String readRaw(Context context, int res) {
		try {
			InputStream is = context.getResources().openRawResource(res);
			if (is != null) {
				byte[] temp = new byte[is.available()];
				is.read(temp);
				String content = new String(temp);
				return content;
			}
		} catch (Exception ex) {
			LeLog.e(ex);
			return "";
		}
		return "";
	}

	public static String getDefaultLauncherPackageName() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		ResolveInfo resolveInfo = sActivity.getPackageManager().resolveActivity(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		String currentHomePackage = resolveInfo.activityInfo.packageName;
		return currentHomePackage;
	}

	public static void addToLauncher(Activity activity, String name, Bitmap icon, Intent invokedIntent) {
		Intent addToLauncherIntent = new Intent(INTENT_TYPE_ADD_SHORTCUT);

		addToLauncherIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);

		addToLauncherIntent.putExtra(DUPILICATE, false);

		addToLauncherIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon);

		addToLauncherIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, invokedIntent);

		activity.sendBroadcast(addToLauncherIntent);
	}

	public static boolean storageMounted() {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}

//	public static String getExternalStorageRoot() {
//		String externalSD = Environment.getExternalStorageDirectory().getPath();
//		String sdRoot;
//		final String storage = "/storage";
//		if (externalSD.contains(storage)) {
//			sdRoot = storage;
//		} else {
//			int index = externalSD.lastIndexOf(File.separator);
//			if (index != -1) {
//				sdRoot = externalSD.substring(0, index);
//			} else {
//				sdRoot = externalSD;
//			}
//		}
//		return sdRoot;
//	}

	public static boolean couldHandleIntent(Context context, Intent intent) {
		if (context == null || intent == null) {
			return false;
		}
		final PackageManager packageManager = context.getPackageManager();
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.GET_ACTIVITIES);
		return list.size() > 0;
	}

	public static String getSystemTime() {
		Time t = new Time();
		t.setToNow(); // 取得系统时间。
		int hour = t.hour; // 0-23
		int minute = t.minute;
		String interval = ":";
		if (!isTime24Format()) {
			hour = hour % 12;
		}
		if (minute < 10) {
			interval += "0";
		}
		return hour + interval + minute;
	}

	public static boolean isTime24Format() {
		ContentResolver cv = sActivity.getContentResolver();
		String strTimeFormat = android.provider.Settings.System.getString(cv,
				android.provider.Settings.System.TIME_12_24);

		if (strTimeFormat != null && strTimeFormat.equals("24")) {
			return true;
		}
		return false;
	}

	public static double getPaintHeight(Paint paint) {
		FontMetrics fm = paint.getFontMetrics();
		return Math.ceil(fm.descent - fm.ascent);
	}

	public static boolean isPortait() {
		if (sActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			return true;
		}
		return false;
	}

	public static void lockScreenAsPortait() {
		sActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	public static void lockScreenAsLandscape() {
		sActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
	}

	public static void lockScreen() {
		if (sActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			sActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
		} else {
			sActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		}
	}

	public static void unlockScreen(int requestedOrientation) {
		sActivity.setRequestedOrientation(requestedOrientation);
	}

	public static void invokeInputMethod(final View anyView) {
		anyView.postDelayed(new LeSafeRunnable() {
			@Override
			public void runSafely() {
				final InputMethodManager imm = (InputMethodManager) anyView.getContext().getSystemService(
						Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}, 100);
	}

	public static void closeInputMethod(final View anyView) {
		final InputMethodManager imm = (InputMethodManager) anyView.getContext().getSystemService(
				Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(anyView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public static String getApplicationValue(String key) {
		String value = "";
		try {
			if (sActivity == null) {
				return "";
			}
			ApplicationInfo appInfo = sActivity.getPackageManager().getApplicationInfo(
					sActivity.getPackageName(), PackageManager.GET_META_DATA);
			Object val = appInfo.metaData.get(key);
			value = String.valueOf(val);
		} catch (NameNotFoundException e) {
		} catch (Exception e) {
		}
		return value;
	}

	public static boolean isDebuggable() {
		return isDebuggable(sActivity);
	}
	
	public static boolean isDebuggable(Activity activity) {
		try {
			ApplicationInfo appInfo = activity.getPackageManager().getApplicationInfo(
					activity.getPackageName(), 0);
			int flags = appInfo.flags;
			if ((flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
				return true;
			}

		} catch (Exception e) {
			LeLog.e("isDebuggable got error: " + e.getMessage());
		}
		return false;
	}

	//获取状态栏高度
	public static int getStatusBarHeight() {
		Rect rect = new Rect();
		sActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		return rect.top;
	}

	//获取系统属性
	public static String getSystemProperties(String prop) {
		Class cls = null;
		try {
			cls = Class.forName("android.os.SystemProperties");
		} catch (ClassNotFoundException e) {
		}
		if (cls == null) {
			return null;
		}

		Object systemProperties = LeReflectUtils.newInstance(cls, null, null);
		Object[] params = new Object[1];
		params[0] = prop;
		Object[] results = new Object[1];
		LeReflectUtils.invokeMethod(systemProperties, "get", params, results);
		String optr = null;
		if (results[0] != null && results[0] instanceof String) {
			optr = (String) results[0];
		}
		return optr;
	}

	public static void openFile(String mimeType, String filePath) {
		Uri path = formPath(filePath);

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(path, mimeType);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		try {
			sActivity.startActivity(intent);
		} catch (ActivityNotFoundException e) {
		}
	}

	@SuppressLint("NewApi")
	public static void copyToClipboard(String text) {
		if (LeMachineHelper.getSDKVersionInt() < android.os.Build.VERSION_CODES.HONEYCOMB) {
			android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager) sActivity
					.getSystemService(Context.CLIPBOARD_SERVICE);
			clipboardManager.setText(text);
		} else {
			android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager) sActivity
					.getSystemService(Context.CLIPBOARD_SERVICE);
			android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
			clipboardManager.setPrimaryClip(clip);
		}
	}

	public static String pasteFromClip(Context context) {
		return pasteFromClip(context, PASTED_URL);
	}

	public static String pasteFromClip(Context context, String prefKey) {
		LeSharedPrefUnit mPastedUrl = new LeSharedPrefUnit(LePrimitiveType.STRING, prefKey, "");
		ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		if (cmb == null) {
			return null;
		}
		CharSequence charSequence = cmb.getText();
		if (charSequence == null || charSequence.toString() == null
				|| charSequence.toString().trim().length() == 0) {
			return null;
		}
		String url = charSequence.toString().trim();
		String mPresentUrl = mPastedUrl.getString();
		if (!LeUtils.checkStringIsUrl(url)) {
			return null;
		}
		if (LeUtils.checkStringIsEmail(url)) {
			return null;
		}
		if (mPresentUrl == null || !mPresentUrl.equals(url)) {
			mPastedUrl.setValue(url);
			return url;
		}
		return null;
	}

	public static void markeUrlUsed(String url) {
		if (TextUtils.isEmpty(url)) {
			return;
		}

		if (TextUtils.isEmpty(url.trim())) {
			return;
		}
		url = url.trim();
		if (LeUtils.checkStringIsUrl(url)) {
			LeSharedPrefUnit mPastedUrl = new LeSharedPrefUnit(LePrimitiveType.STRING, PASTED_URL, "");
			mPastedUrl.setValue(url);
		}
	}

	public static void installApk(String filePath) {
		if (filePath != null && filePath.endsWith(".apk")) {
			Uri path = formPath(filePath);
			String mimeType = "application/vnd.android.package-archive";

			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(path, mimeType);

			try {
				sActivity.startActivity(intent);
			} catch (ActivityNotFoundException e) {
				LeLog.v("apk install fail!");
			}
		}
	}

	public static boolean isInstalled(String pkgName, int versionCode) {
		try {
			PackageInfo packageInfo = sContext.getPackageManager().getPackageInfo(pkgName, 0);
			if (packageInfo.versionCode == versionCode) {
				return true;
			}
		} catch (Exception e) {

		}
		return false;
	}

	public static boolean isInstalled(String pkgName) {
		try {
			sContext.getPackageManager().getPackageInfo(pkgName, 0);
			return true;
		} catch (Exception e) {

		}
		return false;
	}

	private static Uri formPath(String filePath) {
		Uri path = Uri.parse(filePath);

		if (path.getScheme() == null) {
			path = Uri.fromFile(new File(filePath));
		}
		return path;
	}

	/**
	 * 不择手段，获取一个存储空间位置， 首选当然是externalStorage
	 * @return 获取到的路径，如果为空，那就真心找不到了。
	 */
	public static String getExternalStorageAbsolutePath(Context context) {
		String path = null;
		if (storageMounted()) {
			path = Environment.getExternalStorageDirectory().getAbsolutePath();
		} else {
			List<String> list = null;
			if (context == null) {
				list = getInvokeSdCard(sContext);
			} else {
				list = getInvokeSdCard(context);
			}
			if (list != null && list.size() > 0) {
				path = list.get(0);
			}
		}
		return path;
	}

	/**
	 * getStorageAbsolutePath();
	 * @return
	 */
	public static String getExternalStoragePath(Context context) {
		String path = null;
		if (storageMounted()) {
			path = Environment.getExternalStorageDirectory().getPath();
		} else {
			List<String> list = null;
			if (context == null) {
				list = getInvokeSdCard(sContext);
			} else {
				list = getInvokeSdCard(context);
			}
			if (list != null && list.size() > 0) {
				path = list.get(0);
			}
		}
		return path;
	}

	public static boolean checkHasExternalStorage(Context context) {
		if (storageMounted()) {
			return true;
		} else {
			if (!TextUtils.isEmpty(getExternalStorageAbsolutePath(context))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 反射拿到所有可用的存储空间.
	 * @param context context
	 * @return 所有可用的路径
	 */
	public static final List<String> getInvokeSdCard(Context context) {
		// 获取sdcard的路径：外置和内置
		String[] paths = {};
		List<String> result = new ArrayList<String>();
		try {
			StorageManager sm = (StorageManager) context.getApplicationContext().getSystemService(Context.STORAGE_SERVICE);
			paths = (String[]) sm.getClass().getMethod("getVolumePaths", null).invoke(sm, null);
			int n = paths.length;
			for (int i = 0; i < n; i++) {
				if (!TextUtils.isEmpty(paths[i])) {
					if (Build.VERSION.SDK_INT < 23) {
						File file = new File(paths[i]);
						if (file.canWrite()) {
							result.add(paths[i]);
						}
					} else {
						result.add(paths[i]);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	//查看所有sdcard的路径
	public static ArrayList<String> getAllSDCardPaths() {
		ArrayList<String> mount = new ArrayList<String>();
		try {
			Runtime runtime = Runtime.getRuntime();
			Process proc = runtime.exec("mount");
			InputStream is = proc.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			String line;
			BufferedReader br = new BufferedReader(isr);
			while ((line = br.readLine()) != null) {
				if (line.contains("secure")) {
					continue;
				}
				if (line.contains("asec")) {
					continue;
				}
				if (line.contains("fat")) {
					String columns[] = line.split(" ");
						if (columns != null && columns.length > 1) {
							mount.add(columns[1]);
						}
					} else if (line.contains("fuse")) {
						String columns[] = line.split(" ");
						if (columns != null && columns.length > 1) {
							mount.add(columns[1]);
						}
					}
				}
			} catch (FileNotFoundException e) {
				LeLog.e(e);
			} catch (IOException e) {
				LeLog.e(e);
			}
			return mount;
		}

	public static ComponentName getNextActivity(Context context) {
		ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE) ;
	     List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(2);
	     if (runningTaskInfos != null && runningTaskInfos.size() >= 2) {
	    	 RunningTaskInfo taskInfo = runningTaskInfos.get(1);
	    	 if (taskInfo != null) {
	    		 ComponentName nextActivity = taskInfo.topActivity;
	    		 return nextActivity;
	    	 }
	     }
	     return null;
	}


	/**
	 * 判断是否是MIUI
	 * @return
	 */
	public static boolean isMIUI() {
		final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
		String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
		String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";
		try {
			String versionCode = getSystemProperties(KEY_MIUI_VERSION_CODE);
			String versionName = getSystemProperties(KEY_MIUI_VERSION_NAME);
			String storage = getSystemProperties(KEY_MIUI_INTERNAL_STORAGE);
//			LeLog.e("zyb " + versionCode + " " + versionName + " " + storage);
			return !TextUtils.isEmpty(versionCode)
					|| !TextUtils.isEmpty(versionName)
					|| !TextUtils.isEmpty(storage);
		} catch (final Exception e) {
			return false;
		}
	}

	public static boolean isMeizu() {
		if (android.os.Build.BRAND != null) {
			if ("Meizu".equalsIgnoreCase(android.os.Build.BRAND)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isSamsung() {
		if (android.os.Build.BRAND != null) {
			if ("samsung".equalsIgnoreCase(android.os.Build.BRAND)) {
				return true;
			}
		}
		return false;
	}

	public static String getRealPathFromURI(Context context, Uri contentUri) {
		if (contentUri == null) {
			return null;
		}
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
		if (cursor != null) {
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		}
		return null;
	}

    public static ConnectivityManager getConnectivityManager(Context context) {
        return (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
    }
	
}
