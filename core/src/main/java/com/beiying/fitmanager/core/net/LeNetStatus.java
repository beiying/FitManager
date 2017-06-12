/** 
 * Filename:    NetStatus.java
 * Description:  
 * Copyright:   Lenovo PCL Copyright(c)2013 
 * @author:     chenwei27 
 * @version:    1.0
 * Create at:   2013-6-25 下午4:50:59
 * 
 * Modification History: 
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------ 
 * 2013-6-25     chenwei27    1.0         1.0 Version 
 */
package com.beiying.fitmanager.core.net;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.beiying.fitmanager.core.LeLog;
import com.beiying.fitmanager.core.utils.LeAndroidUtils;


public class LeNetStatus {
	private static final String CMWAP_IP = "10.0.0.172";
	private static final String CTWAP_IP = "10.0.0.200";
	private static final int PORT_80 = 80;
	private static final String CMWAP_PROXY = CMWAP_IP + ":" + PORT_80;
	private static final String CTWAP_PROXY = CTWAP_IP + ":" + PORT_80;
	
	/** apn相关地址 **/
	private static final Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");

	/** 流量统计，统计除webkit外的所有流量 */
	public static long sFlowCount = 0;

	/** 是否使用wap联网 */
	private static boolean sUseWap = false;
	
	/** 2g 3g 4g 未知类型归为2g*/
	private static GTYPE sGType = GTYPE.G2;

	private static String sWapUrl = CMWAP_PROXY;

	/** 当前网络是否处在连通状态 */
	private static boolean sNetworkUp = true;
	/** 网络类型 */
	private static String sNetmode = "no_net";
	/** 网络类型的细节字段 */
	private static String sNetmodeExtra = "unknow";
	
	enum GTYPE {
		G2,
		G3,
		G4
	}
	
	private LeNetStatus(){}
	
	public static boolean isWifiAvailable(Context context) {
		if (context == null) {
			return false;
		}
		ConnectivityManager connManager = LeAndroidUtils.getConnectivityManager(context);
		NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return wifi.isAvailable();
	}
	
	public static boolean is2G() {
		return sNetworkUp && !isWifi() && sGType == GTYPE.G2;
	}
	
	public static boolean is3G() {
		return sNetworkUp && !isWifi() && sGType == GTYPE.G3;
	}
	
	public static boolean is4G() {
		return sNetworkUp && !isWifi() && sGType == GTYPE.G4;
	}
	
	public static boolean isWifi() {
		return sNetworkUp && "wifi".equals(sNetmode);
	}

	public static boolean isWifiIgnoreCheckNetWork(Context context) {
		ConnectivityManager connectivityManager = LeAndroidUtils.getConnectivityManager(context);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

		if (activeNetInfo != null && activeNetInfo.isConnected()) {
			if ("wifi".equalsIgnoreCase(activeNetInfo.getTypeName())) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isNetworkUp() {
		return sNetworkUp;
	}

	public static boolean isCtwap() {
		if (isWap() && sWapUrl.startsWith(CTWAP_IP)) {
			return true;
		}
		return false;
	}
	
	public static boolean isCmwap() {
		if (isWap() && sWapUrl.startsWith(CMWAP_IP)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 是否开启spdy的条件, 非wap + (3g 或者 4g)
	 * @return 返回是否需要开启。
	 */
	public static boolean isSpdyNet() {
		if (!isWap() && (is3G() || is4G())) {
			return true;
		}
		return false;
	}
	
	public static boolean isWap() {
		return sNetworkUp && sUseWap;
	}

	public static void checkNetwork(Context context) {
		if (context == null) {
			return ;
		}
		ConnectivityManager connectivityManager = LeAndroidUtils.getConnectivityManager(context);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		//Log.i("CW", "activeNetInfo=" + activeNetInfo);

		if (activeNetInfo != null && activeNetInfo.isConnected()) {
			sNetworkUp = true;
			if (activeNetInfo.getTypeName() != null) {
				sNetmode = activeNetInfo.getTypeName()
						.toLowerCase();
			}
			sNetmodeExtra = activeNetInfo.getSubtypeName() + "-" + activeNetInfo.getExtraInfo();
			//Log.i("CW", "net name=" + LeNetStatus.sNetmode + LeNetStatus.sNetmodeExtra);
			if ("wifi".equals(LeNetStatus.sNetmode)) {
				sUseWap = false;
			} else {
				checkApnType(context, activeNetInfo);
				checkGType(activeNetInfo.getSubtype());
			}
		} else { // 没有可用网络
			sNetworkUp = false;
			sNetmode = "no_net";
		}
	}
	
	private static void checkGType(int type) {
		//Log.i("CW", "subtype:" + type);
		switch (type) {
			case TelephonyManager.NETWORK_TYPE_UMTS:
			case TelephonyManager.NETWORK_TYPE_EVDO_0:
			case TelephonyManager.NETWORK_TYPE_EVDO_A:
			case TelephonyManager.NETWORK_TYPE_HSDPA:
			case TelephonyManager.NETWORK_TYPE_HSUPA:
			case TelephonyManager.NETWORK_TYPE_HSPA:
			case TelephonyManager.NETWORK_TYPE_EVDO_B:
			case TelephonyManager.NETWORK_TYPE_EHRPD:
			case TelephonyManager.NETWORK_TYPE_HSPAP:
				sGType = GTYPE.G3;
				break;
			case TelephonyManager.NETWORK_TYPE_LTE:
				sGType = GTYPE.G4;
				break;

			default:
				sGType = GTYPE.G2;
				break;
		}
	}
	
	private static boolean is3GNetType(int type) {
		if (type == TelephonyManager.NETWORK_TYPE_UMTS) {
			return true;
		}
		if (type == TelephonyManager.NETWORK_TYPE_HSDPA) {
			return true;
		}
		if (type == TelephonyManager.NETWORK_TYPE_HSPA) {
			return true;
		}
		if (type == TelephonyManager.NETWORK_TYPE_HSUPA) {
			return true;
		}
		if (type == TelephonyManager.NETWORK_TYPE_EVDO_0) {
			return true;
		}
		if (type == TelephonyManager.NETWORK_TYPE_EVDO_A) {
			return true;
		}
		return false;
	}

	/** 检测apn类型 */
	private static void checkApnType(Context context, NetworkInfo networkInfo) {
		// 先根据网络信息判断
		if (networkInfo.getExtraInfo() == null) {
			return;
		}
		String infor = networkInfo.getExtraInfo().toLowerCase();
		LeLog.v("infor=" + infor);
		if (infor != null) {
			if (infor.startsWith("cmwap") || infor.startsWith("uniwap") || infor.startsWith("3gwap")) {
				LeNetStatus.sUseWap = true;
				LeNetStatus.sWapUrl = CMWAP_PROXY;
				return;
			} else if (infor.startsWith("ctwap")) {
				LeNetStatus.sUseWap = true;
				LeNetStatus.sWapUrl = CTWAP_PROXY;
			} else if (infor.startsWith("cmnet") || infor.startsWith("uninet") || infor.startsWith("ctnet")
					|| infor.startsWith("3gnet")) {
				LeNetStatus.sUseWap = false;
				return;
			}
		}

		// 再根据apn来判断，4.2以下版本可以使用
		if (Build.VERSION.SDK_INT < 17) { // SUPPRESS CHECKSTYLE
			try {
				Cursor cursor = context.getContentResolver().query(PREFERRED_APN_URI,
						new String[] { "_id", "apn", "proxy", "user" }, null, null, null);
				if (cursor == null) {
					return;
				}
				cursor.moveToFirst();
				int count = cursor.getCount();
				LeLog.v("cursor count=" + count);
				if (!cursor.isAfterLast()) {
					String apn = cursor.getString(1);
					String proxy = cursor.getString(2);
					String user = cursor.getString(3);
					if (proxy != null && proxy.length() > 0) {
						if (CMWAP_IP.equals(proxy.trim())) {
							// 当前网络连接类型为cmwap || uniwap
							LeNetStatus.sUseWap = true;
							LeNetStatus.sWapUrl = CMWAP_PROXY;
						} else if (CTWAP_IP.equals(proxy.trim())) { // 新增判断
							LeNetStatus.sUseWap = true;
							LeNetStatus.sWapUrl = CTWAP_PROXY;
						} else {
							// 否则为net
							LeNetStatus.sUseWap = false;
						}
					} else if (apn != null && apn.length() > 0) {
						String strApn = apn.toUpperCase();
						if (strApn.equals("CMWAP") || strApn.equals("UNIWAP") || strApn.equals("3GWAP")) {
							LeNetStatus.sUseWap = true;
							LeNetStatus.sWapUrl = CMWAP_PROXY;
						} else if (strApn.equals("CTWAP")) {
							LeNetStatus.sUseWap = true;
							LeNetStatus.sWapUrl = CTWAP_PROXY;
						} else {
							if (user != null) {
								if (user.toUpperCase().startsWith("CMWAP")) {
									LeNetStatus.sUseWap = true;
								} else {
									LeNetStatus.sUseWap = false;
								}
							} else
								LeNetStatus.sUseWap = false;
						}
					} else {
						// 其它网络都看作是net
						LeNetStatus.sUseWap = false;
					}
				}
				// Log.e(TAG, "Global.g_bUseCMWap = " + Global.g_bUseCMWap);
				cursor.close();
			} catch (Exception e) {
				//Log.e("check apn", "checkApnType Exception", e);
			}
		}
	}
	
	public static String dumpStatus() {
		StringBuilder sb = new StringBuilder();
		sb.append("Network Up: " + isNetworkUp() + "\n");
		String type = "UNKNOWN";
		if (isWifi()) {
			type = "Wifi";
		} else if (is4G()) {
			type = "4G";
		} else if (is3G()) {
			type = "3G";
		} else if (is2G()) {
			type = "2G";
		}
		sb.append("Network Type: " + type + "\n");
		String wap = "no wap";
		if (isWap()) {
			wap = "wap";
			if (isCmwap()) {
				wap = "cmwap";
			} else if (isCtwap()) {
				wap = "ctwap";
			}
		}
		sb.append(wap + "\n");
		
		return sb.toString();
	}
	
}
