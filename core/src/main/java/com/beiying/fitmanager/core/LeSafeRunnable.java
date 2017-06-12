/** 
 * Filename:    LeSafeRunnable.java
 * Description:  
 * Copyright:   Lenovo PCL Copyright(c)2013 
 * @author:     chenwei27 
 * @version:    1.0
 * Create at:   2014-5-8 下午1:28:08
 * 
 * Modification History: 
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------ 
 * 2014-5-8     chenwei27    1.0         1.0 Version 
 */
package com.beiying.fitmanager.core;

import android.content.Context;

/**
 * LeSafeRunnable与Context进行绑定，即LeSafeRunnable实例依赖于Context生命周期，如果Context结束，
 * LeSafeRunnable的实现逻辑（runSafely）将不会执行。
 * 
 * 如果不希望与Context进行绑定，即执行独立于Context生命周期，请使用Runnable。但是，使用Runnable需谨慎，
 * 如果Runnable执行中使用了Context，需要调用#{@link LeSafeBox#isContextAlive(Context)}对Context有效性进行检查。
 */
public abstract class LeSafeRunnable implements Runnable {
	
	public Object mData;
	
	private boolean mCheckSafe = true;
	
	private Context mContext;
	
	public LeSafeRunnable() {
		this(null);
	}
	
	public LeSafeRunnable(Object data) {
		this(data, true);
	}
	
	public LeSafeRunnable(Object data, boolean checkSafe) {
		mContext = ContextContainer.sContext;
		mData = data;
		mCheckSafe = checkSafe;
	}
	
	@Override
	public void run() {
		if (mCheckSafe && !LeSafeBox.isContextAlive(mContext)) {
			return ;
		}
		runSafely();
		
		afterRun();
	}
	
	public void afterRun() {
		
	}

	public abstract void runSafely();
	
}
