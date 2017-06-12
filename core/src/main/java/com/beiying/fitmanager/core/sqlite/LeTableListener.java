/** 
 * Filename:    LeTableListener.java
 * Description:  
 * Copyright:   Lenovo PCL Copyright(c)2013 
 * @author:     chenwei27 
 * @version:    1.0
 * Create at:   2014-1-23 上午10:25:00
 * 
 * Modification History: 
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------ 
 * 2014-1-23     chenwei27    1.0         1.0 Version 
 */
package com.beiying.fitmanager.core.sqlite;

import android.database.sqlite.SQLiteDatabase;

public interface LeTableListener {
	/**
	 * 方法内的数据库操作必须使用回传的SQLiteDatabase
	 * @param db
	 */
	void onReady(final SQLiteDatabase db);
	void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion, final LeSqliteTable table);
	void onDowngrade(final SQLiteDatabase db, final int oldVersion, final int newVersion);
}
