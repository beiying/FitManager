package com.beiying.fitmanager.core.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class LeGalleryIndicator extends LeIndicator {
	
	public static final int UI_HEIGHT = 2;
	private static final int DEFAULT_SLIDER_COLOR = 0xff0fb33b;
	private static final int COLOR_BG = 0xffe1e1e1;
	private static final int COLOR_UP_LINE = 0xffe1e1e1;
	private static final int COLOR_DOWN_LINE = 0xffffffff;
	
	private int mSliderHeight;
	private int mSliderGap;
	
	private Paint mUpLinePaint;
	private Paint mDownLinePaint;
	
	private LeGallery mGallery;
	
	private Paint mSliderPaint;

	private int mSliderWidth;
	private int mPaddingX;
	private Rect mSliderRect;
	
	private int mBgColor;
	
	private boolean mHasDecorLine = true;

	public LeGalleryIndicator(Context context) {
		super(context);

		initResources();
	}

	private void initResources() {
		mBgColor = COLOR_BG;
		
		mSliderHeight = UI_HEIGHT;
		mSliderGap = UI_HEIGHT;
		
		mSliderPaint = new Paint();
		mSliderPaint.setColor(DEFAULT_SLIDER_COLOR);
		
		mUpLinePaint = new Paint();
		mUpLinePaint.setColor(COLOR_UP_LINE);
		mUpLinePaint.setStrokeWidth(UI_HEIGHT);
		
		mDownLinePaint = new Paint();
		mDownLinePaint.setColor(COLOR_DOWN_LINE);
		mDownLinePaint.setStrokeWidth(UI_HEIGHT);
		
		mSliderRect = new Rect();
	}
	
	public int getoffsetTop() {
		return (int) (getMeasuredHeight() - mDownLinePaint.getStrokeWidth());
	}
	
	public void setBgColor(int color) {
		mBgColor = color;
	}
	
	public void setUpLineColor(int color) {
		mUpLinePaint.setColor(color);
	}
	
	public void setUpLineHeight(int height) {
		mUpLinePaint.setStrokeWidth(height);
	}
	
	public void setDownLineColor(int color) {
		mDownLinePaint.setColor(color);
	}
	
	public void setDwonLineHeight(int height) {
		mDownLinePaint.setStrokeWidth(height);
	}
	
	public void setSliderColor(int color) {
		mSliderPaint.setColor(color);
	}
	
	public void setSliderWidth(int width) {
		mSliderWidth = width;
	}
	
	public void setSliderHeight(int height) {
		mSliderHeight = height;
	}
	
	public void setSliderGap(int gap) {
		mSliderGap = gap;
	}
	
	public void setGallery(LeGallery gallery) {
		mGallery = gallery;
	}
	
	public void setPaddingX(int paddingX) {
		mPaddingX = paddingX;
	}
	
	public void setHasDecorLine(boolean hasDecorLine) {
		mHasDecorLine = hasDecorLine;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int height = (int) (mSliderHeight + mSliderGap + mUpLinePaint.getStrokeWidth() + mDownLinePaint.getStrokeWidth());
		
		setMeasuredDimension(width, height);
		
		if (mGallery != null && mSliderWidth == 0) {
			int childCount = mGallery.getChildCount();
			mSliderWidth = (width - mPaddingX * 2) / childCount;
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(mBgColor);
		calcSilderRect();
		canvas.drawRect(mSliderRect, mSliderPaint);
		
		if (mHasDecorLine) {
			int offsetY = (int) (getMeasuredHeight() - mUpLinePaint.getStrokeWidth() - mDownLinePaint.getStrokeWidth());
			canvas.drawLine(0, offsetY, getMeasuredWidth(), offsetY, mUpLinePaint);
			offsetY += mUpLinePaint.getStrokeWidth();
			canvas.drawLine(0, offsetY, getMeasuredWidth(), offsetY, mDownLinePaint);
		}
	}
	
	private void calcSilderRect() {
		if (mGallery != null) {
			int childCount = mGallery.getChildCount();
			if (childCount == 0) {
				return;
			}
			
			int indicatorWidth = getMeasuredWidth() - mPaddingX * 2;
			int sliderScrollX = (int) (((float) mGallery.getScrollX()) / mGallery.getMeasuredWidth() * indicatorWidth);
			int sliderOffsetX = indicatorWidth / childCount / 2 + mPaddingX;
			int slideCenterX = sliderOffsetX + sliderScrollX;
			mSliderRect.set(slideCenterX - mSliderWidth / 2, 0, slideCenterX + mSliderWidth / 2, mSliderHeight);
		}
	}
}
