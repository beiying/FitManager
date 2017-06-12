/** 
 * Filename:    LeSystemManager.java
 * Description:  
 * Copyright:   Lenovo PCL Copyright(c)2013 
 * @author:     chenwei27 
 * @version:    1.0
 * Create at:   2014-4-1 上午11:30:03
 * 
 * Modification History: 
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------ 
 * 2014-4-1     chenwei27    1.0         1.0 Version 
 */
package com.beiying.fitmanager.manager;

import com.beiying.fitmanager.BYBasicContainer;
import com.beiying.fitmanager.utils.LePackageUtils;


public class LeSystemManager extends BYBasicContainer {
	
	public static boolean invokeInstall(LeInstallInfo installInfo) {
		if (installInfo == null) {
			return false;
		}
		int res = LePackageUtils.install(sContext, installInfo.mFilePath);
        
        notifyInstall(res, installInfo);
        
        if (res == LePackageUtils.INSTALL_RESULT_SILENT) {
        	installInfo.mSilentInstall = true;
        	return true;
        }
        return false;
	}
	
	private static void notifyInstall(int result, LeInstallInfo installInfo) {
		if (result == LePackageUtils.INSTALL_RESULT_SILENT) {
        	LeEventCenter.getInstance().broadcastEvent(LeEventCenter.EVENT_INSTALLING, installInfo);
        } else if (result == LePackageUtils.INSTALL_RESULT_FAIL) {
        	LeEventCenter.getInstance().broadcastEvent(LeEventCenter.EVENT_INSTALL_FAIL, installInfo);
        } else if (result == LePackageUtils.INSTALL_RESULT_NORMAL) {
        	LeEventCenter.getInstance().broadcastEvent(LeEventCenter.EVENT_INVOKE_SYSTEM_INSTALL, installInfo);
        }
	}
	
	public static class LeInstallInfo {
		public String mPkgName;
		public String mFilePath;
		public boolean mSilentInstall;
		public LeInstallInfo(String filePath, String pkgName) {
			mFilePath = filePath;
			mPkgName = pkgName;
		}
	}
}