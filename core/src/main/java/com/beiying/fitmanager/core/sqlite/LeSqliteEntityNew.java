/** 
 * Filename:    LeSqliteEntity.java
 * Description:  
 * Copyright:   Lenovo PCL Copyright(c)2013 
 * @author:     chenwei27 
 * @version:    1.0
 * Create at:   2014-1-14 下午5:04:32
 * 
 * Modification History: 
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------ 
 * 2014-1-14     chenwei27    1.0         1.0 Version 
 */
package com.beiying.fitmanager.core.sqlite;


import com.beiying.fitmanager.core.INoProGuard;

public abstract class LeSqliteEntityNew extends LeSqliteEntity implements INoProGuard {
	
	public LeSqliteEntityNew() {
		
	}
	
	public static LeColumnDef getColumn(Class cls, int columnTag) {
		LeSqliteTableNew tableNew = getTableNew(cls);
		return tableNew.getColumn(columnTag);
	}
	
	private static LeSqliteTableNew getTableNew(Class cls) {
		LeSqliteTable table = getTable(cls);
		if (table instanceof LeSqliteTableNew) {
			return (LeSqliteTableNew) table;
		}
		throw new LeSqliteException("LeSqliteEntityNew should use LeSqliteTableNew!");
	}
}
