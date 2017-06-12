package com.beiying.fitmanager.framework;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.ParcelUuid;
import android.view.View;
import android.view.View.MeasureSpec;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.beiying.fitmanager.bluetooth.BYBluetoothManager;
import com.beiying.fitmanager.bluetooth.BYBluetoothManager.ConnectedSocketManager;
import com.beiying.fitmanager.connectivity.BYBluetoothDeviceItemModel;
import com.beiying.fitmanager.connectivity.BYBluetoothDeviceListView;
import com.beiying.fitmanager.core.LeLog;
import com.beiying.fitmanager.core.ui.LeListView;
import com.beiying.fitmanager.core.ui.LeListViewModel;
import com.beiying.fitmanager.core.ui.LeView;
import com.beiying.fitmanager.datacollect.BYKeyValueVerticalView;

public class BYRootView extends LeView {

	private LeView mBackgroundView;
	private BYBluetoothDeviceListView mBluetoothDeviceListView;
//	private BYConnectionFrameView mConnectionFrameView;
	private BYFrameworkView mFrameworkView;
	
	private LeListViewModel<BluetoothDevice> mListModel = new LeListViewModel<BluetoothDevice>();
	
	public BYRootView(Context context) {
		super(context);
		
		mFrameworkView = new BYFrameworkView(context);
		addView(mFrameworkView);
//		BYControlCenter.getInstance().init(this);
//		mBackgroundView = new LeView(context);
//		addView(mBackgroundView);
//		addView(new BYKeyValueVerticalView(context, "时长", "00:00:00", 12, 24, 0, 0));
//		WebView webview = new WebView(context);
//		webview.setWebViewClient(new WebViewClient(){       
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {       
//                view.loadUrl(url);       
//                return true;       
//            }       
//		});   
//		WebSettings webSettings = webview.getSettings();       
//        webSettings.setJavaScriptEnabled(true);  
//        webview.loadUrl("http://m.taobao.com/");
//		addView(webview);
//        showBluetoothDeviceList(context);
		
	}
	private void showBluetoothDeviceList(Context context) {
		BYBluetoothManager mBluetoothManager = BYBluetoothManager.getInstance();
		if (mBluetoothManager.isSupportBluetooth() && mBluetoothManager.isBluetoothEnabled()) {
			Set<BluetoothDevice> bluetoothDevices = mBluetoothManager.getBluetoothDevices();
			if (bluetoothDevices != null && bluetoothDevices.size() > 0) {
				for (BluetoothDevice device : bluetoothDevices) {
//					BYBluetoothDeviceItemModel deviceItemModel = new BYBluetoothDeviceItemModel();
//					deviceItemModel.setmDeviceName(device.getName());
//					deviceItemModel.setmDeviceAdd(device.getAddress());
					LeLog.e("state="+device.getBondState()+";type="+device.getType());
					ParcelUuid[] uuids = device.getUuids();
					for (ParcelUuid uuid : uuids){
						LeLog.e("uuids="+uuid.toString());
					}
					mListModel.add(device);
					UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");  
					BYBluetoothManager.getInstance().getBluetoothSocketAsClient(device, MY_UUID, new ConnectedSocketManager(){

						@Override
						public void manageConnectedSocket(BluetoothSocket socket) {
							try {
								socket.getOutputStream().write("hello world".getBytes());
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						
					});
				}
				mBluetoothDeviceListView = new BYBluetoothDeviceListView(context, mListModel);
//				mConnectionFrameView = new BYConnectionFrameView(context, mListModel);
				addView(mBluetoothDeviceListView);
//				addView(mConnectionFrameView);
			}
		}
		
	}
	
	public BYFrameworkView getFrameworkView() {
		return mFrameworkView;
	}
//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		for (int i = 0; i < getChildCount(); i++) {
//        	final View child = getChildAt(i);
//            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
//		}
//        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
//	}
	public void showNavigationView() {
		mFrameworkView.showNavigationView();
	}
	
	public void hideNavigationView() {
		mFrameworkView.hideNavigationView();
	}
	
}

