/** 
 * Filename:    LeTask.java
 * Description:  
 * Copyright:   Lenovo MIC Copyright(c)2011 
 * @author:     CoCoMo 
 * @version:    1.0
 * Create at:   2011-9-30 下午03:08:06
 * 
 * Modification History: 
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------ 
 * 2011-9-30     CoCoMo      1.0         1.0 Version 
 */
package com.beiying.fitmanager.core;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;

public class LeTask extends AsyncTask<String, Integer, String> {

	public LeTask() {
		
	}

	/**
	 * Executes the task with the specified parameters
	 * 
	 * @param params
	 *            The parameters of the task
	 */
	@SuppressLint("NewApi")
	public void start(String... params) {
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
			execute(params);
		} else {
			executeOnExecutor(THREAD_POOL_EXECUTOR, params);
		}
	}

	@Override
	protected void onPreExecute() {
		//Runs on the UI thread before doInBackground
	}

	@Override
	protected String doInBackground(String... params) {
		//Runs on a background thread
		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		//Runs on the UI thread after publishProgress is invoked
	}

	@Override
	protected void onPostExecute(String result) {
		//Runs on the UI thread after doInBackground
	}

	@Override
	protected void onCancelled() {
		//Runs on the UI thread after cancel(boolean) is invoked
	}

}
