/** 
 * Filename:    LeTextButton.java
 * Description:  
 * Copyright:   Lenovo PCL Copyright(c)2013 
 * @author:     chenwei27 
 * @version:    1.0
 * Create at:   2013-7-18 下午3:03:49
 * 
 * Modification History: 
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------ 
 * 2013-7-18     chenwei27    1.0         1.0 Version 
 */
package com.beiying.fitmanager.core.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;

import com.beiying.fitmanager.core.utils.LeTextUtil;


public class LeTextButton extends LeBgButton {
	
	private static final int UI_BUTTON_TEXT_SIZE = 20;
	
	private static final int UI_WIDTH = 64;
	private static final int UI_HEIGHT = 32;

	private static final int DEFAULT_SHADOW_OFFSET_Y = 0;

	protected String mText;

	protected Paint mPaint;
	
	protected LeStateColor mStateColor;

	private int mShadowOffsetY;
	private int mWidth;
	private int mHeight;
	private int mTextSize;
	
	public LeTextButton(Context context, int textResId) {
		this(context, context.getString(textResId), 0, 0);
	}
	
	public LeTextButton(Context context, String text) {
		this(context, text, 0, 0);
	}
	
	public LeTextButton(Context context, int textResId, int width, int height) {
		this(context, context.getString(textResId), width, height);
	}

	public LeTextButton(Context context, String text, int width, int height) {
		super(context);
		
		mText = text;
		mWidth = width;
		mHeight = height;
		mTextSize = UI_BUTTON_TEXT_SIZE;
		
		initResources();
		
		setWillNotDraw(false);
	}

	private void initResources() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setTextSize(mTextSize);

		mShadowOffsetY = LeUI.getDensityDimen(getContext(), DEFAULT_SHADOW_OFFSET_Y);

		mStateColor = new LeStateColor();
		mStateColor.setEnabledColor(Color.BLACK);
		mStateColor.setPressedColor(Color.BLACK);
		mStateColor.setFocusedColor(DEFAULT_FOCUSED_COLOR);
	}
	
	public void setWidth(int width) {
		mWidth = width;
	}
	
	public void setHeight(int height) {
		mHeight = height;
	}
	
	public void setTextColor(LeStateColor stateColor) {
		mStateColor = stateColor;
		mStateColor.setState(getDrawableState());
	}

	public void setTextColor(int color) {
		mStateColor.setEnabledColor(color);
		mStateColor.setState(getDrawableState());
	}

	public void setTextPressedColor(int color) {
		mStateColor.setPressedColor(color);
		mStateColor.setState(getDrawableState());
	}

	public void setTextFocusedColor(int color) {
		mStateColor.setFocusedColor(color);
		mStateColor.setState(getDrawableState());
	}

	public void setTextDisabledColor(int color) {
		mStateColor.setDisabledColor(color);
		mStateColor.setState(getDrawableState());
	}

	public void setTextSelectedColor(int color) {
		mStateColor.setSelectedColor(color);
		mStateColor.setState(getDrawableState());
	}

	public void setShadowOffsetY(int offsetY) {
		mShadowOffsetY = offsetY;
	}
	
	public String getText() {
		return mText;
	}

	public void setText(int resId) {
		mText = getResources().getString(resId);
		postInvalidate();
	}

	public void setText(String text) {
		mText = text;
		postInvalidate();
	}
	
	public void setTextSize(int size) {
		mTextSize = size;
		mPaint.setTextSize(mTextSize);
	}
	
	@Override
	protected void drawableStateChanged() {
		super.drawableStateChanged();
		
		mStateColor.setState(getDrawableState());
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final DisplayMetrics dm = getResources().getDisplayMetrics();
		int width = (int) (mWidth * dm.density);
		int height = (int) (mHeight * dm.density);
		
		if (width == 0 || height == 0) {
			
			int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
			int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);
			
			if (measuredHeight == 0 || measuredWidth == 0) {
				width = LeUI.getDensityDimen(getContext(), UI_WIDTH);
				height = LeUI.getDensityDimen(getContext(), UI_HEIGHT);
			} else {
				height = measuredHeight;
				width = measuredWidth;
			}
		}
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		int offsetX, offsetY;
		offsetX = LeTextUtil.calcXWhenAlignCenter(getMeasuredWidth(), mPaint, mText);
		offsetY = LeTextUtil.calcYWhenAlignCenter(getMeasuredHeight(), mPaint) + mShadowOffsetY;
		mPaint.setColor(mStateColor.getColor());
		canvas.drawText(mText, offsetX, offsetY, mPaint);
	}

}
