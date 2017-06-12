package com.beiying.fitmanager.connectivity;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.beiying.fitmanager.R;
import com.beiying.fitmanager.bluetooth.BYBluetoothManager;
import com.beiying.fitmanager.core.ui.LeListView;
import com.beiying.fitmanager.core.ui.LeListViewModel;
import com.beiying.fitmanager.core.ui.LeUI;

public class BYBluetoothDeviceListView extends LeListView implements View.OnClickListener {
	private static final int UI_PADDING = 10;

	private Drawable mBgDrawable;
	private int mPadding;
	public BYBluetoothDeviceListView(Context context, LeListViewModel<BluetoothDevice> model) {
		super(context, model);
		initResource(context);
	}
	
	private void initResource(Context context) {
		mBgDrawable = getResources().getDrawable(R.drawable.card_panel_bg);
		mPadding = LeUI.getDensityDimen(context, UI_PADDING);
	}

	@Override
	protected View getListItem(int index, View convertView) {
		BYBluetoothDeviceItem item = null;
		BluetoothDevice device = (BluetoothDevice) mModel.get(index);
		if (device != null) {
			if (convertView == null){
				item = new BYBluetoothDeviceItem(getContext());
				item.setOnClickListener(this);
			} else {
				item = (BYBluetoothDeviceItem) convertView;
			}
			item.setItemModel(device);
		}
		
		return item;
	}

	@Override
	public void onClick(View v) {
		if (v instanceof BYBluetoothDeviceItem) {
			BYBluetoothManager.getInstance().connectGatt(((BYBluetoothDeviceItem)v).getItemModel().getAddress());
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mModel != null && mModel.getSize() > 0) {
			int offsetX = mPadding;
			int offsetY = mPadding;
			mBgDrawable.setBounds(offsetX, offsetY, getMeasuredWidth() - mPadding, getMeasuredHeight() - mPadding);
			mBgDrawable.draw(canvas);
		}
	}

}
