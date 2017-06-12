package com.beiying.fitmanager.connectivity;

import java.util.List;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.beiying.fitmanager.bluetooth.BYBluetoothManager;
import com.beiying.fitmanager.core.LeLog;
import com.beiying.fitmanager.core.ui.LeListViewModel;
import com.beiying.fitmanager.core.ui.LeTextButton;

public class BYBluetoothDeviceView extends LinearLayout implements OnClickListener {
	private LeTextButton mControlBtn;
	private BYBluetoothDeviceListView mBluetoothDeviceListView;
	private LeListViewModel<BluetoothDevice> mBluetoothDevices;
	public BYBluetoothDeviceView(Context context) {
		super(context);
		
		initViews(context);
		
		setOrientation(LinearLayout.VERTICAL);
		setWillNotDraw(false);
	}
	
	private void initViews(Context context) {
		LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		mControlBtn = new LeTextButton(context, "scan");
		mControlBtn.setOnClickListener(this);
		addView(mControlBtn,lParams);
		
		mBluetoothDevices = new LeListViewModel<BluetoothDevice>();
		mBluetoothDeviceListView = new BYBluetoothDeviceListView(context, mBluetoothDevices);
		addView(mBluetoothDeviceListView,lParams);
	}

	@Override
	public void onClick(View v) {
		mBluetoothDevices = new LeListViewModel<BluetoothDevice>();
		mBluetoothDeviceListView.setModel(mBluetoothDevices);
		if (BYBluetoothManager.getInstance().isBluetoothEnabled()) {
			BYBluetoothManager.getInstance().scanLeDevice(true, new ScanCallback() {
				@Override
				public void onScanResult(int callbackType, ScanResult result) {
					LeLog.e("LY onScanResult device address="+result.getDevice().getAddress());
					LeLog.e("BYBluetoothManager onScanResult device name="+result.getDevice().getName());
					if (result != null) {
						mBluetoothDevices.add(result.getDevice());
					}
				}
				
				@Override
				public void onBatchScanResults(List<ScanResult> results) {
					LeLog.e("LY onBatchScanResults");
					super.onBatchScanResults(results);
				}
				
				@Override
				public void onScanFailed(int errorCode) {
					LeLog.e("LY onScanFailed");
					super.onScanFailed(errorCode);
				}
			});
		} else {
			
		}
	}

}
