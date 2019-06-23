package com.beiying.demo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

/**
 * Created by beiying on 2019/6/6.
 */

public class BinderPool {
    public static final int BINDER_BOOK = 0;
    private static BinderPool sInstance;

    private Context mContext;
    private IBinderPool mRemoteBinderPool;

    private BinderPool(Context context) {
        mContext = context.getApplicationContext();
        connectService();
    }

    public static BinderPool getInstance(Context context) {
        if (sInstance == null) {
            synchronized (BinderPool.class) {
                sInstance = new BinderPool(context);
            }
        }
        return sInstance;
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mRemoteBinderPool = IBinderPool.Stub.asInterface(service);
            try {
                mRemoteBinderPool.asBinder().linkToDeath(mBinderPoolDeathrecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onBindingDied(ComponentName name) {

        }
    };

    private IBinder.DeathRecipient mBinderPoolDeathrecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            mRemoteBinderPool.asBinder().unlinkToDeath(mBinderPoolDeathrecipient, 0);
            mRemoteBinderPool = null;
            connectService();
        }
    };

    public IBinder getBinder(int code) {
        IBinder binder = null;
        try {
            if (mRemoteBinderPool != null) {
                binder = mRemoteBinderPool.queryBinder(code);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return binder;
    }

    private synchronized void connectService() {
        mContext.bindService(new Intent(mContext, AIDLService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
    }


}
