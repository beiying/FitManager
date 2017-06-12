/** 
 * Filename:    LeDatabase.java
 * Description:  
 * Copyright:   Lenovo PCL Copyright(c)2013 
 * @author:     chenwei27 
 * @version:    1.0
 * Create at:   2014-1-14 下午12:44:26
 * 
 * Modification History: 
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------ 
 * 2014-1-14     chenwei27    1.0         1.0 Version 
 */
package com.beiying.fitmanager.core.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.beiying.fitmanager.core.LeLog;

import java.util.ArrayList;
import java.util.List;

public class LeDatabase {

	private String mDbName;

	private List<LeSqliteTable> mTableList;

	private int mVersion;

	LeSqliteOpenHelper mOpenHelper;
	
	private boolean mReady = false;
	
	private LeDatabaseListener mListener;

	public LeDatabase(String dbName, int version) {
		mDbName = dbName;
		mVersion = version;
		mTableList = new ArrayList<LeSqliteTable>();
	}
	
	public void registerTable(LeSqliteTable table) {
		table.mDatabase = this;
		mTableList.add(table);
		
		LeSqliteManager.registerTable(table.mCls, table);
	}
	
	public LeSqliteTable getTable(Class cls) {
		return LeSqliteManager.getTable(cls);
	}

	public boolean init(Context context) {
		if (mDbName == null || mDbName.equals("") || mTableList.size() == 0) {
			return false;
		}
		mOpenHelper = new LeSqliteOpenHelper(context, this);
		try {
			SQLiteDatabase db = mOpenHelper.getReadableDatabase();
			if (db.isOpen()) {
				db.close();
			}
		} catch (Exception e) {
			LeLog.e(e);
		} finally {
			setReady();
			if (mListener != null) {
				mListener.onDbReady();
			}
		}
		return true;
	}
	
	public void setListener(LeDatabaseListener listener) {
		mListener = listener;
	}
	
	synchronized boolean isReady() {
		return mReady;
	}
	
	synchronized void setReady() {
		mReady = true;
	}
	
	String getName() {
		return mDbName;
	}

	int getVersion() {
		return mVersion;
	}

	List<LeSqliteTable> getTableList() {
		return mTableList;
	}
	
	public void recycle() {
		mOpenHelper = null;
	}
}
