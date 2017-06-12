package com.beiying.fitmanager.core.net;


import com.beiying.fitmanager.core.LeLog;

import java.util.ArrayList;
import java.util.List;

public class LeNet {

	//是否运行状态
	private boolean isAlive;

	//最小引擎数
	private static final int MIN_ENGINE_NUM = 8;

	//任务列表
	private List<LeNetTask> taskList;

	//网络引擎列表
	private List<LeNetEngine> engineList;

	// 网络类型
	public static final byte GET = 1;

	public static final byte POST = 2;

	public static final byte HEAD = 3;

	public LeNet() {
		taskList = new ArrayList<LeNetTask>();
		engineList = new ArrayList<LeNetEngine>();
		init();
	}

	private void init() {
		for (int i = 0; i < MIN_ENGINE_NUM; i++) {
			LeNetEngine engine = new LeNetEngine(this);
			engineList.add(engine);
		}
	}

	public synchronized void addNetTask(LeNetTask task) {
		taskList.add(task);
	}

	public void startTask() {
		int elsLenght = engineList.size();
		for (int i = 0; i < elsLenght; i++) {
			LeNetEngine net = engineList.get(i);
			if (!net.isRun()) {
				LeNetTask iTask = getNetTask();
				if (iTask != null) {
					net.setMyTask(iTask);
					if (net.isStarted()) {
						net.mNotify();
					} else {
						net.setStarted(true);
						net.start();
					}
					net.setRun(true);
				}
			}
		}
	}

	public synchronized void startTask(LeNetTask task) {
		taskList.add(task);
		startTask();
	}

	public synchronized LeNetTask getNetTask() {
		LeNetTask next = null;
		try {
			int tlsLenght = taskList.size();
			if (tlsLenght > 0) {
				next = taskList.remove(0);
			}

		} catch (Exception e) {
			LeLog.e(e);
		}
		return next;
	}

	public void stopAll() {
		for (LeNetEngine en : engineList) {
			en.setStop(false);
		}
	}

	public synchronized void release() {
		taskList.clear();
		taskList = null;
		stopAll();
		engineList.clear();
		engineList = null;
	}

}
