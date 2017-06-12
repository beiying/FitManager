/** 
 * Filename:    LeSpHelper.java
 * Description:  
 * Copyright:   Lenovo PCL Copyright(c)2013 
 * @author:     chenwei27 
 * @version:    1.0
 * Create at:   2013-12-23 下午3:01:06
 * 
 * Modification History: 
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------ 
 * 2013-12-23     chenwei27    1.0         1.0 Version 
 */
package com.beiying.fitmanager.core.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class LeSpHelper {

	private String mSpName;
	private int mMode = Context.MODE_PRIVATE;

	private SharedPreferences mSharedPref;
	private Editor mSharedPrefEditor;

	public LeSpHelper(String spName) {
		mSpName = spName;
	}

	public LeSpHelper(String spName, int mode) {
		mSpName = spName;
		mMode = mode;
	}
	
	public boolean getBoolean(Context context, final String key, boolean defaultValue) {
		openShared(context);
		if (mSharedPref == null) {
			return false;
		}
		boolean value = mSharedPref.getBoolean(key, defaultValue);
		return value;
	}
	
	public void putBoolean(Context context, final String key, boolean value) {
		openShared(context);
		mSharedPrefEditor.putBoolean(key, value);
		close();
	}
	
	public long getLong(Context context, final String key, long defaultValue) {
		openShared(context);
		if (mSharedPref == null) {
			return 0L;
		}
		long value = mSharedPref.getLong(key, defaultValue);
		return value;
	}
	
	public void putLong(Context context, final String key, long value) {
		openShared(context);
		mSharedPrefEditor.putLong(key, value);
		close();
	}
	
	public int getInt(Context context, final String key, int defaultValue) {
		openShared(context);
		if (mSharedPref == null) {
			return 0;
		}
		int value = mSharedPref.getInt(key, defaultValue);
		return value;
	}
	
	public void putInt(Context context, final String key, int value) {
		openShared(context);
		mSharedPrefEditor.putInt(key, value);
		close();
	}

	public String getString(Context context, final String key, String defaultValue) {
		openShared(context);
		if (mSharedPref == null) {
			return null;
		}
		String value = mSharedPref.getString(key, defaultValue);
		return value;
	}

	public void putString(Context context, final String key, String newValue) {
		openShared(context);
		mSharedPrefEditor.putString(key, newValue);
		close();
	}
	
	public float getFloat(Context context, final String key, float defalutValue) {
		openShared(context);
		if (mSharedPref == null) {
			return 0.0f;
		}
		float value = mSharedPref.getFloat(key, defalutValue);
		return value;
	}
	
	public void putFloat(Context context, final String key, float value) {
		openShared(context);
		mSharedPrefEditor.putFloat(key, value);
		close();
	}

	private synchronized void openShared(Context context) {
		if (mSharedPref == null || mSharedPrefEditor == null) {
			mSharedPref = context.getSharedPreferences(mSpName, mMode);
			mSharedPrefEditor = mSharedPref.edit();
		}
	}

	private synchronized void close() {
		if (mSharedPrefEditor != null) {
			mSharedPrefEditor.commit();
		}
		/*mSharedPrefEditor = null;
		mSharedPref = null;*/
	}
	
}
