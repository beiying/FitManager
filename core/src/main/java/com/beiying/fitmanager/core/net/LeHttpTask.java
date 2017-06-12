package com.beiying.fitmanager.core.net;


import com.beiying.fitmanager.core.LeLog;
import com.beiying.fitmanager.core.LeSafeRunnable;
import com.beiying.fitmanager.core.LeThreadTask;
import com.beiying.fitmanager.core.data.LeLocalLoader;
import com.beiying.fitmanager.core.weblite.LeExpireTime;

public abstract class LeHttpTask {
	
	private static final boolean DEBUG = false;

	private LeLocalLoader mLoader;
	private LeHttpNet mNet;

	private LeSafeRunnable mUpdateRunnable;

	private LeHttpTaskListener mListener;
	
	private String mInvoker;
	
	public LeHttpTask(String targetUrl, String cachePath, String assertFile) {
		this(targetUrl, cachePath, assertFile, false, null);
	}

	public LeHttpTask(String targetUrl, String cachePath, String assertFile,
					  boolean checkHead, String cookie) {
		this(targetUrl, cachePath, assertFile, checkHead, cookie, false, false, null);
	}

	public LeHttpTask(String targetUrl, String cachePath, String assertFile,
					  boolean checkHead, String cookie, boolean modifiedCared, boolean expiredCared,
					  LeExpireTime expireTime) {
		mInvoker = getClass().getSimpleName();
		mNet = new LeHttpNet(targetUrl, null, checkHead, cookie, modifiedCared,
				expiredCared, expireTime) {

			@Override
			protected void setupNetTask(LeNetTask netTask) {
				if (!LeHttpTask.this.setupNetTask(netTask)) {
					super.setupNetTask(netTask);
				}
			}

			@Override
			public void onDisConnect(LeNetTask task) {
				if (DEBUG) {
					LeLog.e("httptask debug update:" + mTargetUrl);
				}
				if (mIsRightData) {
					try {
						if (LeHttpTask.this.onParse(task, new String(mBaos.toByteArray(), "utf-8"), false, false)) {

							super.saveHeadFields(task);
							saveCache(task, mBaos.toByteArray());

							mLoadFromServer = true;
							markSuccessTime();

							if (mListener != null) {
								mListener.onReqeustSuccess(task);
							}
						} else {
							if (mListener != null) {
								mListener.onRequestFail(task);
							}
						}

						closeStreams();

					} catch (Exception e) {
						LeLog.e(e);
						if (mListener != null) {
							mListener.onRequestFail(task);
						}
					}
				} else {
					if (mListener != null) {
						mListener.onRequestFail(task);
					}
				}
				mIsRequesting = false;
			}
			
			@Override
			protected String getInvoker() {
				return mInvoker;
			}

			/*@Override
			public void onThrowException(NetTask task) {
				mListener.onRequestFail();
			}*/
		};

		mLoader = new LeLocalLoader(cachePath, assertFile) {
			
			@Override
			protected void onLoadFail() {
				if (mListener != null) {
					mListener.onCacheLoadFail();
				}
				if (mUpdateRunnable != null) {
					mUpdateRunnable.runSafely();
					mUpdateRunnable = null;
				}
			}

			@Override
			public void onCacheLoaded(String data, boolean assetFile) {
				//LeLog.i("httptask debug load:" + mCachePath);
				if (LeHttpTask.this.onParse(null, data, true, assetFile)) {
					onParseSuccess(null);
					mCacheLoaded = true;

					if (mListener != null) {
						mListener.onCacheLoadSuccess();
					}
				} else {
					if (mListener != null) {
						mListener.onCacheLoadFail();
					}
				}
				if (mUpdateRunnable != null) {
					mUpdateRunnable.runSafely();
					mUpdateRunnable = null;
				}
			}
		};
	}
	
	protected static long fetchLastSuccessTime(String url) {
		return LeHttpNet.fetchLastSuccessTime(url);
	}
	
	protected static void setLastSuccessTime(long time) {
		LeHttpNet.setLastSuccessTime(time);
	}
	
	public long getRequestTime() {
		return mNet.getRequestTime();
	}
	
	public boolean isRequesting() {
		return mNet.isRequesting();
	}

	public boolean isExpired() {
		return mNet.isExpired();
	}

	public boolean isLoadFromServer() {
		return mNet.isLoadFromServer();
	}

	public void loadCache() {
		loadCache(LeThreadTask.PRIORITY_DEFAULT);
	}
	/**
	 * 
	 * @param priority see {@link LeLocalLoader#load(int)}
	 */
	public void loadCache(int priority) {
		mLoader.load(priority);
	}
	
	public void changeUrl(String url) {
		if (mNet != null) {
			mNet.changeUrl(url);
		}
	}

//	public boolean updateOnExpired(String param) {
//		return mNet.startWithProcessedUrl(param);
//	}
	
	/**
	 * 建议在Cache回调完成之后调用
	 * 
	 * 请求外部服务器调用该方法。该方法不会在url拼接任何参数
	 * @param url
	 * @param setting
	 */
	public void startDirectlyWithUrl(String url, Object setting) {
		mNet.startDirectlyWithUrl(url, setting);
	}
	
	/**
	 * 建议在Cache回调完成之后调用
	 * 
	 * 请求GreenTea服务器调用该方法。该方法会在url自动拼接GreenTea自定义参数
	 * 
	 * 更新请求会在Cache加载完成后再调用
	 * 
	 * @param param
	 * @param setting
	 */
	public void forceUpdate(final String param, final boolean ourServer, final Object setting) {
		if (mLoader.isLoadFromCache()) {
			mUpdateRunnable = null;
			mNet.forceStart(param, ourServer, setting);
		} else {
			mUpdateRunnable = new LeSafeRunnable() {
				
				@Override
				public void runSafely() {
					mNet.forceStart(param, ourServer, setting);
				}
			};
		}
	}
	
	/**
	 * 
	 * 请求GreenTea服务器调用该方法。该方法会在url自动拼接GreenTea自定义参数
	 * 
	 * 不管Cache是否加载，直接进行更新请求
	 * 
	 * @param param
	 * @param setting
	 */
	public void forceUpdateIgnoreCache(final String param, final boolean ourServer, final Object setting) {
		mNet.forceStart(param, ourServer, setting);
	}

	public boolean isLoadFromCache() {
		return mLoader.isLoadFromCache();
	}

	public void setListener(LeHttpTaskListener listener) {
		mListener = listener;
	}
	
	public long getLastSuccessTime() {
		return mNet.getLastSuccessTime();
	}

	protected void saveCache(LeNetTask task, byte[] data) {
		mLoader.saveCache(data);
	}

	protected boolean onParseSuccess(LeNetTask task) {
		return true;
	}

	protected boolean setupNetTask(LeNetTask netTask) {
		return false;
	}

	protected abstract boolean onParse(LeNetTask task, String data, boolean cacheParsing, boolean fromAsset);

	public interface LeHttpTaskListener {
		void onCacheLoadSuccess();
		
		void onCacheLoadFail();

		void onReqeustSuccess(LeNetTask task);

		void onRequestFail(LeNetTask task);
	}
}
