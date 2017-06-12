package com.beiying.fitmanager;

import com.beiying.fitmanager.core.sqlite.LeDatabase;
import com.beiying.fitmanager.core.sqlite.LeSqliteManager;
import com.beiying.fitmanager.framework.model.BYUserModel;
/**
 * 整个应用所有数据库初始化过程：注册数据库——注册数据库中的表——初始化数据库——完成创建
 */
public class BYFitnessBikeSqliter extends BYBasicContainer {

	public static void initDataBase() {
		LeSqliteManager.getInstance().register(new BYFitnessBikeDatabase("", 0));
		
		LeSqliteManager.getInstance().initAllDatabase(sContext);
	}
	
	public static class BYFitnessBikeDatabase extends LeDatabase {

		public BYFitnessBikeDatabase(String dbName, int version) {
			super(dbName, version);
			
			registerTables();
		}

		private void registerTables() {
			registerTable(BYUserModel.createTable());
		}
		
		
		
	}
}
