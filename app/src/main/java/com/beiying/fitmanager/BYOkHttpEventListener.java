package com.beiying.fitmanager;

import java.net.InetAddress;
import java.util.List;

import okhttp3.Call;
import okhttp3.EventListener;

public class BYOkHttpEventListener extends EventListener {

    public BYOkHttpEventListener() {

    }

    @Override
    public void callStart(Call call) {
        super.callStart(call);
    }

    @Override
    public void callEnd(Call call) {
        super.callEnd(call);
    }

    @Override
    public void dnsStart(Call call, String domainName) {
        super.dnsStart(call, domainName);
    }

    @Override
    public void dnsEnd(Call call, String domainName, List<InetAddress> inetAddressList) {
        super.dnsEnd(call, domainName, inetAddressList);
    }

    class BYOkHttpEvent {
        public long mDnsStartTime;
        public long mDnsEndTime;

        public boolean mApiSucces;
    }
}
