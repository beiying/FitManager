package com.beiying.demo;

import android.app.Service;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LifecycleOwner{

    private RootView mRootView;
    private LifecycleRegistry lifecycleRegistry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRootView = new RootView(this);

        setContentView(mRootView);
//        UETool.showUETMenu();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor(Color.RED);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

//        ActivityTestBinding binding = ActivityTestBinding.inflate(getLayoutInflater());

        CommonUtil.getInstance(this).showToast();

//        testAIDL();

    }

    public void testAIDL() {
        bindService(new Intent(this, AIDLService.class), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                try {
                    List<Book> books = IBookManager.Stub.asInterface(service).getBooks();
                    IBookManager.Stub.asInterface(service).addListener(new IOnNewBook.Stub() {
                        @Override
                        public void onNewBook(Book newBook) throws RemoteException {
                            Log.e("onNewBook", newBook.toString());
                        }
                    });
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Service.BIND_AUTO_CREATE);

        BinderPool.getInstance(this).getBinder(BinderPool.BINDER_BOOK);
    }

    public void testLocalBroadcastManager() {
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        lbm.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

            }
        }, new IntentFilter("LOCAL_ACTION"));
        lbm.sendBroadcast(new Intent("LOCAL_ACTION"));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        lifecycleRegistry.markState(Lifecycle.State.STARTED);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        lifecycleRegistry.markState(Lifecycle.State.DESTROYED);
    }
}
