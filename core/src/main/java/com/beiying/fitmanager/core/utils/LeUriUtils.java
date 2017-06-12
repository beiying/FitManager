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
package com.beiying.fitmanager.core.utils;


import com.beiying.fitmanager.core.LeLog;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


public class LeUriUtils {
	
	private LeUriUtils(){}
	
	public static String combineParam(String url, String param) {
		if (url == null) {
			return param;
		}
		if (param == null) {
			return url;
		}
		if (param.startsWith("?")) {
			param = param.replace("?", "");
		}
		if (url.contains("?")) {
			if (!param.startsWith("&")) {
				param = "&" + param;
			}
			return url + param;
		} else {
			if (param.startsWith("&")) {
				param = param.replaceFirst("&", "");
			}
			return url + "?" + param;
		}
	}
	
	public static String combinePath(String head, String tail) {
		if (head == null) {
			return tail;
		}
		if (tail == null) {
			return head;
		}
		if (!head.endsWith("/")) {
			head = head + "/";
		}
		tail = erazeSrcPrefix(tail);
		
		return head + tail;
	}
	
	public static String erazeSrcPrefix(String src) {
		if (src == null) {
			return src;
		}
		if (src.startsWith("/")) {
			src = src.substring("/".length());
		} else if (src.startsWith("../")) {
			src = src.substring("../".length());
		}
		return src;
	}
	
	public static String getDirFromUrl(String url) {
		if (url == null) {
			return null;
		}
		String dir = url;
		int index = url.lastIndexOf("/");
		if (index != -1) {
			dir = url.substring(0, index);
		}
		return dir;
	}
	
	public static String getFilenameFromUrl(String url) {
		if (url == null) {
			return null;
		}
		String name = url;
		int index = url.lastIndexOf("/");
		if (index > 0) {
			name = url.substring(index + 1, url.length());
		}
		return name;
	}
	
	public static String getParam(String url, String paramName) {
		if (url == null || paramName == null) {
			return null;
		}
		Map<String, String> list = URLRequest(url);
		if (list != null) {
			return list.get(paramName);
		}
		return null;
	}

	/**
	 * 解析出url参数中的键值对 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
	 *
	 * @param URL
	 *            url地址
	 * @return url请求参数部分
	 */
	public static Map<String, String> URLRequest(String URL){
		Map<String, String> mapRequest = new HashMap<String, String>();

		String[] arrSplit=null;

		String strUrlParam=TruncateUrlPage(URL);
		if(strUrlParam==null){
			return mapRequest;
		}
		//每个键值为一组
		arrSplit=strUrlParam.split("[&]");
		for(String strSplit:arrSplit){
			String[] arrSplitEqual=null;
			arrSplitEqual= strSplit.split("[=]");

			//解析出键值
			if(arrSplitEqual.length>1){
				//正确解析
				mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);

			} else {
				if(arrSplitEqual[0]!="") {
					//只有参数没有值，不加入
					mapRequest.put(arrSplitEqual[0], "");
				}
			}
		}
		return mapRequest;
	}

	/**
	 * 解析出url请求的路径，包括页面
	 *
	 * @param strURL
	 *            url地址
	 * @return url路径
	 */
	public static String UrlPage(String strURL) {
		String strPage = null;
		String[] arrSplit = null;

		strURL = strURL.trim().toLowerCase();

		arrSplit = strURL.split("[?]");
		if (strURL.length() > 0) {
			if (arrSplit.length > 1) {
				if (arrSplit[0] != null) {
					strPage = arrSplit[0];
				}
			}
		}

		return strPage;
	}

	/**
	 * 去掉url中的路径，留下请求参数部分
	 *
	 * @param strURL
	 *            url地址
	 * @return url请求参数部分
	 */
	private static String TruncateUrlPage(String strURL) {
		String strAllParam = null;
		String[] arrSplit = null;

		strURL = strURL.trim().toLowerCase();

		arrSplit = strURL.split("[?]");
		if (strURL.length() > 1) {
			if (arrSplit.length > 1) {
				if (arrSplit[1] != null) {
					strAllParam = arrSplit[1];
				}
			}
		}

		return strAllParam;
	}
	
	public static String urlFilter(String url) {
		if (url == null) {
			return url;
		}
		return url.trim().replaceAll(" ", "%20");
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
	
	public static String getHost(final String url) {
		String retUrl = url;
		if (!LeUtils.isEmptyString(retUrl)) {
			int start = retUrl.indexOf("://");
			if (start > 0) {
				retUrl = retUrl.substring(start + 3);
			}
			if (retUrl.startsWith("www.")) {
				retUrl = retUrl.substring(4);
			}
			int end = retUrl.indexOf("/");
			if (end > 0) {
				retUrl = retUrl.substring(0, end);
			}
		}
		return retUrl;
	}
	
	public static String urlEncode(String url) {
		try {
			return URLEncoder.encode(url, "utf-8");
		} catch (Exception e) {
			LeLog.e(e);
		}
		return url;
	}
	
	public static String urlDecode(String url) {
		try {
			return URLDecoder.decode(url, "utf-8");
		} catch (Exception e) {
			LeLog.e(e);
		}
		return url;
	}
	
	public static int matchKeyInUrl(String url, String key) {
		if (key == null || key.equals(""))
			return -1;
		int index = -1;
		while (true) {
			index = url.indexOf(key, index + 1);
			if (index == -1) {
				return -1;
			}
			return index;
			//			else if (!Character.isLetterOrDigit(url.charAt(index - 1))) {
			//				return index;
			//			}
		}
	}
	
}
