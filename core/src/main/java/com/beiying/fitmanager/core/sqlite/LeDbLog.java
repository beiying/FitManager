/** 
 * Filename:    LeDbLog.java
 * Description:  
 * Copyright:   Lenovo PCL Copyright(c)2013 
 * @author:     chenwei27 
 * @version:    1.0
 * Create at:   2014-1-14 下午1:03:55
 * 
 * Modification History: 
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------ 
 * 2014-1-14     chenwei27    1.0         1.0 Version 
 */
package com.beiying.fitmanager.core.sqlite;

class LeDbLog {
	public static LeDbLogListener sListener;
	
	private LeDbLog() {
		
	}
	
	static void i(String msg) {
		if (sListener != null) {
			sListener.i(msg);
		}
	}

	public interface LeDbLogListener {
		void i(String msg);
	}
}
