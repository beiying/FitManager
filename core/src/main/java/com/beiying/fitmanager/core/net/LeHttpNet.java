package com.beiying.fitmanager.core.net;

import android.text.format.DateUtils;
import android.util.Log;

import com.beiying.fitmanager.core.LeLog;
import com.beiying.fitmanager.core.LePrimitiveType;
import com.beiying.fitmanager.core.data.LeSharedPrefUnit;
import com.beiying.fitmanager.core.utils.LeUtils;
import com.beiying.fitmanager.core.weblite.LeExpireTime;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class LeHttpNet implements INetConnListener {
	
	private static final boolean DEBUG = false;

    private static final String TAG = "LeHttpNet";

	private static final String PREF_LAST_MODIFIED = "_last_modified";
	private static final String PREF_EXPIRED = "_expired";
	private static final String PREF_LAST_SUCCESS = "_last_success";
	private static final int CONN_TIMEOUT_SECONDS = 5;
	private static final int READ_TIMEOUT_SECONDS = 15;

	private LeNetTask mTask;
	
	protected String mTargetUrl;

	protected boolean mIsRightData;

	protected ByteArrayOutputStream mBaos;
	private DataOutputStream mDos;

	private LeHttpNetListener mNetListener;

	private boolean mCheckHead;
	private String mCookie;

	private boolean mCheckModified;
	private boolean mCheckExpired;
	private String mLastModified;
	private long mExpiredInMillis;
	private LeExpireTime mExpireTime;
	private String mPrefName;

	protected boolean mLoadFromServer;
	
	protected boolean mIsRequesting;
	
	protected long mRequestTime;
	
	protected long mLastSuccessTime;
	
	private LeSharedPrefUnit mLastModifiedPref;
	
	private LeSharedPrefUnit mLastSuccessPref;
	
	private LeSharedPrefUnit mExpiredPef;
	
	private static LeSharedPrefUnit mLastSuccessTimePref;
	
	private static LeUrlProcessor sUrlProcessor;

	public LeHttpNet(String url) {
		this(url, null);
	}
	
	protected LeHttpNet(String url, String prefName) {
		this(url, prefName, false, null, false, false, null);
	}
	
	public LeHttpNet(String url, String prefName, boolean checkHead, String cookie,
					 boolean modifiedCared, boolean expiredCared, LeExpireTime expireTime) {
		mTargetUrl = url;
		mPrefName = prefName;
		if (mPrefName == null && !LeUtils.isEmptyString(mTargetUrl)) {
			mPrefName = formDefaultPrefNamePrefix(mTargetUrl);
		}
		mCheckHead = checkHead;
		if (mCheckHead) {
			if (cookie == null) {
				mCookie = sUrlProcessor.getAuth();
			} else {
				mCookie = cookie;
			}
		}
		
		mLoadFromServer = false;

		mCheckModified = modifiedCared;
		mCheckExpired = expiredCared;

		mLastSuccessTime = loadLastSuccessTime();

		if (mCheckModified) {
			mLastModified = loadLastModified();
		}

		if (mCheckExpired) {
			mExpireTime = expireTime;
			mExpiredInMillis = loadExpired();
		}
	}
	
	public void changeUrl(String url) {
		mTargetUrl = url;
		if (mPrefName == null && !LeUtils.isEmptyString(mTargetUrl)) {
			mPrefName = formDefaultPrefNamePrefix(mTargetUrl);
		}
	}
	
	public void setToken(String token) {
		if (token != null) {
			mCheckHead = true;
			mCookie = token;
		} else {
			mCheckHead = false;
		}
	}
	
	public static String getDefalutToken() {
		if (sUrlProcessor != null) {
			return sUrlProcessor.getAuth();
		}
		return null;
	}
	
	private static String formDefaultPrefNamePrefix(String url) {
		return String.valueOf(url.hashCode());
	}
	
	public static void init(LeUrlProcessor ulrProcessor) {
		sUrlProcessor = ulrProcessor;
	}

	public boolean isExpired() {
		long currentLong = System.currentTimeMillis();
		if (currentLong > mExpiredInMillis) {
			return true;
		}
		return false;
	}

	public boolean isLoadFromServer() {
		return mLoadFromServer;
	}

//	public boolean startWithProcessedUrl(String param) {
//		String requestUrl = processUrl(param);
//		return startDirectlyWithUrl(requestUrl, null);
//	}

	public boolean startDirectlyWithUrl(String url, Object setting) {
		return startOnExpire(url, true, setting);
	}

	public boolean forceStart(String param, boolean ourServer, Object setting) {
		String requestUrl;
		if (ourServer) {
			requestUrl = processUrl(param);
		} else {
			requestUrl = combine(mTargetUrl, param);
		}
		return startOnExpire(requestUrl, false, setting);
	}
	
	private String processUrl(String param) {
		if (param == null) {
			param = "";
		}
		String processedUrl = mTargetUrl;
		if (sUrlProcessor != null) {
			processedUrl = sUrlProcessor.process(processedUrl);
		}
		String requestUrl =  combine(processedUrl, param);
		return requestUrl;
	}
	
	private String combine(String url, String param) {
		if (url == null) {
			return param;
		}
		if (param == null || param.equals("")) {
			return url;
		}
		
		if (param.startsWith("?")) {
			param = param.replace("?", "");
		}
		if (url.contains("?")) {
			if (!param.startsWith("&")) {
				param = "&" + param;
			}
			return url + param;
		} else {
			if (param.startsWith("&")) {
				param = param.replaceFirst("&", "");
			}
			return url + "?" + param;
		}
	}
	
	private boolean startOnExpire(String url, boolean expiredCared) {
		return startOnExpire(url, expiredCared, null);
	}

	private boolean startOnExpire(String url, boolean expiredCared, Object setting) {
		if (url == null) {
			return false;
		}
		if (expiredCared) {
			if (!mCheckExpired || isExpired()) {
				return startNetTask(url, setting);
			}
		} else {
			return startNetTask(url, setting);
		}
		return false;
	}

	private boolean startNetTask(String url) {
		return startNetTask(url, null);
	}
	
	private boolean startNetTask(String url, Object setting) {
		mIsRequesting = true;
		
		mTask = new LeNetTask();
		if (setting != null) {
			mTask.setSetting(setting);
		}


		mTask.setListener(this);
		mTask.setM_url(url);
		mTask.setConnTimeOut((int) (DateUtils.SECOND_IN_MILLIS * CONN_TIMEOUT_SECONDS));
		mTask.setReadTimeOut((int) (DateUtils.SECOND_IN_MILLIS * READ_TIMEOUT_SECONDS));

		setupNetTask(mTask);
		
		if (mCheckModified && mLastModified != null) {
			mTask.addHttpHeads("if-modified-since", mLastModified);
		}

		LeNetManager.getInstance().getNet().startTask(mTask);
		mRequestTime = System.currentTimeMillis();
		
		if (DEBUG) {
			mTask.dump(TAG, getInvoker());
		}
		
		return true;
	}
	
	protected String getInvoker() {
		String invoker = this.getClass().getSimpleName();
        if (invoker == null || invoker.equals("")) {
            invoker = this.getClass().getName();
        }
        return invoker;
	}
	
	protected void setupNetTask(LeNetTask netTask) {
		mTask.setNetMode(LeNet.GET);
	}

	@Override
	public void onConnStart(LeNetTask task) {
		try {
			mBaos = new ByteArrayOutputStream();
			mDos = new DataOutputStream(mBaos);
		} catch (Exception e) {
			LeLog.e(e);
		}
	}

	@Override
	public void onReceiveHead(LeNetTask task) {
		
		if (mCheckHead) {

			checkHeadCookie(task);

		} else {
			mIsRightData = true;
		}

		if (mIsRightData) {
			if (mNetListener != null) {
				mNetListener.onReceiveHeadSuccess();
			}
		} else {
			mTask.stopConn();
			if (mNetListener != null) {
				mNetListener.onRequestFail();
			}
		}
	}

	private String loadLastModified() {
		if (mLastModifiedPref == null) {
			mLastModifiedPref = new LeSharedPrefUnit(LePrimitiveType.STRING, mPrefName + PREF_LAST_MODIFIED, null);
		}
		return mLastModifiedPref.getString();
	}

	private long loadExpired() {
		if (mExpiredPef == null) {
			mExpiredPef = new LeSharedPrefUnit(LePrimitiveType.LONG, mPrefName + PREF_EXPIRED, 0l);
		}
		return mExpiredPef.getLong();
	}

	protected void saveLastMOdified(String lastModified) {
		mLastModifiedPref.setValue(lastModified);
	}

	protected void saveExpired(long expireInMillis) {
		mExpiredPef.setValue(expireInMillis);
	}
	
	protected void saveLastSuccessTime(long lastSuccess) {
		mLastSuccessPref.setValue(lastSuccess);
	}
	
	private long loadLastSuccessTime() {
		if (mLastSuccessPref == null) {
			mLastSuccessPref = new LeSharedPrefUnit(LePrimitiveType.LONG, mPrefName + PREF_LAST_SUCCESS, 0l);
		}
		return mLastSuccessPref.getLong();
	}
	
	public static long fetchLastSuccessTime(String url) {
		if (mLastSuccessTimePref == null) {
			mLastSuccessTimePref = new LeSharedPrefUnit(LePrimitiveType.LONG, formDefaultPrefNamePrefix(url) + PREF_LAST_SUCCESS, 0l);
		}
		return mLastSuccessTimePref.getLong();
	}
	
	public static void setLastSuccessTime(long lastSuccessTime) {
		mLastSuccessTimePref.setValue(lastSuccessTime);
	}

	@Override
	public void onResponseCode(LeNetTask task, int resCode) {
        LeLog.i(TAG, getInvoker() + ": " + resCode + "--" + mTask.getM_url());
	}

	@Override
	public void onReceiveData(LeNetTask task, byte[] data, int len) {
		if (mTask.equals(task) && mIsRightData) {
			try {
				if (mDos != null) {
					mDos.write(data, 0, len);
				}
			} catch (IOException e) {
				LeLog.e(e);
			}
		}
	}

	@Override
	public void onDisConnect(LeNetTask task) {
		if (mIsRightData) {
			try {

				saveHeadFields(task);
				
				//String data = new String(mBaos.toByteArray(), "utf-8");
				byte[] data = mBaos.toByteArray();
				
				if (onParse(data, task)) {
					if (mNetListener != null) {
						mNetListener.onReceiveSuccess(data);
					}
                    if (DEBUG) {
                        LeLog.i(TAG, new String(data, "utf-8"));
                    }
	
					closeStreams();
	
					mLoadFromServer = true;
					markSuccessTime();
				} else {
					if (mNetListener != null) {
						mNetListener.onRequestFail();
					}
				}

			} catch (Exception e) {
				if (mNetListener != null) {
					mNetListener.onRequestFail();
				}
                LeLog.e(TAG, getInvoker());
				LeLog.e(TAG, Log.getStackTraceString(e));
			}
		} else {
			if (mNetListener != null) {
				mNetListener.onRequestFail();
			}
		}
		mIsRequesting = false;
	}
	
	protected void markSuccessTime() {
		mLastSuccessTime = System.currentTimeMillis();
		saveLastSuccessTime(mLastSuccessTime);
	}

	protected void saveHeadFields(LeNetTask task) {
		if (mCheckModified) {
			String newLastModified = task.getUrlConn().getHeaderField("Last-Modified");
			if (newLastModified != null) {
				if (mLastModified == null || !mLastModified.equals(newLastModified)) {
					mLastModified = newLastModified;
					saveLastMOdified(mLastModified);
				}
			}
		}
		if (mCheckExpired) {
			String expire = task.getUrlConn().getHeaderField("Expires");
			long expireInMillis = System.currentTimeMillis() + mExpireTime.mDefault;
			if (expire != null) {
				expireInMillis = LeExpireTime.parseExpires(expire, mExpireTime.mDefault,
						mExpireTime.mMin, mExpireTime.mMax);
			}
			if (expireInMillis != mExpiredInMillis) {
				mExpiredInMillis = expireInMillis;
				saveExpired(mExpiredInMillis);
			}
		}
	}
	
	protected boolean onParse(byte[] data, LeNetTask task) {
		return true;
	}

	@Override
	public void onThrowException(LeNetTask task) {
		mIsRightData = false;
	}


	protected void closeStreams() {
		try {
			mDos.close();
			mBaos.close();
			mDos = null;
			mBaos = null;
		} catch (Exception e) {
			LeLog.e(e);
		}
	}

	private void checkHeadCookie(LeNetTask task) {
		Map<String, List<String>> heads = task.getUrlConn().getHeaderFields();
		if (heads != null) {
			Iterator<Entry<String, List<String>>> iter = heads.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, List<String>> entry = iter.next();
				String headKey = entry.getKey();
				if (headKey != null && headKey.toLowerCase().contains("cookie")) {
					List<String> headValues = entry.getValue();
					for (String headValue : headValues) {
						LeLog.v("Cookie value=" + headValue);
						if (headValue != null) {
							String[] values = headValue.split(";");
							for (String value : values) {
								if (value.equalsIgnoreCase(mCookie)) {
									mIsRightData = true;
								}
							}
						}
					}
				}
			}
		}
	}

	public void setListener(LeHttpNetListener listener) {
		mNetListener = listener;
	}
	
	public boolean isRequesting() {
		return  mIsRequesting;
	}
	
	public long getRequestTime() {
		return mRequestTime;
	}
	
	public long getLastSuccessTime() {
		return mLastSuccessTime;
	}

	public interface LeHttpNetListener {

		void onRequestFail();

		void onReceiveSuccess(byte[] data);

		void onReceiveHeadSuccess();
	}
	
	public interface LeUrlProcessor {
		String process(String originalUrl);
		String getAuth();
	}
}
