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
import java.util.Set;

/**
 * Created by beiying on 17/6/9.
 */

public class BYTrainModel extends LeSqliteEntity{
    public static final String DATABASE_TABLE_NAME = "bike";
    public static final LeColumnDef COL_NAME = new LeColumnDef("name", ColumnType.TEXT);
    public static final LeColumnDef COL_DESCRIPTION = new LeColumnDef("description", ColumnType.TEXT);
    public static final LeColumnDef COL_ICON = new LeColumnDef("icon", ColumnType.TEXT);
    public static final LeColumnDef COL_COUNT = new LeColumnDef("count", ColumnType.INTEGER);
    public static final LeColumnDef COL_TIME = new LeColumnDef("time", ColumnType.INTEGER);
    public static final LeColumnDef COL_CALORIE = new LeColumnDef("calorie", ColumnType.INTEGER);

    public String mName;
    public String mDescription;
    public String mIcon;
    public int mCount;
    public int mTime;
    public int mCalorie;

    public BYTrainModel() { }

    public static LeSqliteTable createTable() {
        LeSqliteTable table = null;
        List<LeColumnDef> columnDefs = new ArrayList<LeColumnDef>();
        LeSqliteConventer conventer = new LeSqliteConventer() {

            @Override
            public Object convertToDb(LeColumnDef columnDef, LeSqliteEntity entity) {
                BYTrainModel train = (BYTrainModel)entity;
                if (columnDef == COL_NAME) {
                    return train.mName;
                } else if (columnDef == COL_DESCRIPTION) {
                    return train.mDescription;
                } else if (columnDef == COL_ICON) {
                    return train.mIcon;
                } else if (columnDef == COL_COUNT) {
                    return train.mCount;
                } else if (columnDef == COL_CALORIE) {
                    return train.mCalorie;
                } else if (columnDef == COL_TIME) {
                    return train.mTime;
                }
                return null;
            }

            @Override
            public LeSqliteEntity convertFromDb(Class cls, Map<LeColumnDef, Object> colValueMap) {
                BYTrainModel train = new BYTrainModel();
                Set<Map.Entry<LeColumnDef, Object>> entries = colValueMap.entrySet();
                for (Map.Entry<LeColumnDef, Object> entry : entries) {
                    LeColumnDef columnDef = entry.getKey();
                    Object obj = colValueMap.get(columnDef);
                    if (columnDef == COL_CALORIE) {
                        train.mCalorie = (obj == null) ? null : (Integer) obj;
                    } else if (columnDef == COL_NAME) {
                        train.mName = (obj == null) ? null :(String) obj;
                    } else if (columnDef == COL_ICON) {
                        train.mIcon = (obj == null) ? null :(String) obj;
                    } else if (columnDef == COL_DESCRIPTION) {
                        train.mDescription = (obj == null) ? null :(String) obj;
                    } else if (columnDef == COL_COUNT) {
                        train.mCount = (obj == null) ? null :(Integer) obj;
                    } else if (columnDef == COL_TIME) {
                        train.mTime = (obj == null) ? null :(Integer) obj;
                    }
                }
                return train;
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
        table = new LeSqliteTable(BYTrainModel.class, DATABASE_TABLE_NAME, columnDefs, conventer, tableListener);
        return table;
    }

}
