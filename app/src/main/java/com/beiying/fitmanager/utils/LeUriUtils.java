/** 
 * Filename:    LeUriUtils.java
 * Description:  
 * Copyright:   Lenovo PCL Copyright(c)2013 
 * @author:     chenwei27 
 * @version:    1.0
 * Create at:   2013-7-4 下午5:23:59
 * 
 * Modification History: 
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------ 
 * 2013-7-4     chenwei27    1.0         1.0 Version 
 */
package com.beiying.fitmanager.utils;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LeUriUtils {
	
	private static final Pattern STRIP_URL_PATTERN = Pattern.compile("^http://(.*?)/?$");
	
	public static String urlFilter(String url) {
		if (url == null) {
			return url;
		}
		return url.replaceAll(" ", "%20");
	}

	private static String urlComplete(String url) {
		if (url == null)
			return "";

		if (!url.contains("://"))
			url = "http://" + url;
		if (!url.endsWith("/"))
			url = url + "/";
		return url;
	}

	public static boolean isUrlEqual(String url1, String url2) {
		url1 = urlComplete(url1);
		url2 = urlComplete(url2);
		return url1.equals(url2);
	}
	
	public static String stripUrl(String url) {
        if (url == null) return null;
        Matcher m = STRIP_URL_PATTERN.matcher(url);
        if (m.matches()) {
            return m.group(1);
        } else {
            return url;
        }
    }
	
	public static String getIconFileName(String link) {
		String name = null;
		if (link != null) {
			int index = link.lastIndexOf(File.separator);
			if (index != -1) {
				name = link.substring(index + 1, link.length());
			}
		}
		return name;
	}

}
