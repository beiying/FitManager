package com.beiying.fitmanager.core.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.beiying.fitmanager.core.LeLog;
import com.beiying.fitmanager.core.LeSafeRunnable;
import com.beiying.fitmanager.core.ui.LeUI;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressLint("NewApi")
public class LeUtils {

	/** 外部版本号.*/
	public static final String URL_PARAM_OUT_VERSION = "out_version";
	
	private static long sBeginTime;
	private static long sEndTime;
	
	private static long sLastClickTime; 
	private LeUtils() {}

	public static String getString(Context context, int resourceId) {
		if (context != null) {
			return context.getResources().getString(resourceId);
		}
		return null;
	}
	
	public static Bitmap getBitmapUsingDecode(Context context, int resId) {
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
		return bitmap;
	}
	
	public static Bitmap getBitmapUsingDrawable(Context context, int resId) {
		BitmapDrawable drawable = (BitmapDrawable) context.getResources().getDrawable(resId);
		return drawable.getBitmap();
	}

	public static void showToast(Context context, int msgResId) {
		showToast(context, context.getString(msgResId));
	}
	
	public static void showToast(Context context, String msg) {
		final int padding = LeUI.getDensityDimen(context, 6);
		TextView textView = new TextView(context);
		textView.setPadding(padding, padding, padding, padding);
		textView.setBackgroundColor(0xcc000000);
		textView.setTextColor(Color.WHITE);
		textView.setTextSize(14);
		textView.setText(msg);
		showToast(context, textView);
	}
	
	public static void showToast(Context context, View toastView) {
		showToast(context, toastView, Toast.LENGTH_SHORT);
	}
	
	public static void showToast(Context context, View toastView, int duration) {
		Toast toast = new Toast(context);

		if (Build.VERSION.SDK_INT < 14) {
			if (toast != null) {
				toast.cancel();
			}
		}
		if (toast != null) {
			toast.setView(toastView);
			toast.setDuration(duration);
			toast.show();
		}
	}

	public static void showInputMethod(final View anyView) {
		anyView.postDelayed(new LeSafeRunnable() {
			@Override
			public void runSafely() {
				final InputMethodManager imm = (InputMethodManager) anyView.getContext().getSystemService(
						Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(anyView, InputMethodManager.SHOW_IMPLICIT);
			}
		}, 100);
	}
	
	public static void hideInputMethod(final View anyView) {
		InputMethodManager inputMethodManager = (InputMethodManager) anyView.getContext().getSystemService(
				Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(anyView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public static boolean isEmptyCollection(Collection<?> collection) {
		if (collection == null || collection.size() == 0) {
			return true;
		}
		return false;
	}

	public static boolean isEmptyString(String str) {
		if (str == null || str.trim().equals("") || str.trim().equalsIgnoreCase("null")) {
			return true;
		}
		return false;
	}

	/** 判断字符串是否为网址 */
	public static boolean checkStringIsUrl(String input) {
		if (input == null)
			return false;
		if (input.indexOf("tel://") == 0) {
			return true;
		}
		if (input.indexOf("mailto:") == 0 && input.contains("@")) {
			return true;
		}
		if (input.indexOf("wtai://") == 0) {
			return true;
		}
		final String oldPatternStr = "(^((https|http|ftp|rtsp|mms)?://)"
				+ "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?"
				+ "(([0-9]{1,3}\\.){3}[0-9]{1,3}" + "|" + "([0-9a-z_!~*'()-]+\\.)*"
				+ "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." + "[a-z]{2,})" + "(:[0-9]{1,5})?" + "((/?)|"
				+ "(/[0-9a-z\\u4e00-\\u9fa5_!~*'().;?:@\\|&=+$,%#-/]+)+/?)$)|(^file://*)";
//		final String scheme = "(https?://|ftp://|rtsp://|mms://)";
//		final String qq = "((" + scheme + "(?:(?:[-_0-9a-zA-Z.~!$&\'\\(\\)*+,;=%]+\\.)+[-_0-9a-zA-Z.~!$&\'\\(\\)*+,;=%]+))|(([-_0-9a-zA-Z.~!$&\'\\(\\)*+,;=%]+@)?(?<![-_0-9a-zA-Z~$&\'*+])www\\.([-_0-9a-zA-Z.~!$&\'\\(\\)*+,;=%]+\\.)+[-_0-9a-zA-Z.~!$&\'\\(\\)*+,;=%]+)|(([-_0-9a-zA-Z.~!$&\'\\(\\)*+,;=%]+@)?(?<![-_0-9a-zA-Z~$&\'*+])(([0-9a-zA-Z]|%\\d\\d)+\\.)*([0-9a-zA-Z]|%\\d\\d)+(\\.com\\b|\\.net\\b|\\.cn\\b|\\.org\\b))|" + scheme + "((\\d{1,3}\\.){3}\\d{1,3}))(?:\\:\\d+)?(([/#][-_0-9a-zA-Z.~!$&\'\\(\\)*+,;=:@/?#%]*)|(\\?[-_0-9a-zA-Z.~!$&\'\\(\\)*+,;=:@/?#%]+))?";
		final String patternStr = oldPatternStr;
		Pattern pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(input.trim());
		if (matcher.find()) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean checkStringIsEmail(String str) {
		String patternStr = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(str);
		if (matcher.find()) {
			return true;
		}
		return false;
	}

	public static String urlCompletion(String srcUrl) {
		if (srcUrl == null) {
			return srcUrl;
		}
		if (srcUrl.contains("://")) {
			return srcUrl;
		}
		srcUrl = "http://" + srcUrl;
		return srcUrl;
	}
	
	/**
	 * 判断是否为系统应用
	 * 
	 * need test
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
	 * 给URL拼接公共参数
	 */
	public static String processUrl(String url) {
		//TODO 添加公用参数
		return url;
	}
	
	public static boolean isLoading(int progress) {
		return 0 < progress && progress < 100;
	}
	
	public static boolean saveJPEGBitmap(Bitmap bmp, String path) {
		return saveBitmap(bmp, path, Bitmap.CompressFormat.JPEG);
	}

	public static boolean savePNGBitmap(Bitmap bmp, String path) {
		return saveBitmap(bmp, path, Bitmap.CompressFormat.PNG);
	}

	private static boolean saveBitmap(Bitmap bmp, String path, CompressFormat format) {
		if (bmp == null || bmp.isRecycled() || path == null) {
			return false;
		}
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bmp.compress(format, 100, baos);
			fos.write(baos.toByteArray());
			baos.flush();
			fos.flush();
			baos.close();
			fos.close();

			return true;
		} catch (Exception e) {
			LeLog.e(e);
			return false;
		}
	}

	public static void applyKeepScreenOn(Activity activity) {
		activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	public static void removeKeepScreenOn(Activity activity) {
		activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	public static void markBegin() {
		sBeginTime = System.currentTimeMillis();
	}

	public static void markEnd() {
		markEnd("");
	}

	public static void markEnd(String tag) {
		sEndTime = System.currentTimeMillis();
		LeLog.e(tag + " ellapse:" + (sEndTime - sBeginTime));
	}

	// focus中使用，为每一个view创造一个唯一的id
	public static int createId(View view) {
		if (view != null) {
			return view.hashCode();
		} else {
			return -1;
		}
	}

	public static void dumpTouchEvent(MotionEvent event, String tag) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			LeLog.e(tag + "action down");
			break;
		case MotionEvent.ACTION_MOVE:
			LeLog.e(tag + "action move");
			break;
		case MotionEvent.ACTION_UP:
			LeLog.e(tag + "action up");
			break;
		case MotionEvent.ACTION_CANCEL:
			LeLog.e(tag + "action cancel");
			break;
		default:
			break;
		}
	}

	public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - sLastClickTime;
        if ( 0 < timeD && timeD < 600) {
            return true;
        }
        sLastClickTime = time;
        return false;
    }

	public static String saveBitmap(Bitmap bitmap, String path) {
		long time = System.currentTimeMillis();
		File file = new File(path + time);
		if(file.exists()){
			file.delete();
		}
		FileOutputStream out;
		try{
			out = new FileOutputStream(file);
			if(bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)) {
				out.flush();
				out.close();
			}
			return file.getPath();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean deleteBitmap(String path) {
		File file = new File(path);
		if (file != null && file.exists()) {
			return file.delete();
		}
		return false;
	}

}
