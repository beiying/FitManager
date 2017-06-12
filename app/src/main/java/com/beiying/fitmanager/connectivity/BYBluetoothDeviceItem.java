package com.beiying.fitmanager.connectivity;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;

import com.beiying.fitmanager.R;
import com.beiying.fitmanager.core.ui.LeUI;
import com.beiying.fitmanager.core.ui.LeView;
import com.beiying.fitmanager.core.utils.LeTextUtil;

public class BYBluetoothDeviceItem extends LeView {
	private static final int UI_HEIGHT = 50;
	private static final int UI_PADDING = 10;

	private Paint mNamePaint;
	private Paint mAddressPaint;
	private BluetoothDevice mDevice;
	public BYBluetoothDeviceItem(Context context) {
		super(context);
		setWillNotDraw(false);
		initResource(context);
	}

	private void initResource(Context context) {
		mNamePaint = new Paint();
		mNamePaint.setAntiAlias(true);
		mNamePaint.setColor(Color.BLACK);
		mNamePaint.setTextSize(getResources().getDimension(R.dimen.common_text_big_size));
		
		mAddressPaint = new Paint();
		mAddressPaint.setAntiAlias(true);
		mAddressPaint.setColor(Color.BLACK);
		mAddressPaint.setTextSize(getResources().getDimension(R.dimen.common_text_normal_size));
	}

	public void setItemModel(BluetoothDevice device) {
		mDevice = device;
		invalidate();
	}
	
	public BluetoothDevice getItemModel() {
		return mDevice;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), LeUI.getDensityDimen(getContext(), UI_HEIGHT));
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if (isPressed()) {
			canvas.drawColor(getResources().getColor(R.color.common_clicked_bg_color));
		} else {
			canvas.drawColor(Color.WHITE);
		}
		int offsetX = 0;
		int offsetY = 0;
		offsetX = LeUI.getDensityDimen(getContext(), UI_PADDING);
		offsetY = (getMeasuredHeight() - LeTextUtil.getPaintHeight(mNamePaint) - LeTextUtil.getPaintHeight(mAddressPaint)) / 2 - (int)mNamePaint.getFontMetrics().ascent;
		
		canvas.drawText(TextUtils.isEmpty(mDevice.getName())?"unknown device":mDevice.getName(), offsetX, offsetY, mNamePaint);
		
		offsetY += LeTextUtil.getPaintHeight(mAddressPaint);
		canvas.drawText(mDevice.getAddress(), offsetX, offsetY, mAddressPaint);
	}

}
