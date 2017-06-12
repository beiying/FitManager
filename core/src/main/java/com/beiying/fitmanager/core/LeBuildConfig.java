/** 
 * Filename:    BuildConfig.java
 * Description:  
 * Copyright:   Lenovo PCL Copyright(c)2013 
 * @author:     chenwei27 
 * @version:    1.0
 * Create at:   2013-6-25 下午5:26:41
 * 
 * Modification History: 
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------ 
 * 2013-6-25     chenwei27    1.0         1.0 Version 
 */
package com.beiying.fitmanager.core;

public final class LeBuildConfig {

	private LeBuildConfig() {
		
	}

	/** DEBUG Mode */
	public static boolean DEBUG = false;
	
	public static void init(boolean debug) {
		DEBUG = debug;
		
		LeLog.init(debug);
	}

}