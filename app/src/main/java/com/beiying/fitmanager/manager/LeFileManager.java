package com.beiying.fitmanager.manager;

import android.content.Context;
import android.os.Environment;

import com.beiying.fitmanager.core.utils.LeAndroidUtils;

import java.io.File;

public class LeFileManager {

	//Dir
	public static final String DIR_DATA = "/data";
	public static final String DIR_IMAGES = "/images";
	public static final String DIR_SDCARD = "/Android/data/com.lenovo.browser";
	public static final String DIR_DOWNLOAD_DIR = "/Download";
	public static final String DIR_PLUGIN = "/plugin";
	public static final String DIR_CA = "/CA";

	public static final String ASSERT_PATH = "file:///android_asset";

	//Member Variable
	private static String sDirFiles;
	private static String sDirSd;
	private static String sDirCache;

	//File
	public static final String FILE_BLOCK_AD = "ad_block.dat";
	public static final String FILE_HOME_NAVI = "home_navi.dat";
	public static final String FILE_NAV_AD = "nav_ad.dat";
	public static final String FILE_NAV_NEWS = "nav_news.dat";
	public static final String FILE_NAV_HOT_WORDS = "nav_hotwords.dat";
	public static final String FILE_HOT_SITES = "hot_sites.dat";
	public static final String FILE_NAV_UNIT = "nav_unit.dat";
	public static final String FILE_NAV_CARD_LIST = "nav_card_list.dat";
	public static final String FILE_RSS_CHANNEL = "rss_channel.dat";
	public static final String FILE_RSS_CHANNEL_DATA = "rss_channel_data.dat";
	public static final String FILE_HOME_WEATHER = "home_weather.dat";
	public static final String FILE_LOCATION = "loc.dat";
	public static final String FILE_URL_ISSUE = "url_issue.dat";
	public static final String FILE_STR_ISSUE = "str_issue.dat";
	public static final String FILE_URL_RECOMMEND = "url_recommend.dat";
	public static final String FILE_ADDRESS_SEARCH_ENGINE = "address_search_engine.dat";
	public static final String FILE_CATEGORY_SEARCH_ENGINE = "category_search_engine.dat";
	public static final String FILE_NEXTAGENT = "nextagent.dat";
	public static final String FILE_READMODE = "readmode.dat";
	public static final String FILE_PLUGIN = "plugin.dat";
	public static final String FILE_PRECISE_SEARCH = "precise_search.dat";
	private static final String FILE_AMS_REG = "ams_reg.dat";
	private static final String FILE_AMS_UPDATE = "ams_update.dat";
	private static final String FILE_WELCOME_PAGE = "welcomepage.dat";
	public static final String FILE_FIREWORKS = "fireworks.dat";
	private static final String FILE_FIREWORK_WORDS = "firework_words.dat";
	public static final String FILE_USERINFO = "userinfo.dat";
	public static final String FILE_WEIBO_USER_INFO = "weibo_user_info.data";

	private LeFileManager(){}

	public static void init(Context appContext) {
		File fileDir = appContext.getFilesDir();
		File cacheDir = appContext.getCacheDir();
		if (fileDir != null) {
			sDirFiles = fileDir.getAbsolutePath();
			
			File file = new File(sDirFiles + DIR_IMAGES);
			file.mkdir();
			file = new File(sDirFiles + DIR_DATA);
			file.mkdir();
		}
		if (cacheDir != null) {
			sDirCache = cacheDir.getAbsolutePath();
		}
	}

	public static String getDownloadPath() {
		return makeDownloadDir();
	}

	private static String makeDownloadDir() {
		String downloadPath = getDirSd() + DIR_DOWNLOAD_DIR;
		File file = new File(downloadPath);
		if (file.exists() && file.isDirectory()) {
			return downloadPath;
		}
		file.mkdir();
		return downloadPath;
	}

	public static boolean isFileExist(String filename) {
		File file = new File(filename);
		return file.exists();
	}

	public static void createDir(String filename) {
		File file = new File(filename);
		file.mkdirs();
	}

	public static String getDirFiles() {
		return sDirFiles;
	}

	public static String getDirCache() {
		return sDirCache;
	}

	public static String getDirSd() {
		if (sDirSd == null) {
			sDirSd = LeAndroidUtils.getExternalStorageAbsolutePath(null);
			File file = new File(sDirSd + DIR_SDCARD);
			file.mkdirs();
		}
		if (sDirSd == null) {
			sDirSd = Environment.getExternalStorageDirectory().getAbsolutePath();
			File file = new File(sDirSd + DIR_SDCARD);
			file.mkdirs();
		}
		return sDirSd;
	}

	public static String getSdAppRoot() {
		return getDirSd() + DIR_SDCARD;
	}

	public static String getDirImages() {
		return getDirFiles() + DIR_IMAGES;
	}

	public static String getDirData() {
		return getDirFiles() + DIR_DATA;
	}

	/** 软件在SD中所创建的目录 */
	public static String getAppSDRootDir() {
		return getDirSd() + DIR_SDCARD;
	}
	
	public static String getFileAmsUpdate() {
		return getDirData() + "/" + FILE_AMS_UPDATE;
	}
	
	public static String getFileAmsReg() {
		return getDirData() + "/" + FILE_AMS_REG;
	}

	public static String getFileHomeNavi() {
		return getDirData() + "/" + FILE_HOME_NAVI;
	}
	
	public static String getFileNavAd() {
		return getDirData() + "/" + FILE_NAV_AD;
	}
	
	public static String getFileNavNews() {
		return getDirData() + "/" + FILE_NAV_NEWS;
	}
	
	public static String getFileNavHotWords() {
		return getDirData() + "/" + FILE_NAV_HOT_WORDS;
	}
	
	public static String getFileHotSites() {
		return getDirData() + "/" + FILE_HOT_SITES;
	}
	
	public static String getFileNavUnit() {
		return getDirData() + "/" + FILE_NAV_UNIT;
	}
	
	public static String getFileNavCardList() {
		return getDirData() + "/" + FILE_NAV_CARD_LIST;
	}
	
	public static String getFileAdBlock() {
		return getDirData() + "/" + FILE_BLOCK_AD;
	}
	
	public static String getFileRssChannel() {
		return getDirData() + "/" + FILE_RSS_CHANNEL;
	}

	public static String getFileRssChannelData() {
		return getDirData() + "/" + FILE_RSS_CHANNEL_DATA;
	}

	public static String getFileLocation() {
		return getDirData() + "/" + FILE_LOCATION;
	}

	public static String getFileHomeWeather() {
		return getDirData() + "/" + FILE_HOME_WEATHER;
	}

	public static String getFileUrlIssue() {
		return getDirData() + "/" + FILE_URL_ISSUE;
	}
	
	public static String getFileUrlRecommend() {
		return getDirData() + "/" + FILE_URL_RECOMMEND;
	}
	
	public static String getFileAddressSearchEngine() {
		return getDirData() + "/" + FILE_ADDRESS_SEARCH_ENGINE;
	}
	
	public static String getFileCategorySearchEngine() {
		return getDirData() + "/" + FILE_CATEGORY_SEARCH_ENGINE;
	}

	public static String getFileNextAgent() {
		return getDirData() + "/" + FILE_NEXTAGENT;
	}
	
	public static String getFileReadMode() {
		return getDirData() + "/" + FILE_READMODE;
	}
	
	public static String getFilePlugin() {
		return getDirData() + File.separator + FILE_PLUGIN;
	}
	
	public static String getPluginFullPath(String file) {
		return getDirData() + DIR_PLUGIN  + File.separator + file;
	}

	public static String getFilePreciseSearch() {
		return getDirData() + "/" + FILE_PRECISE_SEARCH;
	}
	
	public static String getAssertFile(String file) {
		return ASSERT_PATH + File.separator + file;
	}

	public static String getWelcomePagePath() {
		return getDirData() + File.separator + FILE_WELCOME_PAGE;
	}


	public static String getFireworksFile() {
		return getDirData() + "/" + FILE_FIREWORKS;
	}
	
	public static String getFileFireworkWords() {
		return getDirData() + "/" + FILE_FIREWORK_WORDS;
	}

	public static String getFileUserInfo() {
		return getDirData() + "/" + FILE_USERINFO;
	}

	public static String getCaFile() {
		return getDirFiles() + DIR_CA;
	}

	public static String getFileWeiboUserInfo() {
		return getDirData() + "/" + FILE_WEIBO_USER_INFO;
	}
}
