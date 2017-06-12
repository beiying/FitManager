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

import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;
import com.beiying.fitmanager.core.sqlite.LeColumnDef.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class LeSqliteTableNew extends LeSqliteTable {
	
	private SparseArray<LeColumnDef> mTagArray;

	public LeSqliteTableNew(Class cls, String tblName, LeTableListener listener) {
		super(cls, tblName, null, new LeSqliteConventer() {
			
			@Override
			public Object convertToDb(LeColumnDef columnDef, LeSqliteEntity entity) {
				return convertFromEntityToDb(columnDef, entity);
			}
			
			@Override
			public LeSqliteEntity convertFromDb(Class cls, Map<LeColumnDef, Object> colValueMap) {
				return convertFromDbToEntity(cls, colValueMap);
			}
		}, listener);
		
		mTagArray = new SparseArray<LeColumnDef>();
		List<LeColumnDef> columnList = fetchPersistentFields(mCls);
		if (columnList != null) {
			mColumnList.addAll(columnList);
		}
	}
	
	private List<LeColumnDef> fetchPersistentFields(Class cls) {
		List<LeColumnDef> list = null;
		Field[] fields = cls.getDeclaredFields();
		if (fields != null && fields.length > 0) {
			list = new ArrayList<LeColumnDef>();
			for (Field field : fields) {
				if (shouldPersistent(field)) {
					String name = "_" + field.getName();
					Class type = field.getType();
					ColumnType columnType = LeColumnDef.parseType(type);
					boolean indexing = field.getAnnotation(Indexing.class) != null;
					
					LeColumnDef columnDef = new LeColumnDef(name, columnType, indexing);
					
					TargetVersion targetVersion = field.getAnnotation(TargetVersion.class);
					if (targetVersion != null) {
						columnDef.mTargetVersion = targetVersion.value();
					}
					columnDef.mField = field;
					
					ColumnTag columnTag = field.getAnnotation(ColumnTag.class);
					if (columnTag != null) {
						mTagArray.put(columnTag.value(), columnDef);
					}
					
					list.add(columnDef);
				}
			}
		}
		return list;
	}
	
	private boolean shouldPersistent(Field field) {
		int modifier = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifier);
		if (isStatic) {
			return false;
		}
		Transient trans = field.getAnnotation(Transient.class);
		return trans == null;
	}
	
	public LeColumnDef getColumn(int columnTag) {
		return mTagArray.get(columnTag);
	}
	
	@Override
	protected void shouldAddColumns(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (mColumnList != null) {
			for (LeColumnDef columnDef : mColumnList) {
				if (columnDef.mTargetVersion > oldVersion) {
					addColumn(db, columnDef);
				}
			}
		}
	}
	
	private static LeSqliteEntity convertFromDbToEntity(Class cls, Map<LeColumnDef, Object> apColValueMap) {
		LeSqliteEntity entity = null;
		try {
			Constructor<?> constructor = cls.getDeclaredConstructor();
			constructor.setAccessible(true);
			entity = (LeSqliteEntity) constructor.newInstance();
			Set<Entry<LeColumnDef, Object>> colSet = apColValueMap.entrySet();
			for (Entry<LeColumnDef, Object> entry : colSet) {
				LeColumnDef columnDef = entry.getKey();
				Object value = apColValueMap.get(columnDef);
				
				Field field = columnDef.mField;
				if (field != null) {
					field.setAccessible(true);
					field.set(entity, value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return entity;
	}
	
	private static Object convertFromEntityToDb(LeColumnDef columnDef, LeSqliteEntity entity) {
		Object value = null;
		Field field = columnDef.mField;
		if (field != null) {
			field.setAccessible(true);
			try {
				value = field.get(entity);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		return value;
	}
}
