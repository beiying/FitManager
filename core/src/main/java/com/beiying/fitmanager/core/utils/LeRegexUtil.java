/** 
 * Filename:    LeRegexUtil.java
 * Description:  
 * Copyright:   Lenovo PCL Copyright(c)2013 
 * @author:     chenwei27 
 * @version:    1.0
<<<<<<< HEAD
 * Create at:   2013-10-24 上午10:09:50
 * 
 * Modification History: 
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------ 
 * 2013-10-24     chenwei27    1.0         1.0 Version 
 */
package com.beiying.fitmanager.core.utils;

import java.util.regex.Pattern;

public class LeRegexUtil {
	
	private LeRegexUtil(){}
	
	public static boolean isIntegerStr(String str) {
		String pattern = "[0-9]+";
		Pattern p = Pattern.compile(pattern);
		return p.matcher(str).matches();
	}

}