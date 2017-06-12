/** 
 * Filename:    LeTitlebar.java
 * Description:  
 * Copyright:   Lenovo PCL Copyright(c)2013 
 * @author:     chenwei27 
 * @version:    1.0
 * Create at:   2013-8-26 下午2:09:28
 * 
 * Modification History: 
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------ 
 * 2013-8-26     chenwei27    1.0         1.0 Version 
 */
package com.beiying.fitmanager.core.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.View;

import com.beiying.fitmanager.core.LeSafeRunnable;
import com.beiying.fitmanager.core.utils.LeTextUtil;


public class LeTitlebar extends LeView {
	private static final int UI_TITLE_HEIGHT = 46;
	private static final int UI_TITLE_SIZE = 22;
	
	private static final int UI_PADDING_LEFT = 8;
	private static final int UI_ICON_TEXT_GAP = 8;
	
	private static final int COLOR_DEFAULT_TEXT = 0xffffffff;
	
	protected LeIconButton mBackButton;
	
	protected int mPaddingLeft;
	protected int mIconTextGap;
	
	private LeSafeRunnable mBackAction;
	
	public String mTitle;

	protected Paint mPaint;

	public LeTitlebar(Context context, int backResId, String title) {
		super(context);
		
		mTitle = title;
		
		mBackButton = new LeIconButton(context);
		mBackButton.setIcon(context.getResources().getDrawable(backResId));
		mBackButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mBackAction != null) {
					mBackAction.runSafely();
				}
			}
		});
		addView(mBackButton);
		
		setBackgroundColor(Color.TRANSPARENT);
		
		setWillNotDraw(false);
		
		initResources(context);
	}
	
	public void setTitle(String title){
		mTitle = title;
		postInvalidate();
	}
	
	private void initResources(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		
		mPaddingLeft = (int) (dm.density * UI_PADDING_LEFT);
		mIconTextGap = (int) (dm.density * UI_ICON_TEXT_GAP);
		
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(COLOR_DEFAULT_TEXT);
		mPaint.setTextSize(LeUI.getDensityDimen(getContext(), UI_TITLE_SIZE));
	}
	
	public void setBackAction(LeSafeRunnable runnable) {
		mBackAction = runnable;
	}
	
	public void setTitleSize(int size) {
		mPaint.setTextSize(size);
	}
	
	public void setTitleColor(int color) {
		mPaint.setColor(color);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width, height;
		if (widthMeasureSpec == 0) {
			width = 0;
		} else {
			width = MeasureSpec.getSize(widthMeasureSpec);
		}
		if (heightMeasureSpec == 0) {
			height = LeUI.getDensityDimen(getContext(), UI_TITLE_HEIGHT) + getPaddingTop();
		} else {
			height = MeasureSpec.getSize(heightMeasureSpec);
		}
		setMeasuredDimension(width, height);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		
		int offsetX = mPaddingLeft;
		int offsetY = getPaddingTop() + (getMeasuredHeight() - mBackButton.getMeasuredHeight() - getPaddingTop()) / 2;
		LeUI.layoutViewAtPos(mBackButton, offsetX, offsetY);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mTitle != null) {
			int offsetX, offsetY;
			int textWidth = getMeasuredWidth() - mPaddingLeft * 3 - mBackButton.getMeasuredWidth() * 3 - mIconTextGap * 2;
			mTitle = LeTextUtil.getTruncateEndString(mTitle, mPaint, textWidth);
			offsetX = LeTextUtil.calcXWhenAlignCenter(getMeasuredWidth(), mPaint, mTitle);
			offsetY = getPaddingTop() + LeTextUtil.calcYWhenAlignCenter(getMeasuredHeight() - getPaddingTop(), mPaint);
			canvas.drawText(mTitle, offsetX, offsetY, mPaint);
		}
	}
}
