/** 
 * Filename:    LeStringUtil.java
 * Description:  
 * Copyright:   Lenovo PCL Copyright(c)2013 
 * @author:     chenwei27 
 * @version:    1.0
 * Create at:   2013-12-9 下午1:21:23
 * 
 * Modification History: 
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------ 
 * 2013-12-9     chenwei27    1.0         1.0 Version 
 */
package com.beiying.fitmanager.core.utils;

import android.text.TextUtils;

import com.beiying.fitmanager.core.LeLog;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LeStringUtil {
	
	public static String md5Upper(String s) {
		return md5(s).toUpperCase();
	}
	
	public static String md5(String s) {
		try {
			if (TextUtils.isEmpty(s)) {
				return "";
			}
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(s.getBytes());
			byte[] result = messageDigest.digest();
			return toHexString(result);
		} catch (NoSuchAlgorithmException e) {
		}
		return "";
	}

	public static String toHexString(byte[] byteArray) {
		StringBuilder hexStringBuilder = new StringBuilder();
		for (byte i : byteArray) {
			String temp = Integer.toHexString(0xFF & i);
			if (temp.length() == 1)
				hexStringBuilder.append(0);
			hexStringBuilder.append(temp);
		}
		return hexStringBuilder.toString();
	}
	
	/** 
	* @Title: getOverlapString  
	* @Description: 获取合并两个字符串的重叠部分并返回结果，没有重叠则返回null<p> 
	*               比如：56、67则返回567，56、89则返回null 
	* @param str1 前合并串 
	* @param str2 后合并串 
	* @return  有重叠串则返回合并结果，没有则返回null  
	 */  
	public static final String getOverlapString(String str1, String str2) {
		if (str1 == null || str1.equals("")) {
			return str2;
		}
		if (str2 == null || str2.equals("")) {
			return str1;
		}
	    int index = -1;//重叠的开始位置  
	    int len = 0;//重叠串的长度  
	    String result = "";
	    for (int i = 0; i < str1.length(); i++) {//用前串控制外层循环,“指针”向右移动  
	        if(str1.charAt(i) == str2.charAt(0)){//判断右移过程“指针”位置的字符是否与后串的第一个字符匹配，需匹配才有重叠  
	            index = i;  
	            len ++;  
	            if(str1.length() - i > str2.length()){//如果前串的指针位置比后串的长度还要长，则退出，即没有重叠串  
	                index = -1;  
	                break;  
	            }  
	            for (int j = 1; j < str1.length() - i; j++) {  
	                if (str1.charAt(i + j) == str2.charAt(j)) {//前后串移动匹配，找出最长重叠串  
	                    len ++;  
	                }else{  
	                    index = -1;  
	                    len = 0;  
	                    break;  
	                }  
	            }  
	        }  
	    }  
	    if(index == -1){  
	        result = null;  
	    }else {  
	        result = str1 + str2.substring(len);  
	    }  
	    return result;   
	}  

	/**
	 * encoding.
	 *
	 * @param str
	 *            the a str
	 * @return true, if is valid utf8 wrapper
	 */

	public static boolean isValidUtf8Wrapper(String str) {
		byte[] ba;
		try {
			ba = str.getBytes("iso-8859-1");
		} catch (UnsupportedEncodingException e) {
			LeLog.i("UnsupportedEncodingException");
			return true;
		}

		return isValidUtf8(ba, ba.length);

	}
	
	/**
	 * judge utf8 or not with first maxCount Chars.
	 *
	 * @param b
	 *            the b
	 * @param maxCount
	 *            the a max count
	 * @return true, if is valid utf8
	 */
	private static boolean isValidUtf8(byte[] b, int maxCount) { // CHECKSTYLE:OFF
		int lLen = b.length, lCharCount = 0;
		for (int i = 0; i < lLen && lCharCount < maxCount; ++lCharCount) {
			byte lByte = b[i++];
			if (lByte >= 0)
			 {
				continue;// >=0 is normal ascii
			}
			if (lByte < (byte) 0xc0 || lByte > (byte) 0xfd) {
				return false;
			}
			int lCount = lByte > (byte) 0xfc ? 5 : lByte > (byte) 0xf8 ? 4 : lByte > (byte) 0xf0 ? 3
					: lByte > (byte) 0xe0 ? 2 : 1;
			if (i + lCount > lLen) {
				return false;
			}
			for (int j = 0; j < lCount; ++j, ++i) {
				if (b[i] >= (byte) 0xc0) {
					return false;
				}
			}
		}
		return true;
	} // CHECKSTYLE:ON
	
	public static String formatFileSize(long bytes) {
		float result = bytes;
		String suffix = "B";
		if (result > 1024) {
			suffix = "KB";
			result = result / 1024;
		}
		if (result > 1024) {
			suffix = "MB";
			result = result / 1024;
		}
		if (result > 1024) {
			suffix = "GB";
			result = result / 1024;
		}
		if (result > 1024) {
			suffix = "TB";
			result = result / 1024;
		}
		if (result > 1024) {
			suffix = "PB";
			result = result / 1024;
		}
		String value;
		if (result < 1) {
			value = String.format("%.1f", result);
		} else if (result < 10) {
			value = String.format("%.1f", result);
		} else if (result < 100) {
			value = String.format("%.1f", result);
		} else {
			value = String.format("%.0f", result);
		}
		return value + suffix;
	}
}
