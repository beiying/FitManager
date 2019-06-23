package com.beiying.fitmanager.core;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

public class LeHandlerCore {
	
	private static TaskCore sDefaultTask = new TaskCore(Process.THREAD_PRIORITY_MORE_FAVORABLE, "defaultHT");
	
	private static TaskCore sBackgroundTask = new TaskCore(Process.THREAD_PRIORITY_BACKGROUND, "backgroundHT");
	
	public static void init() {
		
	}
	
	public static void runAsDefaultPriority(LeHandlerTask task) {
		sDefaultTask.run(task);
	}
	
	public static void runAsBackgroundPriority(LeHandlerTask task) {
		sBackgroundTask.run(task);
	}
	
	public static class TaskCore {
		private static final int MSG_DEFALUT_DO = 1;
		
		private HandlerThread mHandlerThread;
		private Handler mHandler;
		private List<LeHandlerTask> mTaskList = new ArrayList<LeHandlerCore.LeHandlerTask>();
		
		private int mPriority;
		private String mName;
		
		public TaskCore(int priority, String name) {
			mPriority = priority;
			mName = name;
			initDefaultHandler();
		}
		
		private void initDefaultHandler() {
			mHandlerThread = new HandlerThread(mName, mPriority) {
				@Override
				protected void onLooperPrepared() {
					
					mHandler = new Handler(Looper.myLooper()) {
						@Override
						public void handleMessage(Message msg) {
							switch (msg.what) {
							case MSG_DEFALUT_DO:
								if (msg.obj != null) {
									if (msg.obj instanceof BYSafeRunnable) {
										BYSafeRunnable safeRunnable = (BYSafeRunnable) msg.obj;
										safeRunnable.run();
										
									}
								}
								break;

							default:
								break;
							}
						}
					};
				}
			};
			mHandlerThread.start();
		}
		
		private void triggerDefaultTask(LeHandlerTask task) {
			mHandler.obtainMessage(MSG_DEFALUT_DO, task).sendToTarget();
		}
		
		public void run(LeHandlerTask task) {
			synchronized (mTaskList) {
				mTaskList.add(task);
			}
			runList();
		}
		
		private void runList() {
			synchronized (mTaskList) {
				for (LeHandlerTask task : mTaskList) {
					triggerDefaultTask(task);
				}
				mTaskList.clear();
			}
		}
	}
	
	public abstract static class LeHandlerTask extends BYSafeRunnable {
		
	}
}
