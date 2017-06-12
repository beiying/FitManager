package com.beiying.fitmanager.core.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.beiying.fitmanager.core.utils.LeTextUtil;


public class LeTabButton extends LeButton {
	
	private static final int UI_TEXT = 17;
	
	protected String mTitle;
	protected Paint mPaint;
	
	private int mBgPressColor;
	private int mTextSelectColor;
	private int mTextCommonColor;
	
	public LeTabButton(Context context) {
		this(context, Color.WHITE);
	}
	
	public LeTabButton(Context context, int color) {
		this(context, color, color);
	}
	
	public LeTabButton(Context context, int CommonColor, int selectColor) {
		super(context);
		
		mTextCommonColor = CommonColor;
		mTextSelectColor = selectColor;
		
		initResource();
	}
	
	private void initResource() {
		mBgPressColor = Color.TRANSPARENT;
		
		mPaint = new Paint();
		mPaint.setTextSize(LeUI.getDensityDimen(getContext(), UI_TEXT));
		mPaint.setColor(mTextCommonColor);
		mPaint.setAntiAlias(true);
	}
	
	public void setTextSize(int textSize) {
		mPaint.setTextSize(textSize);
		invalidate();
	}
	
	public void setBgPressColor(int color) {
		mBgPressColor = color;
	}
	
	public void setTextSelectColor(int color) {
		mTextSelectColor = color;
		if (isSelected()) {
			mPaint.setColor(mTextSelectColor);
			invalidate();
		}
	}
	
	public void setTextCommonColor(int color) {
		mTextCommonColor = color;
		if (!isSelected()) {
			mPaint.setColor(mTextCommonColor);
			invalidate();
		}
	}
	
	@Override
	public void setSelected(boolean selected) {
		if (selected) {
			mPaint.setColor(mTextSelectColor);
		} else {
			mPaint.setColor(mTextCommonColor);
		}
		super.setSelected(selected);
	}

	public String getTitle() {
		return mTitle;
	}
	
	public void setTitle(int resId) {
		this.setTitle(getContext().getString(resId));
	}

	public void setTitle(String title) {
		mTitle = title;
		postInvalidate();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int height = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (isPressed()) {
			canvas.drawColor(mBgPressColor);
		} 
		
		if (mTitle != null) {
			int offsetX, offsetY;
			offsetX = LeTextUtil.calcXWhenAlignCenter(getMeasuredWidth(), mPaint, mTitle);
			offsetY = LeTextUtil.calcYWhenAlignCenter(getMeasuredHeight(), mPaint);
			canvas.drawText(mTitle, offsetX, offsetY, mPaint);
		}
	}
}
