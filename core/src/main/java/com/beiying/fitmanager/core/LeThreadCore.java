package com.beiying.fitmanager.core;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class LeThreadCore {
	
	private static final String TAG = "ThreadCore";
	
	private static final int THREAD_POOL_SIZE = 4;
	private static final int THREAD_POOL_MIN_SIZE = 4;
	private static final int THREAD_POOL_MAX_SIZE = 8;
	private static final boolean USE_AUTO_POOL_SIZE = false;
	
	private PriorityBlockingQueue<LeThreadTask> mBackgroundQueue = new PriorityBlockingQueue<LeThreadTask>();
	private PriorityBlockingQueue<LeThreadTask> mDefaultQueue = new PriorityBlockingQueue<LeThreadTask>();
	
	private ArrayBlockingQueue<LeThreadTask> mDefaultOrderQueue = new ArrayBlockingQueue<LeThreadTask>(10);
	
	private List<LeThreadRunner> mBackgroundRunnerList = new ArrayList<LeThreadRunner>();
	private List<LeThreadRunner> mDefaultRunnerList = new ArrayList<LeThreadRunner>();
	
	private Handler mUIHandler;
	
	private Handler mOtherHandler;
	private List<LeSafeRunnable> mOtherWaitingList = new ArrayList<LeSafeRunnable>();
	
	private static LeThreadCore sInstance;
	
	private LeThreadCore() {
		
		enhanceBackgroundPool();
		
		enhanceDefaultPool();
		
		initOrderRunner();
	}
	
	private void initOrderRunner() {
		LeThreadRunner runner = new LeThreadRunner(mDefaultOrderQueue, Process.THREAD_PRIORITY_DEFAULT);
		runner.setName("DOTread");
		
		runner.start();
	}
	
	private void enhanceBackgroundPool() {
		synchronized (mBackgroundRunnerList) {
			for (int i = 0; i < THREAD_POOL_SIZE; i++) {
				if (mBackgroundRunnerList.size() >= THREAD_POOL_MAX_SIZE) {
					break ;
				}
				LeThreadRunner runner = new LeThreadRunner(mBackgroundQueue, Process.THREAD_PRIORITY_BACKGROUND);
				runner.setName("BGT-" + mBackgroundRunnerList.size());
				mBackgroundRunnerList.add(runner);
				
				runner.start();
			}
			Log.i(TAG, "enhance BGT: " + mBackgroundRunnerList.size());
		}
	}
	
	private void reduceBackgroundPool() {
		synchronized (mBackgroundRunnerList) {
			int reduceSize = THREAD_POOL_SIZE;
			for (int i = 0; i < reduceSize; i++) {
				if (mBackgroundRunnerList.size() <= THREAD_POOL_MIN_SIZE) {
					break ;
				}
				LeThreadRunner runner = mBackgroundRunnerList.remove(mBackgroundRunnerList.size() - 1);
				
				runner.quit();
			}
			Log.i(TAG, "reduce BGT: " + mBackgroundRunnerList.size());
		}
	}
	
	private void enhanceDefaultPool() {
		synchronized (mDefaultRunnerList) {
			for (int i = 0; i < THREAD_POOL_SIZE; i++) {
				if (mDefaultRunnerList.size() >= THREAD_POOL_MAX_SIZE) {
					break ;
				}
				LeThreadRunner runner = new LeThreadRunner(mDefaultQueue, Process.THREAD_PRIORITY_DEFAULT);
				runner.setName("DFT-" + mDefaultRunnerList.size());
				mDefaultRunnerList.add(runner);
				
				runner.start();
			}
			Log.i(TAG, "enhance DFT: " + mDefaultRunnerList.size());
		}
	}
	
	private void reduceDefaultPool() {
		synchronized (mDefaultRunnerList) {
			int reduceSize = THREAD_POOL_SIZE;
			for (int i = 0; i < reduceSize; i++) {
				if (mDefaultRunnerList.size() <= THREAD_POOL_MIN_SIZE) {
					break ;
				}
				LeThreadRunner runner = mDefaultRunnerList.remove(mDefaultRunnerList.size() - 1);
				
				runner.quit();
			}
			Log.i(TAG, "reduce DFT: " + mDefaultRunnerList.size());
		}
	}
	
	public static LeThreadCore getInstance() {
		if (sInstance == null) {
			synchronized (LeThreadCore.class) {
				if (sInstance == null) {
					sInstance = new LeThreadCore();
				}
			}
		}
		return sInstance;
	}
	
	void init() {
		mUIHandler = new Handler(Looper.getMainLooper());
		
		HandlerThread handlerThread = new HandlerThread("threadcore") {
			
			@Override
			protected void onLooperPrepared() {
				mOtherHandler = new Handler();
				
				runWaitingTasks();
			}
		};
		handlerThread.start();
	}
	
	static void recycle() {
		if (sInstance == null) {
			return ;
		}
		sInstance.release();
	}
	
	private void release() {
		mUIHandler = null;
	}
	
	public void quit() {
		if (mBackgroundRunnerList != null) {
			for (LeThreadRunner runner : mBackgroundRunnerList) {
				runner.quit();
			}
			mBackgroundRunnerList.clear();
		}
		if (mDefaultRunnerList != null) {
			for (LeThreadRunner runner : mDefaultRunnerList) {
				runner.quit();
			}
			mDefaultRunnerList.clear();
		}
	}

	/**
	 * 不带Looper。若要Looper，请使用 {@link #postOnOtherLooper(LeSafeRunnable, long)}
	 * @param task
	 */
	public void runAsBackgroundPriority(LeThreadTask task) {
		if (task == null) {
			return ;
		}
		mBackgroundQueue.add(task);
		
		if (USE_AUTO_POOL_SIZE) {
			checkPoolSize(mBackgroundQueue, mBackgroundRunnerList, true);
		}
	}
	
	/**
	 * 不带Looper。若要Looper，请使用 {@link #postOnOtherLooper(LeSafeRunnable, long)}
	 * @param task
	 */
	public void runAsDefaultPriority(LeThreadTask task) {
		if (task == null) {
			return ;
		}
		mDefaultQueue.add(task);
		
		if (USE_AUTO_POOL_SIZE) {
			checkPoolSize(mDefaultQueue, mDefaultRunnerList, false);
		}
	}
	
	/**
	 * 后台单线程顺序执行
	 * @param task
	 */
	public void runInOrderAsDefaultPriority(LeThreadTask task) {
		if (task == null) {
			return ;
		}
		try {
			mDefaultOrderQueue.put(task);
		} catch (InterruptedException e) {
		}
	}
	
	private void checkPoolSize(BlockingQueue<LeThreadTask> queue, List<LeThreadRunner> runnerList, boolean isBackground) {
		int runnerSize, queueSize;
		synchronized (runnerList) {
			runnerSize = runnerList.size();
			queueSize = queue.size();
		}
		int enhanceSize = THREAD_POOL_SIZE, reduceSize = THREAD_POOL_SIZE;
		boolean tooLess =  queueSize - runnerSize > enhanceSize;
		boolean tooMuch = runnerSize - queueSize > reduceSize;
		if (tooLess) {
			if (isBackground) {
				mDefaultQueue.add(new LeThreadTask(LeThreadTask.PRIORITY_HIGH) {
					
					@Override
					public void runSafely() {
						enhanceBackgroundPool();
					}
				});
			} else {
				mDefaultQueue.add(new LeThreadTask(LeThreadTask.PRIORITY_HIGH) {
					
					@Override
					public void runSafely() {
						enhanceDefaultPool();
					}
				});
			}
		} else if (tooMuch) {
			if (isBackground) {
				mDefaultQueue.add(new LeThreadTask(LeThreadTask.PRIORITY_HIGH) {
					
					@Override
					public void runSafely() {
						reduceBackgroundPool();
					}
				});
			} else {
				mDefaultQueue.add(new LeThreadTask(LeThreadTask.PRIORITY_HIGH) {
					
					@Override
					public void runSafely() {
						reduceDefaultPool();
					}
				});
			}
		}
	}
	
	public void postOnMainLooper(LeSafeRunnable runnable, long delayMillis) {
		if (mUIHandler != null && runnable != null) {
			mUIHandler.postDelayed(runnable, delayMillis);
		}
	}
	
	public void postOnOtherLooper(LeSafeRunnable runnable, long delayMillis) {
		if (mOtherHandler == null) {
			synchronized (mOtherWaitingList) {
				if (mOtherHandler == null) {
					runnable.mData = delayMillis;
					mOtherWaitingList.add(runnable);
					return ;
				}
			}
		}
		mOtherHandler.postDelayed(runnable, delayMillis);
	}
	
	private void runWaitingTasks() {
		synchronized (mOtherWaitingList) {
			for (LeSafeRunnable runnable : mOtherWaitingList) {
				if (runnable.mData == null) {
					mOtherHandler.post(runnable);
				} else if (runnable.mData instanceof Long) {
					mOtherHandler.postDelayed(runnable, (Long) runnable.mData);
				}
			}
			mOtherWaitingList.clear();
		}
	}
}
