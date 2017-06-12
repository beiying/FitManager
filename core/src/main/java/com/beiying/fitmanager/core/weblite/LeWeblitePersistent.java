package com.beiying.fitmanager.core.weblite;

import android.content.Context;

import com.beiying.fitmanager.core.LeLog;
import com.beiying.fitmanager.core.LePrimitiveType;
import com.beiying.fitmanager.core.data.LeFileHelper;
import com.beiying.fitmanager.core.data.LeSharedPrefUnit;

class LeWeblitePersistent {
	private static final String PREF_NAME = "weblite_pref";
	
	/*
	 * Cache标志：0代表Cache不可用；1代表Cache可用
	 */
	public static final int CACHE_FLAG_NOUSE = 0;
	public static final int CACHE_FLAG_USABLE = 1;

	private int mCacheFlag;

	private String mCacheFile;

	private Context mContext;
	
	private LeSharedPrefUnit mLastModifiedPair = new LeSharedPrefUnit(LePrimitiveType.STRING, "_last_modified", "0");
	private LeSharedPrefUnit mContentLengthPair = new LeSharedPrefUnit(LePrimitiveType.INTEGER, "_content_length", 0);
	private LeSharedPrefUnit mVersionPair = new LeSharedPrefUnit(LePrimitiveType.INTEGER, "_version", 0);
	private LeSharedPrefUnit mUrlPair = new LeSharedPrefUnit(LePrimitiveType.STRING, "_url", null);
	private LeSharedPrefUnit mUpdateTimePair = new LeSharedPrefUnit(LePrimitiveType.LONG, "_update_time", 0l);
	private LeSharedPrefUnit mUpdateIntervalPair = new LeSharedPrefUnit(LePrimitiveType.LONG, "_update_interval", 0l);
	private LeSharedPrefUnit mCacheFlagPair = new LeSharedPrefUnit(LePrimitiveType.INTEGER, "_cache_flag", CACHE_FLAG_NOUSE);

	public LeWeblitePersistent(Context context, String cacheFile, String domain) {
		mContext = context;
		mCacheFile = cacheFile;
	}

	public boolean isCacheUsable() {
		if (mCacheFlag == CACHE_FLAG_USABLE) {
			return true;
		}
		return false;
	}

	public void onClearCache() {
		saveCacheFlag(CACHE_FLAG_NOUSE);
	}

	public int getCacheFlag() {
		return mCacheFlagPair.getInt();
	}

	public void saveCacheFlag(int cacheFlag) {
		mCacheFlagPair.setValue(cacheFlag);
	}

	public String getUrl() {
		return mUrlPair.getString();
	}

	public void saveUrl(String url) {
		mUrlPair.setValue(url);
	}

	public int getVersion() {
		return mVersionPair.getInt();
	}

	public void saveVersion(int version) {
		mVersionPair.setValue(version);
	}

	public String getLastModifiedFromPreference() {
		return mLastModifiedPair.getString();
	}

	public void saveLastModified(String lastModified) {
		mLastModifiedPair.setValue(lastModified);
	}

	public void saveContentLength(int length) {
		mContentLengthPair.setValue(length);
	}

	public int getContentLength() {
		return mContentLengthPair.getInt();
	}

	public void saveUpdateTime(long updateTime) {
		mUpdateTimePair.setValue(updateTime);
	}

	public long getUpdateTime() {
		return mUpdateTimePair.getLong();
	}

	public void saveUpdateInterval(long updateInterval) {
		mUpdateIntervalPair.setValue(updateInterval);
	}

	public long getUpdateInterval() {
		return mUpdateIntervalPair.getLong();
	}

	public boolean isCorrectCache() {
		if (mCacheFlag == CACHE_FLAG_USABLE) {
			byte[] data = readCacheFile();
			return checkLength(data);
		}
		return false;
	}

	private boolean checkLength(byte[] data) {
		if (data == null || data.length == 0) {
			return false;
		}
		/*
		 * int contentLen = getContentLength(); if (aData.length != contentLen
		 * && contentLen != 0) { return false; }
		 */
		return true;
	}

	public void saveCache(byte[] data) {
		if (LeFileHelper.saveFile(data, mCacheFile)) {
			mCacheFlag = CACHE_FLAG_USABLE;
			saveCacheFlag(mCacheFlag);
			
			LeLog.i("cw weblite save html");
		}
	}

	public byte[] readCacheFile() {
		mCacheFlag = getCacheFlag();
		if (mCacheFlag == CACHE_FLAG_USABLE) {
			return LeFileHelper.readFile(mCacheFile);
		}
		return null;
	}

}
