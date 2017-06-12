/** 
 * Filename:    LeCoreManager.java
 * Description:  
 * Copyright:   Lenovo PCL Copyright(c)2013 
 * @author:     chenwei27 
 * @version:    1.0
 * Create at:   2014-2-19 上午10:37:48
 * 
 * Modification History: 
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------ 
 * 2014-2-19     chenwei27    1.0         1.0 Version 
 */
package com.beiying.fitmanager.core;

import android.app.Activity;

import com.beiying.fitmanager.core.data.LeSharedPrefFactory;
import com.beiying.fitmanager.core.data.LeSharedPrefUnit;
import com.beiying.fitmanager.core.net.LeHttpNet;
import com.beiying.fitmanager.core.sqlite.LeSqliteManager;
import com.beiying.fitmanager.core.utils.LeMachineHelper;
import com.beiying.fitmanager.core.weblite.LeWeblite;


/**
 * core包独立于应用，包含应用的基本组件。
 * 主包：基础组件
 * collect: 扩展集合
 * data: 数据持久化组件，包括文件、SharedPreferrence
 * sqlite: 数据库组件
 * net: 网络组件
 * ui: UI组件
 * utils: 工具
 * 
 * 注意：应用使用core包，必须在主Activity创建时对core包进行初始化，调用LeCoreManager.init方法实现。
 */

public class LeCoreManager {
	
	private LeCoreManager() {}
	
	public static void init(String pkgName, Activity activity, LeSharedPrefFactory spFactory, LeHttpNet.LeUrlProcessor urlProcessor, boolean debugMode) {
		
		ContextContainer.activityStart(pkgName, activity);
		
		LeMachineHelper.init(activity);
		
		LeThreadCore.getInstance().init();
		
		LeSharedPrefUnit.setFactory(spFactory);
		
		LeHttpNet.init(urlProcessor);
		LeWeblite.init(urlProcessor);
		
		LeBuildConfig.init(debugMode);
		
	}
	
	public static void recycle() {
		LeSharedPrefUnit.recycle();
		LeThreadCore.recycle();
		LeSqliteManager.recycle();
	}
	
}
