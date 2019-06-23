package com.beiying.fitmanager;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.support.v4.os.TraceCompat;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.beiying.base.modules.BYModuleBus;
import com.beiying.base.modules.BYModuleImpl;
import com.beiying.fitmanager.core.LeLog;
import com.beiying.fitmanager.launchstarter.TaskDispatcher;
import com.beiying.fitmanager.launchstarter.task.MainTask;
import com.beiying.fitmanager.launchstarter.task.Task;
import com.beiying.fitmanager.manager.LeRunningStateManager;
import com.beiying.net.FileStorageManager;
import com.beiying.net.HttpManager;
import com.beiying.net.download.DownloadConfig;
import com.beiying.net.download.DownloadManager;
import com.facebook.stetho.Stetho;
import com.github.anrwatchdog.ANRWatchDog;
import com.github.moduth.blockcanary.BlockCanary;
import com.taobao.android.dexposed.DexposedBridge;
import com.taobao.android.dexposed.XC_MethodHook;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class BYApplication extends Application {
	public static final String LOG_TAG = "Application";
	public static BYApplication sInstance;
	public static long sTime;
	private List<ThrowableHandler> mThrowableHandlers;
	private CountDownLatch mCountDownlatch = new CountDownLatch(1);
	
	@Override
	public void onCreate() {
		sTime = System.currentTimeMillis();
		super.onCreate();
//		Debug.startMethodTracing("app");
		TraceCompat.beginSection("BYApplicationCreate");
//		ExecutorService service = Executors.newFixedThreadPool(Math.max(2, Math.min(Runtime.getRuntime().availableProcessors() - 1, 4)));//使用线程池，多线程运行启动过程中的初始化
//		service.submit(new Runnable() {
//			@Override
//			public void run() {
//				initPerfermanceMonitor();
//				mCountDownlatch.countDown();
//			}
//		});

//		LeApplicationHelper.init(this);
//		assignInstance(this);
//		BYBasicContainer.notifyAppStart(this);
//		Thread.setDefaultUncaughtExceptionHandler(new CrashPrintHandler());
//		FileStorageManager.getInstance().init(this);
//		HttpManager.getInstance().init(this);
//		DownloadConfig config = new DownloadConfig.Builder()
//				.setCoreThreadSize(2)
//				.setMaxThreadSize(4)
//				.build();
//		DownloadManager.getInstance().init(config);

//		initPerfermanceMonitor();
//		Debug.stopMethodTracing();
//		try {
//			mCountDownlatch.await();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}

		TaskDispatcher.init(this);
		TaskDispatcher.getInstance().addTask(new InitAppComponents())
//				.addTask(new InitPerfermanceTask())
//				.addTask(new InitStrictModeTask())
				.start();
		TaskDispatcher.getInstance().await();
		TraceCompat.endSection();
	}

	private void initPerfermanceMonitor() {
		initStrictMode();
		BlockCanary.install(this, new BYAppBlockCanaryContext()).start();
		new ANRWatchDog().start();
		Stetho.initializeWithDefaults(this);
	}

	private void initStrictMode() {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectCustomSlowCalls()
				.detectDiskReads()
				.detectDiskWrites()
				.detectNetwork()
				.penaltyLog()
				.build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectActivityLeaks()
				.detectLeakedSqlLiteObjects()
				.setClassInstanceLimit(BYMainActivity.class, 1)
				.penaltyLog()
				.build());
	}

	class InitPerfermanceTask extends Task {

		@Override
		public void run() {
			initPerfermanceMonitor();
		}
	}

	class InitStrictModeTask extends Task {
		@Override
		public List<Class<? extends Task>> dependsOn() {
			List<Class<? extends Task>> tasks = new ArrayList<>();
			tasks.add(InitPerfermanceTask.class);
			return tasks;
		}

		@Override
		public void run() {
			initStrictMode();
		}
	}

	//主线程执行的Task
	class InitAppComponents extends MainTask {
		@Override
		public boolean needWait() {
			return true;
		}

		@Override
		public void run() {
			LeApplicationHelper.init(BYApplication.this);
			assignInstance(BYApplication.this);
			BYBasicContainer.notifyAppStart(BYApplication.this);
			Thread.setDefaultUncaughtExceptionHandler(new CrashPrintHandler());
			FileStorageManager.getInstance().init(BYApplication.this);
			HttpManager.getInstance().init(BYApplication.this);
			DownloadConfig config = new DownloadConfig.Builder()
					.setCoreThreadSize(2)
					.setMaxThreadSize(4)
					.build();
			DownloadManager.getInstance().init(config);
			if(BuildConfig.DEBUG) {
				ARouter.openLog();
				ARouter.openDebug();
			}
			ARouter.init(BYApplication.this);
			onModuleLoad();
		}

		@Override
		public boolean runOnMainThread() {
			return true;
		}
	}

	private void onModuleLoad() {
		for (String implName: BYModuleConfig.moduleCreate){
			try {
				Class<?> clazz = Class.forName(implName);
				if (clazz.newInstance() instanceof BYModuleImpl){
					BYModuleImpl impl = (BYModuleImpl) clazz.newInstance();
					impl.onLoad(this);
				}
			}catch (ClassNotFoundException e){
				e.printStackTrace();
			}catch (IllegalAccessException e){
				e.printStackTrace();
			}catch (InstantiationException e){
				e.printStackTrace();
			}
		}
	}


	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
		hookThreadConstruct();
		BYModuleBus.init(base);
	}

	private void hookThreadConstruct() {
		DexposedBridge.hookAllConstructors(Thread.class, new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				super.afterHookedMethod(param);
				Thread thread = (Thread) param.thisObject;
				LeLog.e("Thread stack:" + Log.getStackTraceString(new Throwable()));
			}
		});
	}

	@Override
	public Context getApplicationContext() {
		return this;//很多代码可以放在attachBaseContext中执行
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
