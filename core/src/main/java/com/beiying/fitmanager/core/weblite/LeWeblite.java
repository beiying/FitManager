package com.beiying.fitmanager.core.weblite;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.beiying.fitmanager.core.LeSafeRunnable;
import com.beiying.fitmanager.core.net.LeNetTask;
import com.beiying.fitmanager.core.net.LeHttpNet.LeUrlProcessor;

public class LeWeblite {
	
	public static boolean sAutoGenAssertFile = false;
	
	private LeWebliteTask mTask;
	private LeWebliteListener mListener;
	String mToken;
	private Handler handerMessage = new Handler() {
		public void handleMessage(Message msg) {
			
		}
	};
	
	protected LeUrlOverrider mUrlOverrider;

	public LeWeblite(Context context, String assertFile, String cacheRoot, String remoteUrl, boolean ourServer,
			LeExpireTime expireTime) {
		init(context, assertFile, cacheRoot, remoteUrl, ourServer, expireTime);
	}
	
	public static void init(LeUrlProcessor processor) {
		LeWebliteNet.sUrlProcessor = processor;
	}
	
	private LeUrlOverrider createUrlOverrider() {
		return new LeUrlOverrider() {
			
			@Override
			public boolean override(String url) {
				final String remoteUrl = mTask.adjustToRemoteUrl(url);
				if (remoteUrl != null) {
					new Handler(Looper.getMainLooper()).post(new LeSafeRunnable() {
						
						@Override
						public void runSafely() {
							if (mListener != null) {
								mListener.onOverrideToRemoteUrl(remoteUrl);
							}
						}
					});
					return true;
				}
				return false;
			}
		};
	}
	
	public boolean checkFileUri(String uri) {
		return mTask.checkFileUri(uri);
	}
	
	public String adjustToRemoteUrl(String localUrl) {
		return mTask.adjustToRemoteUrl(localUrl);
	}
	
	public String getLocalContent() {
		if (mTask.mCacheContent != null) {
			return mTask.mCacheContent;
		}
		return mTask.mRawContent;
	}

	public Handler getHandler() {
		return handerMessage;
	}

	private void init(Context context, String assertFile, String cacheRoot, String remoteUrl, boolean ourServer, 
			LeExpireTime expireTime) {
		mToken = null;
		
		mUrlOverrider = createUrlOverrider();
		
		mTask = new LeWebliteTask(context, assertFile, cacheRoot, remoteUrl, ourServer, expireTime);
		mTask.load(this);
	}
	
	public boolean isCacheUsable() {
		return mTask.isCacheUsable();
	}

	public boolean isLoadFromServer() {
		return mTask.isLoadFromServer();
	}

	public boolean isLoadFromCache() {
		return mTask.isLoadFromCache();
	}

	public boolean isExpired() {
		return mTask.isExpired();
	}

	public void updateUpdateTime() {
		mTask.updateUpdateTime();
	}

	public String getToken() {
		return mToken;
	}

	public void setToken(String token) {
		this.mToken = token;
	}

	public void forceLoadLocal() {
		if (mTask != null) {
			mTask.forceLoadLocal();
		}
	}

	public void loadLocal() {
		if (mTask != null) {
			mTask.loadLocal();
		}
	}

	public boolean loadFromServer(boolean checkExpired) {
		if (mTask != null) {
			return mTask.loadFromServerOnExpire(checkExpired);
		}
		return false;
	}

	public void onReceiveVersion(int version) {
		if (mTask != null) {
			mTask.onReceiveVersion(version);
		}
	}

	public boolean shouldUpdate() {
		boolean ret = false;
		if (mTask != null) {
			ret = mTask.shouldUpdate();
		}
		return ret;
	}

	public void updateUrl(String url) {
		if (mTask != null) {
			mTask.updateUrl(url);
		}
	}

	public void onClearCache() {
		mTask.onClearCache();
	}

	public void setWebliteListener(LeWebliteListener listener) {
		mListener = listener;
	}

	public void onLoadFromServerSuccess(String baseUrl, String data,
            String mimeType, String encoding, String failUrl) {
		if (mListener != null) {
			mListener.onLoadFromServerSuccess(baseUrl, data, mimeType, encoding, failUrl);
		}
	}

	public void onLoadLocalSuccess(String baseUrl, String data,
            String mimeType, String encoding, String failUrl) {
		if (mListener != null) {
			mListener.onLoadLocalSuccess(baseUrl, data, mimeType, encoding, failUrl);
		}
	}

	public void onLoadFromServerFail() {
		if (mListener != null) {
			mListener.onLoadFromServerFail();
		}
	}

	public void setupNetTask(LeNetTask netTask) {
		if (mListener != null) {
			mListener.setupNetTask(netTask);
		}
	}

	public interface LeWebliteListener {

		void onLoadFromServerSuccess(String baseUrl, String data,
	            String mimeType, String encoding, String failUrl);

		void onLoadLocalSuccess(String baseUrl, String data,
	            String mimeType, String encoding, String failUrl);

		void onLoadFromServerFail();

		void setupNetTask(LeNetTask netTask);
		
		void onOverrideToRemoteUrl(String remoteUrl);

	}

}
