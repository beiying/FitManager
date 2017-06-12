package com.beiying.fitmanager;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.beiying.fitmanager.manager.LeRunningStateManager;

import java.util.List;

public class BYApplication extends Application {
	public static final String LOG_TAG = "Application";
	public static BYApplication sInstance;
	public static long sTime;
	private List<ThrowableHandler> mThrowableHandlers;
	
	@Override
	public void onCreate() {
		sTime = System.currentTimeMillis();
		super.onCreate();

		LeApplicationHelper.init(this);
		assignInstance(this);
		BYBasicContainer.notifyAppStart(this);
		Thread.setDefaultUncaughtExceptionHandler(new CrashPrintHandler());
	}
	
	private static void assignInstance(BYApplication application) {
		sInstance = application;
	}

	class CrashPrintHandler implements Thread.UncaughtExceptionHandler {

		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			if (tryResume(thread, ex)) {
				return;
			}

			Log.e(LOG_TAG, "=== === === === FitManager Crash! tid=" + thread.getId() + " === === === === " + sInstance.getPackageName());
			try {
				if (Looper.getMainLooper() != Looper.myLooper()) {
					new Handler(Looper.getMainLooper()).post(new Runnable() {
						@Override
						public void run() {
							LeRunningStateManager.saveState();
						}
					});
				} else {
					LeRunningStateManager.saveState();
				}
//				Log.e(LOG_TAG, "debuggable:" + LeManifestHelper.isDebuggable());
//				Log.e(LOG_TAG, "Inner Version:" + LeVersion.getInstance().INNER_VERSION);
//				Log.e(LOG_TAG, "Outer Version:" + LeVersion.getInstance().getOuterVersion());
			} catch (Exception e) {
				Log.e(LOG_TAG, Log.getStackTraceString(e));
			}
			Log.e(LOG_TAG, Log.getStackTraceString(ex));
			System.exit(255);
		}

		private boolean tryResume(Thread thread, Throwable throwable) {
			if (mThrowableHandlers != null) {
				int size = mThrowableHandlers.size();
				for (int i=0; i!=size; ++i) {
					ThrowableHandler handle = mThrowableHandlers.get(i);
					if (handle.handleThrowable(throwable)) {
						return true;
					}
				}
			}
			return false;
		}
	}

	public interface ThrowableHandler {
		/**
		 * 处理异常或错误
		 * @param throwable	需要处理的异常或错误
		 * @return 返回true，表示处理了异常；返回false，表示不处理该异常
		 */
		boolean handleThrowable(Throwable throwable);
	}
}
