/** 
 * Filename:    LeSharedPrefUnit.java
 * Description:  
 * Copyright:   Lenovo PCL Copyright(c)2013 
 * @author:     chenwei27 
 * @version:    1.0
 * Create at:   2014-2-14 下午2:27:57
 * 
 * Modification History: 
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------ 
 * 2014-2-14     chenwei27    1.0         1.0 Version 
 */
package com.beiying.fitmanager.core.data;

import android.content.Context;

import com.beiying.fitmanager.core.ContextContainer;
import com.beiying.fitmanager.core.LePrimitiveType;

/**
 * 主进程使用
 */
public class LeSharedPrefUnit extends ContextContainer {
	
	private String key = "";
	private Object value;
	private Object defaultValue = "";
	private boolean loaded = false;
	
	private LePrimitiveType mPrototype;
	
	private static LeSpHelper sCommonHelper;
	
	protected static LeSharedPrefFactory sFactory;

	public LeSharedPrefUnit(LePrimitiveType prototype, String key, Object defaultValue) {
		mPrototype = prototype;
		this.key = key;
		this.defaultValue = defaultValue;
	}
	
	public static void setFactory(LeSharedPrefFactory factory) {
		sFactory = factory;
	}
	
	public static void recycle() {
		sCommonHelper = null;
	}
	
	private static LeSpHelper createHelper() {
		if (sCommonHelper == null && sFactory != null) {
			sCommonHelper = sFactory.createCommonHelper();
		}
		return sCommonHelper;
	}
	
	public String getKey() {
		return key;
	}

	public Object getDefault() {
		return defaultValue;
	}

	boolean isLoaded() {
		return loaded;
	}

	void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}
	
	public boolean getBoolean() {
		return getBoolean(false);
	}
	
	public boolean getBoolean(boolean forceLoad) {
		Object value = getValue(null, null, forceLoad);
		return value == null ? false : (Boolean) value;
	}
	
	public int getInt() {
		Object value = getValue(null, null, false);
		return value == null ? 0 : (Integer) value;
	}
	
	public long getLong() {
		Object value = getValue(null, null, false);
		return value == null ? 0l : (Long) value;
	}
	
	public String getString(boolean forceLoad) {
		Object value = getValue(null, null, forceLoad);
		return value == null ? null : (String) value;
	}
	
	public String getString() {
		return getString(false);
	}
	
	public float getFloat() {
		Object value = getValue(null, null, false);
		return value == null ? 0f : (Float) value;
	}

	Object getValue(LeSpHelper spHelper, Context context, boolean forceLoad) {
		createHelper();
		
		if (!loaded || forceLoad) {
			value = loadValue(getHelper(spHelper), getContext(context));
			loaded = true;
		}
		return value;
	}
	
	/**
	 * context为null代表是commonSP，否则表示为多进程MP
	 * @param context
	 * @return
	 */
	private Context getContext(Context context) {
		return context == null ? ContextContainer.sContext : context;
	}
	
	/**
	 * spHelper为null代表是commonSP，否则表示为多进程MP
	 * @param spHelper
	 * @return
	 */
	private LeSpHelper getHelper(LeSpHelper spHelper) {
		return spHelper == null ? sCommonHelper : spHelper;
	}
	
	private Object loadValue(LeSpHelper spHelper, Context context) {
		Object value = null;
		if (mPrototype == LePrimitiveType.BOOLEAN) {
			value = spHelper.getBoolean(context, key, defaultValue == null ? false : (Boolean) defaultValue);
		} else if (mPrototype == LePrimitiveType.INTEGER) {
			value = spHelper.getInt(context, key, defaultValue == null ? 0 : (Integer) defaultValue);
		} else if (mPrototype == LePrimitiveType.LONG) {
			value = spHelper.getLong(context, key, defaultValue == null ? 0l : (Long) defaultValue);
		} else if (mPrototype == LePrimitiveType.STRING) {
			value = spHelper.getString(context, key, defaultValue == null ? null : (String) defaultValue);
		} else if (mPrototype == LePrimitiveType.FLOAT) {
			value = spHelper.getFloat(context, key, defaultValue == null ? 0f : (Float) defaultValue);
		}
		return value;
	}
	
	public void setValue(Object value) {
		setValue(null, null, value);
	}

	public void setValue(LeSpHelper spHelper, Context context, Object value) {
		createHelper();
		
		this.value = value;
		saveValue(getHelper(spHelper), getContext(context), value);
	}
	
	private void saveValue(LeSpHelper spHelper, Context context, Object value) {
		if (mPrototype == LePrimitiveType.BOOLEAN) {
			spHelper.putBoolean(context, key, value == null ? false : (Boolean) value);
		} else if (mPrototype == LePrimitiveType.INTEGER) {
			spHelper.putInt(context, key, value == null ? 0 : (Integer) value);
		} else if (mPrototype == LePrimitiveType.LONG) {
			spHelper.putLong(context, key, value == null ? 0l : (Long) value);
		} else if (mPrototype == LePrimitiveType.STRING) {
			spHelper.putString(context, key, value == null ? null : (String) value);
		} else if (mPrototype == LePrimitiveType.FLOAT) {
			spHelper.putFloat(context, key, value == null ? 0f : (Float) value);
		}
	}
}
