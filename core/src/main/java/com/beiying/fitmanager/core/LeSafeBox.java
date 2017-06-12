/** 
 * Filename:    LeSafeBox.java
 * Description:  
 * Copyright:   Lenovo PCL Copyright(c)2013 
 * @author:     chenwei27 
 * @version:    1.0
 * Create at:   2014-5-12 下午4:11:52
 * 
 * Modification History: 
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------ 
 * 2014-5-12     chenwei27    1.0         1.0 Version 
 */
package com.beiying.fitmanager.core;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

public class LeSafeBox {
	
	private static final boolean DEBUG = false;
	private static final String DEBUG_TAG = "SafeBox";
	
	private static Map<Context, Boolean> sContextBoxMap = new HashMap<Context, Boolean>();
	
	public static void start(Context context) {
		sContextBoxMap.put(context, true);
		
		if (DEBUG) {
			LeLog.i(DEBUG_TAG, context + " start");
		}
	}
	
	public static void stop(Context context) {
		sContextBoxMap.remove(context);
		
		if (DEBUG) {
			LeLog.i(DEBUG_TAG, context + " stop");
		}
	}
	
	/**
	 * 检查Context是否存活。避免Activity结束后，异步线程对销毁Context使用造成的问题。
	 * 
	 * 该方法通常在异步线程执行时调用，传递当前需要使用的Context进行检查。
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isContextAlive(Context context) {
		boolean res = false;
		Boolean available = sContextBoxMap.get(context);
		if (available != null) {
			res = available.booleanValue();
		}
		if (DEBUG) {
			LeLog.i(DEBUG_TAG, context + " Alive:" + res);
		}
		return res;
	}
}
