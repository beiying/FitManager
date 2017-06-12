/** 
 * Filename:    LeSqliteTable.java
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

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.beiying.fitmanager.core.LeLog;
import com.beiying.fitmanager.core.utils.LeUtils;
import com.beiying.fitmanager.core.sqlite.LeColumnDef.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeSqliteTable {

	//索引
	public static final String IDX_PREFIX = "idx_";

	protected String mTableName;

	protected List<LeColumnDef> mColumnList;
	protected int mDbColumnCnt;

	protected LeSqliteConventer mConventer;

	protected LeDatabase mDatabase;

	private boolean mTableIndexSetted;
	
	private LeTableListener mListener;
	
	private boolean mReady;
	private SQLiteDatabase mUsableDb;
	
	Class mCls;
	
	public LeSqliteTable(Class cls, String tblName, List<LeColumnDef> columnList, LeSqliteConventer conventer,
						 LeTableListener listener) {
		mCls = cls;
		
		mTableName = tblName;

		mColumnList = new ArrayList<LeColumnDef>();
		List<LeColumnDef> dbColumnList = LeSqliteEntity.buildDbColumns();
		mDbColumnCnt = dbColumnList.size();
		mColumnList.addAll(dbColumnList);
		if (columnList != null) {
			mColumnList.addAll(columnList);
		}

		mConventer = conventer;
		
		mListener = listener;
		
		mReady = false;
		mUsableDb = null;
	}
	
	public static String nullSelection(LeColumnDef columnDef) {
		return columnDef.mName + " is null";
	}
	
	public static String notNullSelection(LeColumnDef columnDef) {
		String selection = columnDef.mName + " is not null";
		return selection;
	}
	
	public static String equalSelection(LeColumnDef columnDef, Object value) {
		String selection = null;
		if (columnDef.mColumnType == ColumnType.INTEGER) {
			selection = columnDef.mName + "=" + ((Integer) value).intValue();
		} else if (columnDef.mColumnType == ColumnType.LONG) {
			selection = columnDef.mName + "=" + ((Long) value).longValue();
		} else if (columnDef.mColumnType == ColumnType.TEXT) {
			selection = columnDef.mName + "='" + ((String) value) + "'";
		} else if (columnDef.mColumnType == ColumnType.BOOLEAN) {
			if (((Boolean) value).booleanValue()) {
				selection = columnDef.mName + ">0";
			} else {
				selection = columnDef.mName + "<0";
			}
		}
		return selection;
	}
	
	public static String likeSelection(LeColumnDef columnDef, Object value, boolean leftLike,
									   boolean rightLike) {
		String selection = null;
		if (columnDef.mColumnType == ColumnType.INTEGER) {
			selection = columnDef.mName + "=" + ((Integer) value).intValue();
		} else if (columnDef.mColumnType == ColumnType.LONG) {
			selection = columnDef.mName + "=" + ((Long) value).longValue();
		} else if (columnDef.mColumnType == ColumnType.TEXT) {
			value = ((String) value).replace("'", "''");
			if (!leftLike && !rightLike) {
				selection = columnDef.mName + "='" + value + "'";
			} else {
				StringBuilder sb = new StringBuilder();
				sb.append(columnDef.mName);
				sb.append(" LIKE '");
				sb.append(leftLike ? "%" : "");
				sb.append(value);
				sb.append(rightLike ? "%" : "");
				sb.append("'");
				selection = sb.toString();
			}
		} else if (columnDef.mColumnType == ColumnType.BOOLEAN) {
			if (((Boolean) value).booleanValue()) {
				selection = columnDef.mName + ">0";
			} else {
				selection = columnDef.mName + "<0";
			}
		}
		return selection;
	}

	private String columnFiledSql(LeColumnDef columnDef) {
		StringBuilder sb = new StringBuilder();
		sb.append(columnDef.mName + " " + columnDef.mColumnType.mKey);
		if (columnDef.mPrimaryKey) {
			sb.append(" PRIMARY KEY AUTOINCREMENT");
		} else {
			sb.append(" DEFAULT " + columnDef.mColumnType.mDefaultValue);
		}
		return sb.toString();
	}

	private String columnIndexSql(LeColumnDef columnDef) {
		return "CREATE INDEX IF NOT EXISTS " + indexName(columnDef) + " ON " + mTableName + " ("
				+ columnDef.mName + ");";
	}

	private String indexName(LeColumnDef columnDef) {
		return IDX_PREFIX + mTableName + columnDef.mName;
	}

	public void createTable(SQLiteDatabase db) {
		if (LeUtils.isEmptyCollection(mColumnList)) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE IF NOT EXISTS " + mTableName + " (");
		for (int index = 0; index < mColumnList.size(); index++) {
			LeColumnDef columnDef = mColumnList.get(index);
			String columnSql = columnFiledSql(columnDef);
			if (index > 0) {
				sb.append(", ");
			}
			sb.append(columnSql);
		}
		sb.append(");");
		String sql = sb.toString();
		db.execSQL(sql);

		for (LeColumnDef columnDef : mColumnList) {
			if (columnDef.mIndexing) {
				sql = columnIndexSql(columnDef);
				db.execSQL(sql);
			}
		}
	}
	
	public void addColumn(SQLiteDatabase db, LeColumnDef columnDef) {
		String columnSql = columnFiledSql(columnDef);
		
		StringBuilder sb = new StringBuilder();
		sb.append("ALTER TABLE ").append(mTableName).append(" ADD COLUMN ").append(columnSql);
		String sql = sb.toString();
		
		db.execSQL(sql);
	}

	/**
	 * 数据库新建时的回调.
	 * 
	 * @param db
	 *            SQLiteDatabase
	 */
	protected void onCreate(final SQLiteDatabase db) {
		mUsableDb = db;
		
		createTable(db);
		
		mUsableDb = null;
	}
	
	/**
	 * 数据库升级时的回调.
	 * 
	 * @param db
	 *            SQLiteDatabase
	 * @param oldVersion
	 *            旧数据库的版本号
	 * @param newVersion
	 *            新数据库的版本号
	 */
	protected void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
		mUsableDb = db;
		
		createTable(db);
		
		shouldAddColumns(db, oldVersion, newVersion);
		
		if (mListener != null) {
			mListener.onUpgrade(db, oldVersion, newVersion, this);
		}
		mUsableDb = null;
	}
	
	protected void shouldAddColumns(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
		
	}

	/**
	 * 数据库降级时的回调.
	 * 
	 * @param db
	 *            SQLiteDatabase
	 * @param oldVersion
	 *            旧数据库的版本号
	 * @param newVersion
	 *            新数据库的版本号
	 */
	protected void onDowngrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
		mUsableDb = db;
		
		createTable(db);
		
		if (mListener != null) {
			mListener.onDowngrade(db, oldVersion, newVersion);
		}
		mUsableDb = null;
	}
	
	/**
	 * 数据表创建、升级、阶级完成后回调
	 * @param db
	 */
	protected void onOpen(final SQLiteDatabase db) {
		if (!mReady) {
			mUsableDb = db;
			if (mListener != null) {
				mListener.onReady(db);
			}
			mUsableDb = null;
			mReady = true;
		}
	}
	
	public LeSqliteEntity insertFetch(LeSqliteEntity entity) {
		long id = insert(entity);
		String idSelection = LeSqliteEntity.idWhereClause(id);
		return querySingle(idSelection);
	}

	public long insert(LeSqliteEntity entity) {
		if (!dbUsable()) {
			return -1;
		}

		if (getUsableDb() == null) {
			return -1;
		}

		ContentValues values = convertToContentValues(entity);

		long id = getUsableDb().insert(mTableName, null, values);
		return id;
	}
	
	/**
	 * 批量插入事务,by zyb。
	 * @param list 插入的list
	 * @return 返回插入的条目数， 不一定准确，有可能插入失败.
	 */
	public int insertList(List<? extends LeSqliteEntity> list) {
		if (!dbUsable() || list == null) {
			return -1;
		}
		getUsableDb().beginTransaction();
		
		try {
			int n = list.size();
			for (int i = 0; i < n; i++) {
				insert((list.get(i)));
			}
			getUsableDb().setTransactionSuccessful();
		} catch (Exception e) {
			LeLog.e("zyb sql insert list exception!");
		} finally {
			getUsableDb().endTransaction();
		}
		return list.size();
	}
	
	private boolean dbUsable() {
		if (mReady) {
			return true;
		}
		if (mUsableDb != null || mDatabase.mOpenHelper.getWritableDatabase() != null) {
			return true;
		}
		return false;
	}
	
	private SQLiteDatabase getUsableDb() {
		if (!mReady) {
			return mUsableDb;
		}
		if (mDatabase == null || mDatabase.mOpenHelper == null) {
			return null;
		}
		return mDatabase.mOpenHelper.getWritableDatabase();
	}

	private ContentValues convertToContentValues(LeSqliteEntity entity) {
		ContentValues values = LeSqliteEntity.convertToContentValues(entity,
				mColumnList.subList(0, mDbColumnCnt));
		for (int index = mDbColumnCnt; index < mColumnList.size(); index++) {
			LeColumnDef columnDef = mColumnList.get(index);
			Object value = mConventer.convertToDb(columnDef, entity);
			if (columnDef == null) {
				return values;
			}
			if (value == null) {
				values.putNull(columnDef.mName);
			} else if (columnDef.mColumnType == ColumnType.INTEGER) {
				values.put(columnDef.mName, (Integer) value);
			} else if (columnDef.mColumnType == ColumnType.LONG) {
				values.put(columnDef.mName, (Long) value);
			} else if (columnDef.mColumnType == ColumnType.TEXT) {
				values.put(columnDef.mName, (String) value);
			} else if (columnDef.mColumnType == ColumnType.BOOLEAN) {
				values.put(columnDef.mName, (Boolean) value);
			} else if (columnDef.mColumnType == ColumnType.BYTES) {
				values.put(columnDef.mName, (byte[]) value);
			} else if (columnDef.mColumnType == ColumnType.FLOAT) {
				values.put(columnDef.mName, (Float) value);
			}
		}
		return values;
	}
	
	public int delete(LeSqliteEntity entity) {
		if (entity.mDbId == -1) {
			throw new LeSqliteException("Could not update detattch entity!");
		}
		String whereClause = entity.idWhereClause();
		return delete(whereClause);
	}

	public int delete(String whereClause) {
		if (!dbUsable()) {
			return -1;
		}
		return getUsableDb().delete(mTableName, whereClause, null);
	}
	
	public LeSqliteEntity querySingle(String singleSelection) {
		List list = query(singleSelection);
		if (list != null && list.size() > 0) {
			return (LeSqliteEntity) list.get(0);
		}
		return null;
	}

	public List query(String selection) {
		return query(selection, null, null);
	}
	
	public List query(String selection, String sortOrder, String limit) {
		List list = null;
		try {
			if (!dbUsable()) {
				return null;
			}
			Cursor cursor = getUsableDb().query(mTableName, null, selection,
					null, null, null, sortOrder, limit);
			list = convertCursor(cursor);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	protected int countQuery(String selection) {
		int count = 0;
		if (!dbUsable()) {
			return count;
		}
		String sql = "select count(*) from " + mTableName + " where " + selection;
		if (getUsableDb() != null) {
			Cursor countCursor = getUsableDb().rawQuery(sql, null);
			if (countCursor != null) {
				countCursor.moveToFirst();
				count = countCursor.getInt(0);
				countCursor.close();
			}
		}
		return count;
	}

	private List<LeSqliteEntity> convertCursor(Cursor cursor) {
		if (cursor == null) {
			return null;
		}
		setColumnIndex(cursor);

		List<LeSqliteEntity> list = new ArrayList<LeSqliteEntity>();
		while (cursor.moveToNext()) {
			LeSqliteEntity entity = convertOneCursor(cursor);
			list.add(entity);
		}
		cursor.close();
		return list;
	}

	private LeSqliteEntity convertOneCursor(Cursor cursor) {
		Map<LeColumnDef, Object> dbColValueMap = new HashMap<LeColumnDef, Object>();
		Map<LeColumnDef, Object> apColValueMap = new HashMap<LeColumnDef, Object>();
		for (int index = 0; index < mDbColumnCnt; index++) {
			LeColumnDef columnDef = mColumnList.get(index);
			Object value = parseCursor(cursor, columnDef);
			dbColValueMap.put(columnDef, value);
		}
		for (int index = mDbColumnCnt; index < mColumnList.size(); index++) {
			LeColumnDef columnDef = mColumnList.get(index);
			Object value = parseCursor(cursor, columnDef);
			apColValueMap.put(columnDef, value);
		}
		LeSqliteEntity entity = mConventer.convertFromDb(mCls, apColValueMap);
		LeSqliteEntity.convertFromDb(entity, dbColValueMap);
		return entity;
	}
	
	private Object parseCursor(Cursor cursor, LeColumnDef columnDef) {
		Object value = null;
		if (columnDef.mColumnType == ColumnType.INTEGER) {
			value = cursor.getInt(columnDef.mTableIndex);
		} else if (columnDef.mColumnType == ColumnType.LONG) {
			value = cursor.getLong(columnDef.mTableIndex);
		} else if (columnDef.mColumnType == ColumnType.TEXT) {
			value = cursor.getString(columnDef.mTableIndex);
		} else if (columnDef.mColumnType == ColumnType.BOOLEAN) {
			value = cursor.getInt(columnDef.mTableIndex) > 0;
		} else if (columnDef.mColumnType == ColumnType.BYTES) {
			value = cursor.getBlob(columnDef.mTableIndex);
		} else if (columnDef.mColumnType == ColumnType.FLOAT) {
			value = cursor.getFloat(columnDef.mTableIndex);
		}
		return value;
	}

	private void setColumnIndex(Cursor cursor) {
		if (mTableIndexSetted) {
			return;
		}
		for (LeColumnDef columnDef : mColumnList) {
			columnDef.mTableIndex = cursor.getColumnIndex(columnDef.mName);
		}
		mTableIndexSetted = true;
	}

	public int update(LeSqliteEntity entity) {
		if (entity.mDbId == -1) {
			throw new LeSqliteException("Could not update detattch entity!");
		}
		ContentValues values = convertToContentValues(entity);
		String whereClause = entity.idWhereClause();
		return update(values, whereClause);
	}

	public int update(ContentValues values, String whereClause) {
		if (!dbUsable()) {
			return -1;
		}
		return getUsableDb().update(mTableName, values, whereClause, null);
	}

}
