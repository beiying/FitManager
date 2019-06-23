package com.beiying.net;

import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by beiying on 19/4/7.
 */

public class CryptoUtils {
    public static String md5(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        StringBuffer buffer = new StringBuffer();
        try{
            MessageDigest digest = MessageDigest.getInstance("md5");
            digest.update(url.getBytes());
            byte[] cipher = digest.digest();
            for (byte b: cipher) {
                String hexStr = Integer.toHexString(b & 0xff);
                buffer.append(hexStr.length() == 1 ? "0" + hexStr : hexStr);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return buffer.toString();


    }

}
