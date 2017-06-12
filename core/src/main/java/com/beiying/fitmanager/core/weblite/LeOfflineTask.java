/** 
 * Filename:    LeOfflineTask.java
 * Description:  
 * Copyright:   Lenovo PCL Copyright(c)2013 
 * @author:     chenwei27 
 * @version:    1.0
 * Create at:   2014-2-27 下午2:40:41
 * 
 * Modification History: 
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------ 
 * 2014-2-27     chenwei27    1.0         1.0 Version 
 */
package com.beiying.fitmanager.core.weblite;

import android.content.Context;

import com.beiying.fitmanager.core.net.LeNetTask;
import com.beiying.fitmanager.core.weblite.LeWeblite.LeWebliteListener;

public class LeOfflineTask {
	
	private LeWeblite mWeblite;
	
	private LeOfflineListener mListener;

	public LeOfflineTask(Context context, String cacheRoot, String remoteUrl) {
		mWeblite = new LeWeblite(context, null, cacheRoot, remoteUrl, false, null);
		mWeblite.setWebliteListener(new LeWebliteListener() {
			
			@Override
			public void setupNetTask(LeNetTask netTask) {
				if (mListener != null) {
					mListener.setupNetTask(netTask);
				}
			}
			
			@Override
			public void onLoadLocalSuccess(String baseUrl, String data, String mimeType, String encoding,
					String failUrl) {
				if (mListener != null) {
					mListener.onLoadLocalSuccess(baseUrl, data, mimeType, encoding, failUrl);
				}
			}
			
			@Override
			public void onLoadFromServerSuccess(String baseUrl, String data,
		            String mimeType, String encoding, String failUrl) {
				if (mListener != null) {
					mListener.onLoadFromServerSuccess(baseUrl, data, mimeType, encoding, failUrl);
				}
			}
			
			@Override
			public void onLoadFromServerFail() {
				if (mListener != null) {
					mListener.onLoadFromServerFail();
				}
			}

			@Override
			public void onOverrideToRemoteUrl(String remoteUrl) {
				
			}
		});
	}
	
	public void offline() {
		mWeblite.loadFromServer(false);
	}
	
	public void loadCache() {
		mWeblite.loadLocal();
	}
	
	public void setListener(LeOfflineListener listener) {
		mListener = listener;
	}
	
	public interface LeOfflineListener extends LeWebliteListener {
		
	}

}
