/** 
 * Filename:    LeSqliteManager.java
 * Description:  
 * Copyright:   Lenovo PCL Copyright(c)2013 
 * @author:     chenwei27 
 * @version:    1.0
 * Create at:   2014-1-14 下午12:42:16
 * 
 * Modification History: 
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------ 
 * 2014-1-14     chenwei27    1.0         1.0 Version 
 */
package com.beiying.fitmanager.core.sqlite;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeSqliteManager {

	private List<LeDatabase> mDatabaseList;
	
	private static Map<Class, LeSqliteTable> sTableMap = new HashMap<Class, LeSqliteTable>();
	
	private static LeSqliteManager sInstance;
	
	private LeSqliteManager() {
		mDatabaseList = new ArrayList<LeDatabase>();
	}
	
	public static LeSqliteManager getInstance() {
		if (sInstance == null) {
			synchronized (LeSqliteManager.class) {
				if (sInstance == null) {
					sInstance = new LeSqliteManager();
				}
			}
		}
		return sInstance;
	}
	
	public static void registerTable(Class cls, LeSqliteTable table) {
		sTableMap.put(cls, table);
	}
	
	public static LeSqliteTable getTable(Class cls) {
		return sTableMap.get(cls);
	}
	
	public void register(LeDatabase database) {
		synchronized (mDatabaseList) {
			mDatabaseList.add(database);
		}
	}
	
	public void initAllDatabase(final Context context) {
		if (Looper.myLooper() == null || Looper.myLooper() != Looper.getMainLooper()) {
			//异步线程直接初始化数据库
			executeInit(context);
		} else {
			//同步线程中，另起线程进行数据库初始化
			new AsyncTask() {
				@Override
				protected Object doInBackground(Object... params) {
					executeInit(context);
					return null;
				}
			} .execute();
		}
	}
	
	private void executeInit(Context context) {
		synchronized (mDatabaseList) {
			for (LeDatabase database : mDatabaseList) {
				database.init(context);
			}
		}
	}
	
	public static void recycle() {
		if (sInstance != null) {
			sInstance.release();
		}
	}
	
	private void release() {
		synchronized (mDatabaseList) {
			for (LeDatabase database : mDatabaseList) {
				database.recycle();
			}
		}
	}
}
