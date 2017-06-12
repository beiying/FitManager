package com.beiying.fitmanager.core.weblite;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

class LeWebliteModel {
	
	public static final String ROOT_DIR_NAME = "weblite";
	
	private static final String DEFAULT_HTML = "index.html";
	
	private static final String AUTO_ASSERT_PREFIX = "assert_";
	
	static final String CACHE_FILE_SCHEME = "file://";
	static final String ASSERT_FILE_PREFIX = "file:///android_asset/";
	
	String mAssertRootDir;
	String mHostRootDir;
	String mPath;
	
	String mAssertFile;
	String mCacheFile;
	String mRemoteUrl;
	boolean mOurServer;
	String mBaseUri;
	int mVersion;
	boolean mUpdateFlag = false;

	public LeWebliteModel(String assertFile, String cacheRootPath, String remoteUrl, boolean ourServer) {
		mAssertFile = assertFile;
		mRemoteUrl = remoteUrl;
		mOurServer = ourServer;
		
		parseUrl(cacheRootPath, remoteUrl);
	}
	
	private void parseUrl(String cacheRootPath, String remoteUrl) {
		mBaseUri = getBasePath(remoteUrl);
		try {
			URI uri = new URI(remoteUrl);
			String host = uri.getHost();
			String path = uri.getPath();
			if (mAssertFile != null && !mAssertFile.endsWith(path)) {
				throw new LeWebliteException("Assert file must has same dir structure as url!");
			}
			if (mAssertFile != null) {
				mAssertRootDir = mAssertFile.replace(path, "");
			}
			mHostRootDir = cacheRootPath + File.separator + host;
			if (path.equals("") || path.endsWith("/")) {
				path += DEFAULT_HTML;
			}
			mPath = path;
			mCacheFile = mHostRootDir + mPath;
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	public String getAutoAssertFile() {
		return getCacheDir() + AUTO_ASSERT_PREFIX + getFilename(mCacheFile);
	}
	
	String getCacheDir() {
		return getBasePath(mCacheFile);
	}
	
	public String getCacheFileDir() {
		return getBasePath(getCacheFileWithScheme());
	}
	
	public String getAssertFileDir() {
		return getBasePath(getAssertFileWithScheme());
	}
	
	public String getCacheFileWithScheme() {
		return CACHE_FILE_SCHEME + mCacheFile;
	}
	
	public String getAssertFileWithScheme() {
		return ASSERT_FILE_PREFIX + mAssertFile;
	}
	
	public String getCacheRootDirWithScheme() {
		return CACHE_FILE_SCHEME + mHostRootDir;
	}
	
	public String getAssertRootDirWithScheme() {
		return ASSERT_FILE_PREFIX + mAssertRootDir;
	}
	
	public static String getBasePath(String uri) {
		if (uri == null) {
			return null;
		}
		String basePath = "";
		int index = uri.lastIndexOf("/");
		if (index != -1) {
			basePath = uri.substring(0, index + 1);
		}
		return basePath;
	}
	
	public static String getFilename(String uri) {
		if (uri == null) {
			return null;
		}
		String filename = "";
		int index = uri.lastIndexOf("/");
		if (index != -1) {
			filename = uri.substring(index + 1, uri.length());
		}
		return filename;
	}
}
