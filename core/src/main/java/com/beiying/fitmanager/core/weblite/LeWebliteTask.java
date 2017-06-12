package com.beiying.fitmanager.core.weblite;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;

import android.content.Context;

import com.beiying.fitmanager.core.LeLog;
import com.beiying.fitmanager.core.data.LeFileHelper;
import com.beiying.fitmanager.core.net.LeNetTask;
import com.beiying.fitmanager.core.weblite.LeWebliteNet.LeWebliteNetListener;

class LeWebliteTask implements LeWebliteNetListener {

	private static final String MIMETYPE = "text/html";
	private static final String ENCODING = "utf-8";

	String mRawContent;
	String mCacheContent;

	private boolean mHasLoadCorrect = false;
	private boolean mLoadFromRaw = false;
	private boolean mLoadFromCache = false;
	private boolean mLoadFromServer = false;
	//private boolean mHasLoaded;
	private boolean mRunningTask = false;

	private LeWeblitePersistent mPersistent;

	private Context mContext;

	private LeWeblite mWeblite;
	private LeWebliteModel mModel;

	private LeExpireTime mExpireTime;

	//private LeHtmlParser mHtmlParser;
	
	public LeWebliteTask(Context context, String assertFile, String cacheRoot, String remoteUrl, boolean ourServer,
			LeExpireTime expireTime) {
		mContext = context;

		if (cacheRoot == null) {
			cacheRoot = mContext.getFilesDir().getAbsolutePath() + File.separator + "data" + File.separator
					+ LeWebliteModel.ROOT_DIR_NAME;
		}
		if (cacheRoot.endsWith(File.separator)) {
			cacheRoot = cacheRoot.substring(0, cacheRoot.length() - 1);
		}
		mModel = new LeWebliteModel(assertFile, cacheRoot, remoteUrl, ourServer);

		mPersistent = new LeWeblitePersistent(mContext, mModel.mCacheFile, mModel.mBaseUri);
		mRunningTask = false;

		if (expireTime == null) {
			expireTime = LeExpireTime.createNeverExpire();
		}
		mExpireTime = expireTime;

		loadLocalResources();

		//mHtmlParser = new LeHtmlParser(context);
	}
	
	public boolean checkFileUri(String uri) {
		if (uri != null) {
			if (uri.startsWith(mModel.getAssertFileDir()) || uri.startsWith(mModel.getCacheFileDir())) {
				File file = new File(uri);
				if (!file.exists()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public String adjustToRemoteUrl(String localUrl) {
		String remote = null;
			if (localUrl.startsWith(mModel.getAssertFileDir()) || localUrl.startsWith(mModel.getCacheFileDir())) {
			int index = mModel.mRemoteUrl.lastIndexOf("/");
			if (index != -1) {
				remote = mModel.mRemoteUrl.substring(0, index + 1);
			}
			if (localUrl.startsWith(mModel.getAssertFileDir())) {
				remote = localUrl.replace(mModel.getAssertFileDir(), remote);
			} else if (localUrl.startsWith(mModel.getCacheFileDir())) {
				remote = localUrl.replace(mModel.getCacheFileDir(), remote);
			}
		}
		return remote;
	}

	public boolean isCacheUsable() {
		return mPersistent.isCacheUsable();
	}

	public boolean isLoadFromServer() {
		return mLoadFromServer;
	}

	public boolean isLoadFromCache() {
		return mLoadFromCache;
	}

	public boolean isExpired() {
		long currentTime = System.currentTimeMillis();
		long updateTime = mPersistent.getUpdateTime();
		/*
		 * Date currentDate = new Date(currentTime); Date updateDate = new
		 * Date(updateTime); LeLog.i("currentDate:" + currentDate);
		 * LeLog.i("updateDate:" + updateDate);
		 */
		if (currentTime > updateTime) {
			return true;
		}
		return false;
	}

	public void onClearCache() {
		mPersistent.onClearCache();
	}

	private void loadLocalResources() {
		String savedUrl = mPersistent.getUrl();
		if (savedUrl != null) {
			mModel.mRemoteUrl = savedUrl;

			int version = mPersistent.getVersion();
			mModel.mVersion = version;
		}
	}

	public void onReceiveVersion(int version) {
		if (version > mModel.mVersion) {
			mModel.mVersion = version;
			mModel.mUpdateFlag = true;
		}
	}

	public boolean shouldUpdate() {
		return mModel.mUpdateFlag;
	}

	public void updateUrl(String url) {
		if (mModel != null) {
			mModel.mRemoteUrl = url;
			mPersistent.saveUrl(url);

			int version = mModel.mVersion;
			mPersistent.saveVersion(version);

		}
	}

	public void load(LeWeblite weblite) {
		if (mModel != null && mModel.mAssertFile != null) {
			mRawContent = LeFileHelper.getFromAsset(mContext, mModel.mAssertFile);
		}
		readCacheContent();
		
		mWeblite = weblite;
	}
	
	private void readCacheContent() {
		byte[] data = mPersistent.readCacheFile();
		if (data != null) {
			try {

				mCacheContent = new String(data, ENCODING);

			} catch (UnsupportedEncodingException e) {
			}
		}
	}

	public void forceLoadLocal() {
		if (mRunningTask) {
			return;
		}
		mRunningTask = true;

		if (mPersistent.isCorrectCache()) {
			loadFromCache();

		} else { //如果缓存不正确，加载静态资源
			if (!mLoadFromRaw) {
				loadFromRaw();
			}
		}
	}

	public void loadLocal() {
		if (mRunningTask) {
			return;
		}
		mRunningTask = true;
		//long expiretime=mPersistent.getUpdateTime();
		if (!mHasLoadCorrect) { //如果没有正确载入，则先读取本地数据，再联网更新
			if (mPersistent.isCorrectCache()) {
				loadFromCache();

			} else { //如果缓存不正确，加载静态资源
				if (!mLoadFromRaw) {
					loadFromRaw();
				}
			}
		}
	}

	protected void loadFromRaw() {
		if (mModel != null && mWeblite != null) {
			if (mRawContent == null && mModel.mAssertFile != null) {
				mRawContent = LeFileHelper.getFromAsset(mContext, mModel.mAssertFile);
			}
			String content = mRawContent;

			LeLog.i("ELF loadFromRaw:" + mModel.mAssertFile);
			mLoadFromRaw = true;

			String baseUrl = mModel.getAssertFileWithScheme();
			mWeblite.onLoadLocalSuccess(baseUrl, content, "text/html",
					ENCODING, baseUrl);

			mRawContent = null;
		}
	}

	protected void loadFromCache() {
		if (mModel != null && mWeblite != null) {

			if (mCacheContent == null) {
				readCacheContent();
			}

			mLoadFromRaw = false;

			LeLog.i("ELF loadFromCache:" + mModel.mCacheFile);

			mLoadFromCache = true;

			String baseUrl = mModel.getCacheFileWithScheme();
			//String baseUrl = mModel.mBaseUri;
			mWeblite.onLoadLocalSuccess(baseUrl, mCacheContent, MIMETYPE, ENCODING, baseUrl);

			mCacheContent = null;
		}
	}

	protected boolean loadFromServerOnExpire(boolean checkExpired) {
		return loadFromServer(true, checkExpired);
	}

	private boolean loadFromServer(boolean updateFlag, boolean checkExpired) {
		return loadFromServer(mModel.mRemoteUrl, updateFlag, checkExpired);
	}

	private boolean loadFromServer(String aUrl, boolean aUpdateFlag, boolean aCheckExpired) {
		if (!aCheckExpired || isExpired()) {
			if (mModel != null) {
				LeWebliteNet webpageNet;
				if (this.mWeblite.mToken != null) {
					//需要验证cookie
					webpageNet = new LeWebliteNet(this.mWeblite.mToken, mExpireTime);
				} else {
					//不需要验证cookie
					webpageNet = new LeWebliteNet(mExpireTime);
				}
				webpageNet.setListener(this);
				webpageNet.start(aUrl, mModel.mOurServer, mPersistent.getLastModifiedFromPreference(),
						mPersistent.isCacheUsable(), aUpdateFlag);
				return true;
			}
		}
		return false;
	}

	@Override
	public void onRequestFail() {
		/*
		 * if (!mHasLoadCorrect && !mLoadFromRaw && mModel.mAssertFile != null)
		 * { loadFromRaw(); }
		 */
		mRunningTask = false;

		mWeblite.onLoadFromServerFail();
		LeLog.i("ELF loadFromServerFail");
	}

	@Override
	public void onReceiveSuccess(ByteArrayOutputStream baos) {
		String content = null;
		if (baos != null) {
			byte[] bytes = baos.toByteArray();
			try {
				content = new String(bytes, "utf-8");
			} catch (UnsupportedEncodingException e) {
	
			}
	
			if (content != null) {
				//postFetchResources(content, mModel.mBaseUri, mModel.mHostRootDir);
			}
		}
		onProgressCompleted();
	}

//	private void postFetchResources(final String htmlContent, final String baseUri, final String hostRootDir) {
//		new AsyncTask<String, Integer, String>() {
//
//			@Override
//			protected String doInBackground(String... params) {
//				mHtmlParser.setListener(new LeParserListener() {
//					
//					@Override
//					public void onSucess(String newHtml) {
//						try {
//							byte[] bytes = newHtml.getBytes("utf-8");
//							
//							mPersistent.saveContentLength(bytes.length);
//
//							mPersistent.saveCache(bytes);
//
//							mWeblite.onLoadFromServerSuccess(mModel.mBaseUri, newHtml, MIMETYPE, ENCODING, null);
//						} catch (UnsupportedEncodingException e) {
//							
//						}
//						if (LeWeblite.sAutoGenAssertFile) {
//							genAssertFile(htmlContent, baseUri, hostRootDir);
//						}
//					}
//					
//					@Override
//					public void onFail() {
//						
//					}
//				});
//				mHtmlParser.parse(htmlContent, baseUri,
//						Uri.parse(mModel.getCacheRootDirWithScheme()), true);
//				return null;
//			}
//		}.execute();
//	}
//
//	private void genAssertFile(final String htmlContent, final String baseUri, final String hostRootDir) {
//		String adjustContent = mHtmlParser.parse(htmlContent, baseUri,
//				Uri.parse(mModel.getAssertRootDirWithScheme()), false);
//		try {
//			LeFileHelper.saveFile(adjustContent.getBytes("utf-8"), mModel.getAutoAssertFile());
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//	}

	@Override
	public void onReceiveHeadSuccess(String lastModified, long expireTime, long expireInterval) {
		mPersistent.saveLastModified(lastModified);
		//		String expire = thi.getUrlConn().getHeaderField("Expiration-time");
		mPersistent.saveUpdateTime(expireTime);
		mPersistent.saveUpdateInterval(expireInterval);
	}

	public void updateUpdateTime() {
		long now = System.currentTimeMillis();
		long interval = mPersistent.getUpdateInterval();
		long updateTime = now + interval;


		mPersistent.saveUpdateTime(updateTime);
	}

	@Override
	public void setupLeNetTask(LeNetTask netTask) {
		mWeblite.setupNetTask(netTask);
	}

	public void onProgressCompleted() {
		mRunningTask = false;
	}

}
