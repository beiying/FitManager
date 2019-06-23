/** 
 * Filename:    BYSafeRunnable.java
 * Description:  
 * Copyright:   Lenovo PCL Copyright(c)2013 
 * @author:     chenwei27 
 * @version:    1.0
 * Create at:   2014-5-7 下午1:42:35
 * 
 * Modification History: 
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------ 
 * 2014-5-7     chenwei27    1.0         1.0 Version 
 */
package com.beiying.fitmanager.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

/**
 * LeSafeTask与Context进行绑定，即LeSafeTask实例依赖于Context生命周期，如果Context结束，
 * LeSafeTask的实现逻辑将不会执行。
 * 
 * 如果不希望与Context进行绑定，即执行独立于Context生命周期，请使用LeTask。但是，使用Runnable需谨慎，
 * 如果LeTask执行中使用了Context，需要调用#{@link LeSafeBox#isContextAlive(Context)}对Context有效性进行检查。
 */
public abstract class LeSafeTask extends AsyncTask<String, Integer, String> {
	
	private Context mContext;
	
	public LeSafeTask() {
		mContext = ContextContainer.sContext;
	}

	@Override
	protected void onPreExecute() {
		if (!LeSafeBox.isContextAlive(mContext)) {
			return;
		}
		onPreExecuteSafely();
	}

	@Override
	protected String doInBackground(String... params) {
		if (!LeSafeBox.isContextAlive(mContext)) {
			return null;
		}
		return doInBackgroundSafely(params);
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		if (!LeSafeBox.isContextAlive(mContext)) {
			return;
		}
		onProgressUpdateSafely(values);
	}

	@Override
	protected void onPostExecute(String result) {
		if (!LeSafeBox.isContextAlive(mContext)) {
			return;
		}
		onPostExecuteSafely(result);
	}

	/**
	 * Executes the task with the specified parameters
	 * 
	 * @param params
	 *            The parameters of the task
	 */
	@SuppressLint("NewApi")
	public void start(String... params) {
		if (!LeSafeBox.isContextAlive(mContext)) {
			return;
		}
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
			execute(params);
		} else {
			executeOnExecutor(THREAD_POOL_EXECUTOR, params);
		}
	}

	protected abstract void onPreExecuteSafely();

	protected abstract String doInBackgroundSafely(String... params);

	protected abstract void onProgressUpdateSafely(Integer... values);

	protected abstract void onPostExecuteSafely(String result);

}
