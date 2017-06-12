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

public class BYBikeTrainModel extends LeSqliteEntity {
	public static final String DATABASE_TABLE_NAME = "bike";
	public static final LeColumnDef COL_TIME = new LeColumnDef("time", ColumnType.LONG);
	public static final LeColumnDef COL_DURATION = new LeColumnDef("duration", ColumnType.LONG);
	public static final LeColumnDef COL_SPEED = new LeColumnDef("speed", ColumnType.FLOAT);
	public static final LeColumnDef COL_DISTANCE = new LeColumnDef("distance", ColumnType.FLOAT);
	public static final LeColumnDef COL_STRENGTH = new LeColumnDef("strength", ColumnType.FLOAT);
	public static final LeColumnDef COL_HEART_RATE = new LeColumnDef("heartrate", ColumnType.INTEGER);
	public static final LeColumnDef COL_CALORIE = new LeColumnDef("calorie", ColumnType.FLOAT);
	
	
	public long mTime;//开始时间，ms为单位
	public long mDuration;//时长
	public float mSpeed;//速度
	public float mDistance;//里程
	public float mStrength;//蹬力
	public int mHeartRate;//心率
	public float mCalorie;//卡路里
	
	public BYBikeTrainModel(){
		
	}

	public static LeSqliteTable createTable() {
		LeSqliteTable table = null;
		List<LeColumnDef> columnDefs = new ArrayList<LeColumnDef>();
		LeSqliteConventer conventer = new LeSqliteConventer() {
			
			@Override
			public Object convertToDb(LeColumnDef columnDef, LeSqliteEntity entity) {
				BYBikeTrainModel fitness = (BYBikeTrainModel)entity;
				if (columnDef == COL_CALORIE) {
					return fitness.mCalorie;
				} else if (columnDef == COL_DISTANCE) {
					return fitness.mDistance;
				} else if (columnDef == COL_DURATION) {
					return fitness.mDuration;
				} else if (columnDef == COL_HEART_RATE) {
					return fitness.mHeartRate;
				} else if (columnDef == COL_SPEED) {
					return fitness.mSpeed;
				} else if (columnDef == COL_TIME) {
					return fitness.mTime;
				} else if (columnDef == COL_STRENGTH) {
					return fitness.mStrength;
				}
				return null;
			}

			@Override
			public LeSqliteEntity convertFromDb(Class cls, Map<LeColumnDef, Object> colValueMap) {
				BYBikeTrainModel fitnessBikeModel = new BYBikeTrainModel();
				Set<Entry<LeColumnDef, Object>> entries = colValueMap.entrySet();
				for (Entry<LeColumnDef, Object> entry : entries) {
					LeColumnDef columnDef = entry.getKey();
					Object obj = colValueMap.get(columnDef);
					if (columnDef == COL_CALORIE) {
						fitnessBikeModel.mCalorie = (obj == null) ? null : (Float) obj;
					} else if (columnDef == COL_DISTANCE) {
						fitnessBikeModel.mDistance = (obj == null) ? null :(Float) obj;
					} else if (columnDef == COL_DURATION) {
						fitnessBikeModel.mDuration = (obj == null) ? null :(Long) obj;
					} else if (columnDef == COL_HEART_RATE) {
						fitnessBikeModel.mHeartRate = (obj == null) ? null :(Integer) obj;
					} else if (columnDef == COL_SPEED) {
						fitnessBikeModel.mSpeed = (obj == null) ? null :(Float) obj;
					} else if (columnDef == COL_STRENGTH) {
						fitnessBikeModel.mStrength = (obj == null) ? null :(Float) obj;
					} else if (columnDef == COL_TIME) {
						fitnessBikeModel.mTime = (obj == null) ? null :(Long) obj;
					}
				}
				return fitnessBikeModel;
			}

		};
		
		LeTableListener tableListener = new LeTableListener() {
			
			@Override
			public void onReady(SQLiteDatabase db) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion, LeSqliteTable table) {

			}

			@Override
			public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
				// TODO Auto-generated method stub
				
			}
		};
		table = new LeSqliteTable(BYBikeTrainModel.class, DATABASE_TABLE_NAME, columnDefs, conventer, tableListener);
		return table;
	}
	
	
}
