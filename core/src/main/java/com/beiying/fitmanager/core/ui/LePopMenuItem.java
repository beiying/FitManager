package com.beiying.fitmanager.core.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.beiying.fitmanager.core.LeLanguager;
import com.beiying.fitmanager.core.utils.LeTextUtil;
//xbb

public class LePopMenuItem extends LeButton {
	public static final int UI_HEIGHT = 42;
	public static final int UI_MARGIN = 5;
	
	private static int UI_WIDTH;// = 129;//xbb
	private static final int UI_TEXT = 15;
	private static final int UI_TEXT_PADDING = 32;
	private static final int UI_PADDING_LEFT = 12;
	
	private static final int COLOR_DEFAULT_SUB_TEXT = 0xffffffff;
	private static final int COLOR_DEFAULT_PRESS = 0x1a000000;
	
	protected int mMarginLeft;
	protected int mMarginTop;
	protected int mMarginRight;
	protected int mMarginBottom;
	
	private int mPaddingLeft;
	private int mTextPadding;
	
	protected String mTitle = "";
	private Paint mPaint;
	
	public LePopMenuItem(Context context) {
		super(context);
		
		initResources();
	}
	
	private int getPopMenuItemWidth(Context context){
		int WIDTH;
		if(LeLanguager.isEnglish(context)){
			WIDTH = 189;
		}else{
			WIDTH = 129;
		}
		return WIDTH;
	}
		
	private void initResources() {
		mMarginLeft = LeUI.getDensityDimen(getContext(), UI_MARGIN);
		mMarginTop = LeUI.getDensityDimen(getContext(), UI_MARGIN);
		mMarginRight = LeUI.getDensityDimen(getContext(), UI_MARGIN);
		mMarginBottom = LeUI.getDensityDimen(getContext(), UI_MARGIN);
		mPaddingLeft = LeUI.getDensityDimen(getContext(), UI_PADDING_LEFT);
		mTextPadding = LeUI.getDensityDimen(getContext(), UI_TEXT_PADDING);
		
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(COLOR_DEFAULT_SUB_TEXT);
		mPaint.setTextSize(LeUI.getDensityDimen(getContext(), UI_TEXT));
		
	}

	public int getMarginLeft() {
		return mMarginLeft;
	}

	public int getMarginTop() {
		return mMarginTop;
	}

	public int getMarginRight() {
		return mMarginRight;
	}

	public int getMarginBottom() {
		return mMarginBottom;
	}
	
	public Paint getPaint() {
		return mPaint;
	}
	
	public String getTitle() {
		return mTitle;
	}
	
	public void setTitle(int resId) {
		mTitle = getResources().getString(resId);
	}

	protected void recyclePopMenuItem() {

	}
	
	public int getTextWidth() {
		int textwidth = 0;
		if (mTitle != null && mPaint != null) {
			textwidth = (int) (mPaint.measureText(mTitle) + mTextPadding);
		}
		return textwidth;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		UI_WIDTH = getPopMenuItemWidth(getContext());
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int textwidth = getTextWidth();
		if (textwidth > width) {
			width = textwidth;
		}
		final int height = LeUI.getDensityDimen(getContext(), UI_HEIGHT);
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (isPressed()) {
			canvas.drawColor(COLOR_DEFAULT_PRESS);
		}
		
		int offsetX, offsetY;
		offsetX = mPaddingLeft;
		offsetY = LeTextUtil.calcYWhenAlignCenter(getMeasuredHeight(), mPaint);
		canvas.drawText(mTitle, offsetX, offsetY, mPaint);
	}
}
