package com.beiying.fitmanager.core.weblite;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.text.format.DateUtils;

import com.beiying.fitmanager.core.LeLog;
import com.beiying.fitmanager.core.net.INetConnListener;
import com.beiying.fitmanager.core.net.LeNet;
import com.beiying.fitmanager.core.net.LeNetManager;
import com.beiying.fitmanager.core.net.LeNetTask;
import com.beiying.fitmanager.core.net.LeHttpNet.LeUrlProcessor;

class LeWebliteNet implements INetConnListener {
	private static final int CONN_TIMEOUT_SECONDS = 5;
	private static final int READ_TIMEOUT_SECONDS = 15;

	private LeNetTask mTask;

	private boolean mIsRightData;

	private String mLastModified;

	private ByteArrayOutputStream mBaos;
	private DataOutputStream mDos;

	private LeWebliteNetListener mNetListener;

	private String mToken;
	private boolean mIsNeedCheckHead;

	private LeExpireTime mExpireTime;
	
	protected static LeUrlProcessor sUrlProcessor;

	LeWebliteNet(LeExpireTime expireTime) {
		mToken = null;
		mIsNeedCheckHead = false;
		mExpireTime = expireTime;
	}

	LeWebliteNet(String token, LeExpireTime expireTime) {
		mToken = token;
		mIsNeedCheckHead = true;
		mExpireTime = expireTime;
	}

	public void setListener(LeWebliteNetListener listener) {
		mNetListener = listener;
	}

	public void start(String url, boolean ourServer, String lastModified, boolean cacheUsable, boolean updateFlag) {
		if (ourServer && sUrlProcessor != null) {
			url = sUrlProcessor.process(url);
		}
		
		mTask = new LeNetTask();
		mTask.setListener(this);
		mTask.setM_url(url);
		mTask.setNetMode(LeNet.GET);
		mTask.setConnTimeOut((int) (DateUtils.SECOND_IN_MILLIS * CONN_TIMEOUT_SECONDS));
		mTask.setReadTimeOut((int) (DateUtils.SECOND_IN_MILLIS * READ_TIMEOUT_SECONDS));

		mLastModified = lastModified;
		/*
		 * Date date = new Date(0); mLastModified = date.toGMTString();
		 */
		if (mLastModified != null && cacheUsable && !updateFlag) {
			mTask.addHttpHeads("if-modified-since", mLastModified);
		}

		mNetListener.setupLeNetTask(mTask);

		LeNetManager.getInstance().getNet().startTask(mTask);
		mIsRightData = true;

		LeLog.i(url);
	}

	@Override
	public void onConnStart(LeNetTask task) {
		// TODO Auto-generated method stub
		try {
			mBaos = new ByteArrayOutputStream();
			mDos = new DataOutputStream(mBaos);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onReceiveHead(LeNetTask task) {
		checkHeadCookie(task);

		if (mIsRightData) {

			String newLastModified = task.getUrlConn().getHeaderField("Last-Modified");
			if (mLastModified == null || (newLastModified != null && !mLastModified.equals(newLastModified))) {
				mLastModified = newLastModified;
			}

			String expire = task.getUrlConn().getHeaderField("Expires");
			long expireInMillis = System.currentTimeMillis() + mExpireTime.mDefault;
			if (expire != null) {
				expireInMillis = parseExpires(expire);
			} else {
				expire = task.getUrlConn().getHeaderField("Expiration-time");
				if (expire != null) {
					expireInMillis = parseExpirationtime(expire);
				}
			}
			long expireInterval = expireInMillis - System.currentTimeMillis();
			mNetListener.onReceiveHeadSuccess(mLastModified, expireInMillis, expireInterval);

		} else {
			mTask.stopConn();
			mNetListener.onRequestFail();
		}
	}

	private long parseExpires(String expire) {
		return LeExpireTime.parseExpires(expire, mExpireTime.mDefault, mExpireTime.mMin,
				mExpireTime.mMax);
	}

	private long parseExpirationtime(String expire) {
		long expireMs = mExpireTime.mDefault;
		try {
			long revExpire = Long.parseLong(expire) * 1000;
			if (mExpireTime.mMin <= revExpire && revExpire <= mExpireTime.mMax) {
				expireMs = revExpire;
			}
		} catch (NumberFormatException e) {

		}
		return System.currentTimeMillis() + expireMs;
	}

	@Override
	public void onResponseCode(LeNetTask task, int resCode) {
		if (resCode == 200) {
			setIsRightData(true);
		} else {
			setIsRightData(false);
		}
	}

	@Override
	public void onReceiveData(LeNetTask task, byte[] data, int len) {
		// TODO Auto-generated method stub
		if (mTask.equals(task) && mIsRightData) {
			try {
				mDos.write(data, 0, len);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onDisConnect(LeNetTask task) {
		// TODO Auto-generated method stub
		if (mIsRightData) {

			long remoteLength = getHeaderLength(task);
			LeLog.i("ELF remoteLength:" + remoteLength);
			LeLog.i("ELF mBaos.size():" + mBaos.size());
			LeLog.i("ELF byte:" + mBaos.toByteArray().length);
			if (mBaos.size() > 0) { // 如果数据下载完整，则加载内容
				try {

					mNetListener.onReceiveSuccess(mBaos);

					closeStreams();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				mNetListener.onRequestFail();
			}
		} else {
			mNetListener.onRequestFail();
		}

	}

	@Override
	public void onThrowException(LeNetTask task) {
		setIsRightData(false);
	}

	private void setIsRightData(boolean isRight) {
		if (mIsRightData) {
			mIsRightData = isRight;
		}
	}

	private void closeStreams() {
		try {
			mDos.close();
			mBaos.close();
			mDos = null;
			mBaos = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private long getHeaderLength(LeNetTask task) {
		long remoteLength = 0L;
		HttpURLConnection con = task.getUrlConn();
		if (con == null) {
			LeLog.w("HttpURLConnection is null");
			return remoteLength;
		}
		//String lengthField = con.getHeaderField("Content-Length");
		//Map<String, List<String>> heads = con.getHeaderFields();
		String lengthField = con.getHeaderField("File-Length");
		try {
			remoteLength = Long.valueOf(lengthField);
		} catch (NumberFormatException e) {
			LeLog.w("get remote content length error.", e);
		}
		return remoteLength;
	}

	private void checkHeadCookie(LeNetTask task) {
		if (mIsNeedCheckHead) {
			Map<String, List<String>> heads = task.getUrlConn().getHeaderFields();
			Set<Entry<String, List<String>>> set = heads.entrySet();
			for (Entry<String, List<String>> entry : set) {
				String headKey = entry.getKey();
				List<String> headValues = entry.getValue();
				if (headKey != null && headKey.toLowerCase().contains("cookie")) {
					for (String headValue : headValues) {
						LeLog.v("Cookie value=" + headValue);
						if (headValue != null) {
							String[] values = headValue.split(";");
							for (String value : values) {
								if (value.equalsIgnoreCase(mToken)) {
									setIsRightData(true);
									return;
								}
							}
						}
					}
				}
			}
			setIsRightData(false);
		} else {
			setIsRightData(true);
		}
	}

	interface LeWebliteNetListener  {

		void onRequestFail();

		void onReceiveSuccess(ByteArrayOutputStream baos);

		void onReceiveHeadSuccess(String lastModified, long expireTime, long expireInterval);

		void setupLeNetTask(LeNetTask netTask);
	}

}
