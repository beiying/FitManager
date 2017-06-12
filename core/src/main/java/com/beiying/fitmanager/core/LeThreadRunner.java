package com.beiying.fitmanager.core;

import android.os.Process;

import java.util.concurrent.BlockingQueue;

public class LeThreadRunner extends Thread {
	
	BlockingQueue<LeThreadTask> mTaskQueue;
	
	private boolean mQuit;
	
	private int mPriority;
	
	public LeThreadRunner(BlockingQueue<LeThreadTask> taskQueue) {
		this(taskQueue, Process.THREAD_PRIORITY_DEFAULT);
	}
	
	public LeThreadRunner(BlockingQueue<LeThreadTask> taskQueue, int priority) {
		mTaskQueue = taskQueue;
		mPriority = priority;
	}

	@Override
	public void run() {
		Process.setThreadPriority(mPriority);
		
		while (true) {
			LeThreadTask task;
			try {
				task = mTaskQueue.take();
			} catch (InterruptedException e) {
				if (mQuit) {
					return ;
				}
				continue;
			}
			task.run();
		}
	}
	
	public void quit() {
		mQuit = true;
		interrupt();
	}
}
