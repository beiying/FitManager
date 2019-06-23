package com.beiying.fitmanager.navigation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.beiying.fitmanager.R;
import com.beiying.fitmanager.core.ui.LeButton;
import com.beiying.fitmanager.core.ui.LeUI;
import com.beiying.fitmanager.core.utils.BYBitmapUtil;
import com.beiying.fitmanager.core.utils.LeTextUtil;

public class BYNavigationMenuItemView extends LeButton {
	public static final int UI_HEIGHT = 50;
	private static final int UI_ICON_TEXT_GAP = 25;
	private static final int UI_ICON_OFFSET_LEFT = 10;
	
	private Bitmap mIcon;
	
	private int mPosition = -1;
	private int mIconId;
	private int mSelectedIconId;
	private String mText;
	
	private Paint mPaint;
	private float mTextSize;
	private int mHeight;
	private int mIconTextGap;
	private int mIconOffsetLeft;
	
	private boolean isSelected = false;
	
	public BYNavigationMenuItemView(Context context) {
		super(context);
		
		initResource(context);
		
	}
	
	public void initResource(Context context) {
		mHeight = LeUI.getDensityDimen(context, UI_HEIGHT);
		mIconTextGap = LeUI.getDensityDimen(context, UI_ICON_TEXT_GAP);
		mTextSize = getResources().getDimension(R.dimen.navigation_menu_text_size);
		mIconOffsetLeft = LeUI.getDensityDimen(context, UI_ICON_OFFSET_LEFT);
		
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setTextSize(mTextSize);
		
	}
	public void setNormalIcon(int id) {
		mIconId = id;
		invalidate();
	}
	
	public void setSelectedIcon(int id) {
		mSelectedIconId = id;
		invalidate();
	}
	
	public void setText(String text) {
		mText = text;
	}
	
	public void setText(int id) {
		mText = (String) getResources().getText(id);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = BYBitmapUtil.getBitmap(getContext(), mIconId).getHeight();
		
		if (mHeight < height) {
			setMeasuredDimension(width, height);
		} else {
			setMeasuredDimension(width, mHeight);
		}
		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if (getId() == BYNavigationManager.getInstance().getSelectedId()) {
			mPaint.setColor(getResources().getColor(R.color.nav_item_selected));
			canvas.drawColor(getResources().getColor(R.color.common_clicked_bg_color));
			mIcon = BYBitmapUtil.getBitmap(getContext(), mSelectedIconId);
		} else {
			mPaint.setColor(Color.BLACK);
			if (isPressed()) {
				canvas.drawColor(getResources().getColor(R.color.common_clicked_bg_color));
			} else {
				canvas.drawColor(Color.WHITE);
			}
			mIcon = BYBitmapUtil.getBitmap(getContext(), mIconId);
		}
		
		
		int offsetX = 0,offsetY = 0;
		offsetX = mIconOffsetLeft;
		offsetY = (getMeasuredHeight() - mIcon.getHeight()) / 2;
		if(mIcon != null) {
			canvas.drawBitmap(mIcon, offsetX, offsetY, mPaint);
		}
		
		offsetX += (mIcon.getWidth() + mIconTextGap);
		offsetY = LeTextUtil.calcYWhenAlignCenter(getMeasuredHeight(), mPaint);
		canvas.drawText(mText, offsetX, offsetY, mPaint);
		
	}
}
