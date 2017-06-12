package com.beiying.fitmanager.core.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class LeIndicator extends View implements LeThemable {
	
	private static final int UI_HEIGHT = 3;
	private static final int DEFAULT_SLIDER_COLOR = 0x330000ff;

	protected int mCurSelect = 0;
	protected int mCount = 0;
	protected int mIndicatorHeight;
	protected int mSliderWidth;

	private int mSliderColor;
	
	private Paint mPaint;
	
	public LeIndicator(Context context) {
		super(context);
		
		mSliderColor = DEFAULT_SLIDER_COLOR;
		
		mPaint = new Paint();
		
		setClickable(true);
	}

	public void init(int selected, int count) {
		mCurSelect = selected;
		mCount = count;

		checkVisible();
		requestLayout();
	}

	public void addCount() {
		mCount++;
		checkVisible();
		requestLayout();
	}

	public void subCount() {
		mCount--;
		checkVisible();
		requestLayout();
	}

	public void setSelectIndex(int selected) {
		mCurSelect = selected;
		invalidate();
	}

	public void setSliderColor(int sliderColor) {
		mSliderColor = sliderColor;
	}
	
	public void setIndicatorHeight(int height) {
		mIndicatorHeight = height;
	}
	
	public void setSliderWidth(int width) {
		mSliderWidth = width;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = mIndicatorHeight;
		if (height == 0) {
			height = LeUI.getDensityDimen(getContext(), UI_HEIGHT);
		}

		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mCount != 0) {
			final int itemWidth = getWidth() / mCount;
			int sliderWidth = mSliderWidth;
			if (sliderWidth == 0) {
				sliderWidth = itemWidth;
			}
			int offsetX = itemWidth * mCurSelect + (itemWidth - sliderWidth) / 2;
			mPaint.setColor(mSliderColor);
			canvas.drawRect(offsetX, 0, offsetX + sliderWidth, getMeasuredHeight(), mPaint);
		}

		super.onDraw(canvas);
	}

	private void checkVisible() {
		if (mCount <= 1) {
			setVisibility(View.INVISIBLE);
		} else {
			setVisibility(View.VISIBLE);
		}
	}

    @Override
    public void onThemeChanged() {

    }
}
