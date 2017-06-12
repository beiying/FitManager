package com.beiying.fitmanager.manager;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.beiying.fitmanager.core.LeLog;


/**
 * 主要用于监听网路发生变化控制下载
 */
public class LeTelephonyManager extends LeBasicManager {
	public static final int NETWORK_CLASS_UNKNOWN = 0;
    /** Class of broadly defined "2G" networks.  */
    public static final int NETWORK_CLASS_2_G = 1;
    /** Class of broadly defined "3G" networks.  */
    public static final int NETWORK_CLASS_3_G = 2;
    /** Class of broadly defined "4G" networks.  */
    public static final int NETWORK_CLASS_4_G = 3;
    
    
	public static LeTelephonyManager sInstance;
	public static TelephonyManager sTelephonyManager;
	public static PhoneStateListener sPhoneStateListener;
	
	public LeTelephonyManager(){
		init();
	}
	
	@Override
	protected boolean onRelease() {
		release();
		return false;
	}
    
	private void init(){
		sTelephonyManager = (TelephonyManager)sContext.getSystemService(Context.TELEPHONY_SERVICE);
		sPhoneStateListener = new PhoneStateListener() {
			@Override
			public void onDataConnectionStateChanged(int state, int networkType) {
//				LeLog.e("zyb DataConnectionStateChanged state="+state+"; networkType:"+networkType);
				if (state == TelephonyManager.DATA_CONNECTED
						&& (getNetworkClass(networkType) == NETWORK_CLASS_2_G || getNetworkClass(networkType) == NETWORK_CLASS_3_G)) {
//					LeLog.e("zyb DataChanged 2G 2 3G!");
//					LeDownloadManager.resumeDownload();
				}
			}
		};
	}
	public static LeTelephonyManager getInstance(){
		if (sInstance != null && sInstance.reuse()) {
			return sInstance;
		}
		synchronized (LeTelephonyManager.class) {
			if (sInstance == null) {
				sInstance = new LeTelephonyManager();
			}
		}
		return sInstance;
	}
	
	/**
     * Return general class of network type, such as "3G" or "4G". In cases
     * where classification is contentious, this method is conservative.
     *
     */
	public static int getNetworkClass(int networkType) {
		switch (networkType) {
			case TelephonyManager.NETWORK_TYPE_GPRS:
			case TelephonyManager.NETWORK_TYPE_EDGE:
			case TelephonyManager.NETWORK_TYPE_CDMA:
			case TelephonyManager.NETWORK_TYPE_1xRTT:
			case TelephonyManager.NETWORK_TYPE_IDEN:
				return NETWORK_CLASS_2_G;
			case TelephonyManager.NETWORK_TYPE_UMTS:
			case TelephonyManager.NETWORK_TYPE_EVDO_0:
			case TelephonyManager.NETWORK_TYPE_EVDO_A:
			case TelephonyManager.NETWORK_TYPE_HSDPA:
			case TelephonyManager.NETWORK_TYPE_HSUPA:
			case TelephonyManager.NETWORK_TYPE_HSPA:
			case TelephonyManager.NETWORK_TYPE_EVDO_B:
			case TelephonyManager.NETWORK_TYPE_EHRPD:
			case TelephonyManager.NETWORK_TYPE_HSPAP:
				return NETWORK_CLASS_3_G;
			case TelephonyManager.NETWORK_TYPE_LTE:
				return NETWORK_CLASS_4_G;
			default:
				return NETWORK_CLASS_UNKNOWN;
		}
	}
	
	public void registerListener(int events){
		LeLog.e("registerListener events="+events);
		sTelephonyManager.listen(sPhoneStateListener, events);
	}

	private static void release() {
		sTelephonyManager.listen(sPhoneStateListener, PhoneStateListener.LISTEN_NONE);
	}
}
