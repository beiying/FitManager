/** 
 * Filename:    LeMachineProperty.java
 * Description:  
 * Copyright:   Lenovo PCL Copyright(c)2013 
 * @author:     chenwei27 
 * @version:    1.0
 * Create at:   2013-7-8 上午10:55:13
 * 
 * Modification History: 
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------ 
 * 2013-7-8     chenwei27    1.0         1.0 Version 
 */
package com.beiying.fitmanager.core.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.beiying.fitmanager.core.ContextContainer;
import com.beiying.fitmanager.core.LeLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class LeMachineHelper extends ContextContainer {
	
	private static final String PATH_CPU = "/sys/devices/system/cpu/";
	
	private static final String PATH_CPUINFO_MAX_FREQ = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq";

	private static final String UNIQUE_ID = "unique_id";

	public static final String FIREWORK_UNIQUE_ID = "firework_unique_id";

	public static final int RECYCLE_MAX_VERSION = 10;

	private static final String CPU_REGEX = "cpu[0-9]";
	
	private static final int TYPE_UNINITIALIZED = 0;
	private static final int TYPE_PHONE = 1;
	private static final int TYPE_PAD = 2;

	private static int sMachineType = TYPE_UNINITIALIZED;
	
	private static Boolean sHighSpeedMachine = null;

	private static String imei = null;

	private LeMachineHelper() {
	}

	public static void init(Context mContext) {
		sMachineType = TYPE_PHONE;
	}


	public static boolean isMachineAPad() {
		//return sMachineType == TYPE_PAD;
		return false;
	}

	public static boolean isShowTab() {
		return true;
	}

	
	public static boolean kitkatWebView() {
		if (getSDKVersionInt() > 18) {
			return true;
		}
		return false;
	}
	
	/**
	 * 获取IMEI号
	 * @return IMEI
	 */
	public static String getImei() {
		TelephonyManager tm = (TelephonyManager) sContext.getSystemService(Context.TELEPHONY_SERVICE);
		if (tm != null) {
			imei = tm.getDeviceId();
		}

		return imei;
	}
	
	public static String getAndroidId() {
		return android.provider.Settings.Secure.getString(sContext.getContentResolver(), 
				android.provider.Settings.Secure.ANDROID_ID);
	}

    public static String getIPAddress() {
        String ip = "";
        WifiManager wifiMgr = (WifiManager)sContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
        if (null != info) {
            int ipInt = info.getIpAddress();
            ip = (ipInt & 0xFF) + "." +
                 ((ipInt >> 8) & 0xFF) + "." +
                 ((ipInt >> 16) & 0xFF) + "." +
                 (ipInt >> 24 & 0xFF);
        }
        return ip;
    }
	
	public static String getMacAddress() {
		String macAddress = "";
		WifiManager wifiMgr = (WifiManager)sContext.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
		if (null != info) {
		    macAddress = info.getMacAddress();
		}
		return macAddress;
	}

    public static String getRouterMacAddress() {
        String macAddress = "";
        WifiManager wifiMgr = (WifiManager)sContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
        if (null != info) {
            macAddress = info.getBSSID();
        }
        return macAddress;
    }

    public static String getRouterSSID() {
        String ssid = "";
        WifiManager wifiMgr = (WifiManager)sContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
        if (null != info) {
            ssid = info.getSSID();
        }
        return ssid;
    }
	
	public static String getIMSI() {
		String imsi = "";
		TelephonyManager tm = (TelephonyManager) sContext.getSystemService(Context.TELEPHONY_SERVICE);
		if (tm != null) {
			imsi = tm.getSubscriberId();
		}
		return imsi;
	}
	
	public static String getNetworkType() {
		String typeName = "";
		ConnectivityManager manager = BYAndroidUtils.getConnectivityManager(sContext);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			typeName = networkInfo.getTypeName();
		}
		return typeName;
	}
	
	public static TreeMap<String, Integer> getScreenParameters() {
		TreeMap<String, Integer> map = new TreeMap<String, Integer>();
		DisplayMetrics metric = new DisplayMetrics();
        sActivity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        int height = metric.heightPixels;   // 屏幕高度（像素）
        int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）
        map.put("width", width);
        map.put("height", height);
        map.put("densityDpi", densityDpi);
        return map;
	}
	
	/**
	 * 是否为高性能机器
	 * 
	 */
	public static boolean isHighSpeedPhone() {
		if (sHighSpeedMachine == null) {
			sHighSpeedMachine = checkHighSpeed();
		}
		return sHighSpeedMachine;
	}
	
	private static boolean checkHighSpeed() {
		long cpufreq = fetchCpuMaxFreq() * getCpuCoresNum();
		cpufreq = cpufreq / 1000;
		boolean hasGPU = true;

		LeLog.d("GFS info: " + cpufreq + " " + hasGPU);
		// boolean hasGPU = true;
		long allmem;
		allmem = LeMemUtil.getSysMemoryInfo().get("MemTotal:");
		allmem = allmem / 1000;
		if (cpufreq >= 1433 && allmem >= 1024 && hasGPU) {
			return true;
		} else {
			return false;
		}
	}
	
	public static long fetchCpuMaxFreq() {
		String max = readSysInfo(PATH_CPUINFO_MAX_FREQ);
		long freq = 0;
		try {
			if (max != null && max.length() > 0 && max.charAt(max.length() - 1) == '\n') {
				max = max.substring(0, max.length() - 1);
			}
			freq = Long.parseLong(max);
		} catch (NumberFormatException e) {
			LeLog.e(e);
			freq = 0;
		}
		return freq;
	}
	
	/**
	 * 读取系统配置文件来获取信息
	 * 
	 * @param fileString
	 * @return 文件内容
	 */
	private static String readSysInfo(String fileString) {
		StringBuilder sb = new StringBuilder();
		File cpuinfo = new File(fileString);
		if (cpuinfo.exists()) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(cpuinfo));
				String aLine;
				while ((aLine = br.readLine()) != null) {
					sb.append(aLine);
                    sb.append('\n');
				}
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				LeLog.e(e);
			}
		}
		return sb.toString();
	}
	
	private static int getCpuCoresNum() {
		return getCpuCoresNumInner(PATH_CPU);
	}
	
	/**
	 * 获取CPU个数
	 * 
	 * @return cpu核心数目
	 */
	private static int getCpuCoresNumInner(String filename) {
		// Private Class to display only CPU devices in the directory listing
		class CpuFilter implements FileFilter {
			@Override
			public boolean accept(File pathname) {
				// Check if filename is "cpu", followed by a single digit number
				if (Pattern.matches(CPU_REGEX, pathname.getName())) {
					return true;
				}
				return false;
			}
		}
		try {
			// Get directory containing CPU info
			File dir = new File(filename);
			// Filter to only list the devices we care about
			File[] files = dir.listFiles(new CpuFilter());
			// Return the number of cores (virtual CPU devices)
			return files.length;
		} catch (Exception e) {
			LeLog.e(e);
			return 1;
		}
	}
	
	public static String getDeviceManufacturer() {
		return android.os.Build.MANUFACTURER;
	}

	public static String getDeviceBrand() {
		return android.os.Build.BRAND;
	}

	public static String getDeviceMode() {
		return android.os.Build.MODEL;
	}

    public static String getDeviceType() {
        if (sMachineType == TYPE_PAD) {
            return "PAD";
        } else {
            return "PHONE";
        }
    }

	public static int getSDKVersionInt() {
		return android.os.Build.VERSION.SDK_INT;
	}

	public static String getSdkVersion() {
		return android.os.Build.VERSION.SDK;
	}

	public static String getOsVersion() {
		return android.os.Build.VERSION.RELEASE;
	}
	
	public static String getSimOperator() {
		TelephonyManager tm = (TelephonyManager) sContext.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getSimOperator();
	}
	
	public static int getHorizontalResolution() {
		DisplayMetrics dm = sContext.getResources().getDisplayMetrics();
		return dm.widthPixels;
	}
	
	public static int getVerticalResolution() {
		DisplayMetrics dm = sContext.getResources().getDisplayMetrics();
		return dm.heightPixels;
	}
	
	public static int getDpi() {
		DisplayMetrics dm = sContext.getResources().getDisplayMetrics();
		return dm.densityDpi;
	}
	
	public static float getDensity() {
		DisplayMetrics dm = sContext.getResources().getDisplayMetrics();
		return dm.density;
	}
	
	public static String getCpuType() {
		String[] cpuInfo = getCpuInfo();
		if (cpuInfo != null && cpuInfo.length > 0) {
			return cpuInfo[0];
		}
		return "";
	}
	
	/**
	 * 获取手机CPU信息
	 * 
	 * @return
	 */
	public static String[] getCpuInfo() {
		String str1 = "/proc/cpuinfo";
		String str2 = "";
		String[] cpuInfo = { "", "" }; //cpuInfo[0]:型号；cpuInfo[1]：频率。
		String[] arrayOfString;
		try {
			FileReader fr = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			for (int i = 2; i < arrayOfString.length; i++) {
				cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
			}
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			cpuInfo[1] += arrayOfString[2];
			localBufferedReader.close();
		} catch (IOException e) {
		}
		return cpuInfo;
	}

	//判断指令集.
	public static final boolean isX86() {
		if ((Build.CPU_ABI != null) && Build.CPU_ABI.toLowerCase().contains("x86")) {
			LeLog.i("zyb this machine is x86");
			return true;
		} else if (!TextUtils.isEmpty(Build.MODEL) && Build.MODEL.toLowerCase().equals("lenovo p80")) {
			LeLog.i("zyb this machine is p80");
			return true;
		} else if (!TextUtils.isEmpty(Build.MODEL) && Build.MODEL.toLowerCase().equals("lenovo k80m")) {
			LeLog.i("zyb this machine is k80m");
			return true;
		} else if (!TextUtils.isEmpty(Build.MODEL) && Build.MODEL.toLowerCase().contains("lenovo yb1")) {
			LeLog.i("zyb this machine is YB1");
			return true;
		} else if (!TextUtils.isEmpty(Build.MODEL) && Build.MODEL.toLowerCase().contains("lenovo yt3")) {
			LeLog.i("zyb this machine is YT3");
			return true;
		}
		else {			
			LeLog.i("zyb this machine is not x86");
		}
		return false;
	}

	public static String getUniqueIdentification() {
		SharedPreferences settings = sActivity.getPreferences(sContext.MODE_PRIVATE);
		if (settings.contains(UNIQUE_ID)) {
			return settings.getString(UNIQUE_ID, null);
		}
		else {
			String imei = getImei();
			String pseudoIMEI = getPseudoIMEI();
			String macAddress = getMacAddress();
			StringBuilder uniqueID = new StringBuilder();
			if (imei != null) {
				uniqueID.append(imei);
			}
			if (pseudoIMEI != null) {
				uniqueID.append(pseudoIMEI);
			}
			if (macAddress != null) {
				macAddress = macAddress.replace(":", "");
				uniqueID.append(macAddress);
			}
			MessageDigest m = null;
			try {
				m = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			m.update(uniqueID.toString().getBytes(), 0, uniqueID.length());
			// get md5 bytes
			byte md5Data[] = m.digest();
			// create a hex string
			String m_szUniqueID = new String();
			for (int i = 0; i < md5Data.length; i++) {
				int b = (0xFF & md5Data[i]);
				// if it is a single digit, make sure it have 0 in front (proper padding)
				if (b <= 0xF) m_szUniqueID += "0";
				// add number to string
				m_szUniqueID += Integer.toHexString(b);
			}

			long time = System.currentTimeMillis();
			String timeString = Long.toHexString(time);
			for (int i = timeString.length(); i < 12; ++i) {
				m_szUniqueID += "0";
			}
			m_szUniqueID += timeString;

			// hex string to uppercase
			m_szUniqueID = m_szUniqueID.toUpperCase();

			SharedPreferences.Editor editor = settings.edit();
			editor.putString(UNIQUE_ID, m_szUniqueID);
			editor.commit();
			return m_szUniqueID;
		}
	}

	public static String getPseudoIMEI() {
		String PIMEI = "35" + Build.BOARD.length() % 10
				+ Build.BRAND.length() % 10
				+ Build.CPU_ABI.length() % 10
				+ Build.DEVICE.length() % 10
				+ Build.DISPLAY.length() % 10
				+ Build.HOST.length() % 10
				+ Build.ID.length() % 10
				+ Build.MANUFACTURER.length() % 10
				+ Build.MODEL.length() % 10
				+ Build.PRODUCT.length() % 10
				+ Build.TAGS.length() % 10
				+ Build.TYPE.length() % 10
				+ Build.USER.length() % 10;
		return PIMEI;
	}


    public static boolean isRootSystem() {
        File f;
        final String kSuSearchPaths[] = {"/system/bin/", "/system/xbin/",
                "/system/sbin/", "/sbin/", "/vendor/bin/"};
        try {
            for (int i = 0; i < kSuSearchPaths.length; i++) {
                f = new File(kSuSearchPaths[i] + "su");
                if (f != null && f.exists()) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    public static long getSystemBootTimeMillis() {
        return System.currentTimeMillis() - android.os.SystemClock.elapsedRealtime();
    }
}
