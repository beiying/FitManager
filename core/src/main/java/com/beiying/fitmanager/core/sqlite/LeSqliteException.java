/** 
 * Filename:    LeSqliteException.java
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

public class LeSqliteException extends RuntimeException {

	public LeSqliteException() {
		super();
	}
	
	public LeSqliteException(String msg) {
		super(msg);
	}
}
