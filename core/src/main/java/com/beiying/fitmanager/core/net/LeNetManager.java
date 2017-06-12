package com.beiying.fitmanager.core.net;

import java.util.HashMap;
import java.util.Map;

public class LeNetManager {

	private static LeNetManager instance;

	private Map<String, LeNet> mNetItems;

	private String cid = "0";

	private LeNetManager() {
		mNetItems = new HashMap<String, LeNet>();
		createNet(cid);
	}

	public static LeNetManager getInstance() {
		if (instance == null) {
			synchronized (LeNetManager.class) {
				if (instance == null) {
					instance = new LeNetManager();
				}
			}
		}
		return instance;
	}

	/**
	 * 
	 * 描述 创建网络
	 * 
	 * @param id
	 */
	public void createNet(String id) {
		LeNet net = new LeNet();
		mNetItems.put(id, net);
	}

	/**
	 * 
	 * 描述 释放网络
	 */
	public void releaseNet(String id) {
		LeNet net = mNetItems.get(id);
		if (net != null) {
			net.release();
		}
	}

	/**
	 * '
	 * 
	 * 描述 根据id取net
	 * 
	 * @param id
	 * @return
	 */
	public LeNet getNet(String id) {
		return mNetItems.get(id);
	}

	/**
	 * 
	 * 描述 取默认net
	 * 
	 * @return
	 */
	public LeNet getNet() {
		return mNetItems.get(cid);
	}

}
