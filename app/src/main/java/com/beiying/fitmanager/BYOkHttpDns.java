package com.beiying.fitmanager;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.sdk.android.httpdns.HttpDns;
import com.alibaba.sdk.android.httpdns.HttpDnsService;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import okhttp3.Dns;

public class BYOkHttpDns implements Dns {
    private static BYOkHttpDns sInstance;
    private HttpDnsService mDnsServie;

    private BYOkHttpDns(Context context) {
        mDnsServie = HttpDns.getService(context);
    }

    public static BYOkHttpDns getInstance(Context context) {
        if (sInstance == null) {
            synchronized (BYOkHttpDns.class) {
                sInstance = new BYOkHttpDns(context);
            }
        }
        return sInstance;
    }

    @Override
    public List<InetAddress> lookup(String hostname) throws UnknownHostException {
        String ip = mDnsServie.getIpByHostAsync(hostname);
        if (!TextUtils.isEmpty(ip)) {
            List<InetAddress> addresses = Arrays.asList(InetAddress.getAllByName(ip));
            return addresses;
        }
        return null;
    }
}
