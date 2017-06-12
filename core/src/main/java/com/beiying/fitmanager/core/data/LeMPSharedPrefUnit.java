/** 
 * Filename:    LeMPSharedPrefUnit.java
 * Description:  
 * Copyright:   Lenovo PCL Copyright(c)2013 
 * @author:     chenwei27 
 * @version:    1.0
 * Create at:   2014-2-14 下午3:57:15
 * 
 * Modification History: 
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------ 
 * 2014-2-14     chenwei27    1.0         1.0 Version 
 */
package com.beiying.fitmanager.core.data;

import android.content.Context;

import com.beiying.fitmanager.core.LePrimitiveType;


/**
 * 支持多进程
 */
public class LeMPSharedPrefUnit extends LeSharedPrefUnit {

	public LeMPSharedPrefUnit(LePrimitiveType prototype, String key, Object defaultValue) {
		super(prototype, key, defaultValue);
	}
	
	private static LeSpHelper getMultiProSpHelper(final Context context) {
		return new LeSpHelper(formSpName(context), Context.MODE_MULTI_PROCESS);
	}

	private static String formSpName(Context context) {
		return adjustSpName(context.getPackageName());
	}
	
	public static String adjustSpName(String name) {
		return name.replace(".", "_") + "_multipro_sp";
	}
	/**
	 * 多进程访问调用
	 * 
	 * @param context
	 * @return
	 */
	public String getMultiProString(Context context) {
		Object value = getValue(getMultiProSpHelper(context), context, true);
		return value == null ? null : (String) value;
	}

	/**
	 * 多进程访问调用
	 * 
	 * @param context
	 * @param value
	 */
	public void setMultiProValue(Context context, Object value) {
		setValue(getMultiProSpHelper(context), context, value);
	}
	
	public boolean getMultiProBoolean(Context context) {
		Object value = getValue(getMultiProSpHelper(context), context, true);
		return value == null ? null : (Boolean) value;
	}
}
