package com.beiying.fitmanager.framework.model;

import android.database.sqlite.SQLiteDatabase;

import com.beiying.fitmanager.core.sqlite.LeColumnDef;
import com.beiying.fitmanager.core.sqlite.LeColumnDef.ColumnType;
import com.beiying.fitmanager.core.sqlite.LeSqliteConventer;
import com.beiying.fitmanager.core.sqlite.LeSqliteEntity;
import com.beiying.fitmanager.core.sqlite.LeSqliteTable;
import com.beiying.fitmanager.core.sqlite.LeTableListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class BYUserModel  extends LeSqliteEntity {
	public static final String DATABASE_TABLE_NAME = "user";
	public static final LeColumnDef COL_ID = new LeColumnDef("userid", ColumnType.TEXT);
	public static final LeColumnDef COL_EMAIL = new LeColumnDef("email", ColumnType.TEXT);
	public static final LeColumnDef COL_NAME = new LeColumnDef("username", ColumnType.TEXT);
	public static final LeColumnDef COL_NICK = new LeColumnDef("nickname", ColumnType.TEXT);
	public static final LeColumnDef COL_GENDER = new LeColumnDef("gender", ColumnType.INTEGER);
	public static final LeColumnDef COL_COUNTRY = new LeColumnDef("country", ColumnType.TEXT);
	public static final LeColumnDef COL_PROVINCE = new LeColumnDef("province", ColumnType.TEXT);
	public static final LeColumnDef COL_CITY = new LeColumnDef("city", ColumnType.TEXT);
	public static final LeColumnDef COL_AGE = new LeColumnDef("age", ColumnType.INTEGER);
	public static final LeColumnDef COL_HEIGHT = new LeColumnDef("height", ColumnType.FLOAT);
	public static final LeColumnDef COL_WEIGHT = new LeColumnDef("weight", ColumnType.FLOAT);
	public static final LeColumnDef COL_BMI = new LeColumnDef("bmi", ColumnType.FLOAT);
	
	public String mUserId;//用户ID
	public String mEmail;//用户邮箱
	public String mUserName;//用户真实姓名
	public String mNickName;//昵称
	public int mGender;//性别
	public String mCountry;//国家
	public String mProvince;//省份
	public String mCity;//城市
	public int mAge;//年龄
	public float mHeight;//身高
	public float mWeight;//体重
	public float mBMI;//BMI指数
	
	
	
	public static LeSqliteTable createTable() {
		LeSqliteTable sqliteTable = null;
		List<LeColumnDef> columnList = new ArrayList<LeColumnDef>();
		LeSqliteConventer conventer = new LeSqliteConventer() {
			
			@Override
			public Object convertToDb(LeColumnDef columnDef, LeSqliteEntity entity) {
				BYUserModel user = (BYUserModel)entity;
				if (columnDef == COL_ID) {
					return user.mUserId;
				} else if (columnDef == COL_AGE) {
					return user.mAge;
				} else if (columnDef == COL_BMI) {
					return user.mBMI;
				} else if (columnDef == COL_CITY) {
					return user.mCity;
				} else if (columnDef == COL_COUNTRY) {
					return user.mCountry;
				} else if (columnDef == COL_EMAIL) {
					return user.mEmail;
				} else if (columnDef == COL_GENDER) {
					return user.mGender;
				} else if (columnDef == COL_HEIGHT) {
					return user.mHeight;
				} else if (columnDef == COL_NAME) {
					return user.mUserName;
				} else if (columnDef == COL_NICK) {
					return user.mNickName;
				} else if (columnDef == COL_PROVINCE) {
					return user.mProvince;
				} else if (columnDef == COL_WEIGHT) {
					return user.mWeight;
				} 
				return null;
			}

			@Override
			public LeSqliteEntity convertFromDb(Class cls, Map<LeColumnDef, Object> colValueMap) {
				BYUserModel user = new BYUserModel();
				Set<Entry<LeColumnDef, Object>> entries = colValueMap.entrySet();
				for (Entry<LeColumnDef, Object> entry : entries) {
					LeColumnDef columnDef = entry.getKey();
					Object obj = colValueMap.get(columnDef);
					if (columnDef == COL_ID) {
						user.mUserId = (obj == null ? null : (String) obj);
					} else if (columnDef == COL_AGE) {
						user.mAge = (obj == null ? null : (Integer) obj);
					} else if (columnDef == COL_BMI) {
						user.mBMI = (obj == null ? null : (Float) obj);
					} else if (columnDef == COL_CITY) {
						user.mCity = (obj == null ? null : (String) obj);
					} else if (columnDef == COL_COUNTRY) {
						user.mCountry = (obj == null ? null : (String) obj);
					} else if (columnDef == COL_EMAIL) {
						user.mEmail = (obj == null ? null : (String) obj);
					} else if (columnDef == COL_GENDER) {
						user.mGender = (obj == null ? null : (Integer) obj);
					} else if (columnDef == COL_HEIGHT) {
						user.mHeight = (obj == null ? null : (Float) obj);
					} else if (columnDef == COL_NAME) {
						user.mUserName = (obj == null ? null : (String) obj);
					} else if (columnDef == COL_NICK) {
						user.mNickName = (obj == null ? null : (String) obj);
					} else if (columnDef == COL_PROVINCE) {
						user.mProvince = (obj == null ? null : (String) obj);
					} else if (columnDef == COL_WEIGHT) {
						user.mWeight = (obj == null ? null : (Float) obj);
					}
				}
				return user;
			}

		};
		
		LeTableListener listener = new LeTableListener() {
			
			@Override
			public void onReady(SQLiteDatabase db) {
				
			}

			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion, LeSqliteTable table) {

			}

			@Override
			public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
				
			}
		};
		
		sqliteTable = new LeSqliteTable(BYUserModel.class, DATABASE_TABLE_NAME, columnList, conventer, listener);
		return sqliteTable;
	}
	
	
	
	
}
