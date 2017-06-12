/** 
 * Filename:    LeSqliteConventer.java
 * Description:  
 * Copyright:   Lenovo PCL Copyright(c)2013 
 * @author:     chenwei27 
 * @version:    1.0
 * Create at:   2014-1-14 下午3:26:12
 * 
 * Modification History: 
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------ 
 * 2014-1-14     chenwei27    1.0         1.0 Version 
 */
package com.beiying.fitmanager.core.sqlite;

import java.util.Map;

public interface LeSqliteConventer {
	
	Object convertToDb(LeColumnDef columnDef, LeSqliteEntity entity);
	
	LeSqliteEntity convertFromDb(Class cls, Map<LeColumnDef, Object> colValueMap);

}
