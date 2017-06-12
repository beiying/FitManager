package com.beiying.fitmanager.core.sqlite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import com.beiying.fitmanager.core.LeLog;

import java.util.List;

public class LeSqliteOpenHelper extends SQLiteOpenHelper {

	/** 可以使用write_ahead模式的sdk版本号. */
	private static final int WRITE_AHEAD_START_SDK = 16;

	private LeDatabase mDatabase;

	public LeSqliteOpenHelper(Context context, LeDatabase database) {
		super(context, database.getName(), null, database.getVersion());
		mDatabase = database;

		setDefaultOptions();
	}

	/**
	 * 设置默认选项.
	 */
	@SuppressLint("NewApi")
	private void setDefaultOptions() {
		//4.1及以上版本，默认采用write ahead logging 模式
		if (Build.VERSION.SDK_INT >= WRITE_AHEAD_START_SDK) {
			setWriteAheadLoggingEnabled(true);
		}
	}

	@Override
	public void onCreate(final SQLiteDatabase db) {
		long in = System.currentTimeMillis();
		List<LeSqliteTable> tableList = mDatabase.getTableList();
		db.beginTransaction();
		try {
			for (LeSqliteTable table : tableList) {
				try {
					table.onCreate(db);
				} catch (Exception e) {
					LeLog.e(e);
				}
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			LeLog.e(e);
		} finally {
			db.endTransaction();
		}
		long out = System.currentTimeMillis();
		LeDbLog.i("create database[" + mDatabase.getName() + "] with time[" + (out - in) + "]");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		long in = System.currentTimeMillis();
		List<LeSqliteTable> tableList = mDatabase.getTableList();
		db.beginTransaction();
		try {
			for (LeSqliteTable table : tableList) {
				try {
					table.onUpgrade(db, oldVersion, newVersion);
				} catch (Exception e) {
					LeLog.e(e);
				}
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			LeLog.e(e);
		} finally {
			db.endTransaction();
		}
		long out = System.currentTimeMillis();
		LeDbLog.i("upgrade database[" + mDatabase.getName() + "] with time[" + (out - in) + "]");
	}

	@Override
	public void onDowngrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
		long in = System.currentTimeMillis();
		List<LeSqliteTable> tableList = mDatabase.getTableList();
		db.beginTransaction();
		try {
			for (LeSqliteTable table : tableList) {
				try {
					table.onDowngrade(db, oldVersion, newVersion);
				} catch (Exception e) {
					LeLog.e(e);
				}
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			LeLog.e(e);
		} finally {
			db.endTransaction();
		}
		long out = System.currentTimeMillis();
		LeDbLog.i("downgrade database[" + mDatabase.getName() + "] with time[" + (out - in) + "]");
	}
	
	public void onOpen(final SQLiteDatabase db) {
		List<LeSqliteTable> tableList = mDatabase.getTableList();
		db.beginTransaction();
		try {
			for (LeSqliteTable table : tableList) {
				try {
					table.onOpen(db);
				} catch (Exception e) {
					LeLog.e(e);
				}
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			LeLog.e(e);
		} finally {
			db.endTransaction();
		}
	}

}
