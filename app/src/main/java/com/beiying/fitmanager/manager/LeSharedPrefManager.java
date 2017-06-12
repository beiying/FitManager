/** 
 * Filename:    LeSharedPrefManager.java
 * Description:  
 * Copyright:   Lenovo PCL Copyright(c)2013 
 * @author:     chenwei27 
 * @version:    1.0
 * Create at:   2014-2-24 下午5:42:53
 * 
 * Modification History: 
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------ 
 * 2014-2-24     chenwei27    1.0         1.0 Version 
 */
package com.beiying.fitmanager.manager;

import android.content.Context;

import com.beiying.fitmanager.BYBasicContainer;
import com.beiying.fitmanager.BYMainActivity;
import com.beiying.fitmanager.LeApplicationHelper;
import com.beiying.fitmanager.core.data.LeMPSharedPrefUnit;
import com.beiying.fitmanager.core.data.LeSharedPrefFactory;
import com.beiying.fitmanager.core.data.LeSpHelper;


public class LeSharedPrefManager extends BYBasicContainer {
	
	public static LeSharedPrefFactory getFactory() {
		return new LeSharedPrefFactory() {
			
			@Override
			public LeSpHelper createCommonHelper() {
				return createCommonSpHelper();
			}
		};
	}

	private static LeSpHelper createCommonSpHelper() {
		String spName = formSpName();
		return new LeSpHelper(spName);
	}

	private static String formSpName() {
		return adjustSpName(LeApplicationHelper.PACKAGE);
	}
	
	private static String adjustSpName(String pkgName) {
		return pkgName.replace(".", "_") + "_sp";
	}
	
	public static LeSpHelper createMultiProSpHelper(Context context) {
		String spName = LeMPSharedPrefUnit.adjustSpName(BYMainActivity.class.getPackage().getName());
		return new LeSpHelper(spName, Context.MODE_MULTI_PROCESS);
	}
}
