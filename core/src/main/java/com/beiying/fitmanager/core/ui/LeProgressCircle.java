package com.beiying.fitmanager.core.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.RectF;
import android.view.View;

import com.beiying.fitmanager.core.LeLog;


public class LeProgressCircle extends View implements LeThemable {

	// 画实心圆的画笔  
	protected Paint mCirclePaint;
	// 画圆环的画笔  
	protected Paint mRingPaint;
	// 画字体的画笔  
	protected Paint mTextPaint;
	// 圆形颜色  
	protected int mCircleColor;
	// 圆环颜色  
	protected int mRingColor;
	// 半径  
	protected float mRadius;
	// 圆环半径  
	protected float mRingRadius;
	// 圆环宽度  
	protected float mStrokeWidth;
	// 圆心x坐标  
	protected int mXCenter;
	// 圆心y坐标  
	protected int mYCenter;
	// 字的长度  
	protected float mTxtWidth;
	// 字的高度  
	protected float mTxtHeight;
	// 总进度  
	protected int mTotalProgress = 100;
	// 当前进度  
	protected int mProgress;

	public LeProgressCircle(Context context, int radius, int strokeWidth, int circleColor, int ringColor) {
		super(context);
		mRadius = radius;
		mStrokeWidth =  strokeWidth;
		mCircleColor = circleColor;
		mRingColor = ringColor;
		initResources(context);
	}

	protected void initResources(Context context) {
		

		mRingRadius = mRadius + mStrokeWidth / 2;
		
		mCirclePaint = new Paint();
		mCirclePaint.setAntiAlias(true);
		mCirclePaint.setColor(mCircleColor);
		mCirclePaint.setStyle(Paint.Style.FILL);

		mRingPaint = new Paint();
		mRingPaint.setAntiAlias(true);
		mRingPaint.setColor(mRingColor);
		mRingPaint.setStyle(Paint.Style.STROKE);
		mRingPaint.setStrokeWidth(mStrokeWidth);

		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setStyle(Paint.Style.FILL);
		mTextPaint.setARGB(255, 255, 255, 255);
		mTextPaint.setTextSize(mRadius / 2);

		FontMetrics fm = mTextPaint.getFontMetrics();
		mTxtHeight = (int) Math.ceil(fm.descent - fm.ascent);

	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int size = (int) ((mRadius + mStrokeWidth) * 2);
		setMeasuredDimension(size, size);
		
		LeLog.i("circle size:" + size);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		mXCenter = getWidth() / 2;
		mYCenter = getHeight() / 2;

		canvas.drawCircle(mXCenter, mYCenter, mRadius, mCirclePaint);

		if (mProgress > 0) {
			RectF oval = new RectF();
			oval.left = (mXCenter - mRingRadius);
			oval.top = (mYCenter - mRingRadius);
			oval.right = mRingRadius * 2 + (mXCenter - mRingRadius);
			oval.bottom = mRingRadius * 2 + (mYCenter - mRingRadius);
			canvas.drawArc(oval, -90, ((float) mProgress / mTotalProgress) * 360, false, mRingPaint); //  
			//	          canvas.drawCircle(mXCenter, mYCenter, mRadius + mStrokeWidth / 2, mRingPaint);  
			String txt = mProgress + "%";
			mTxtWidth = mTextPaint.measureText(txt, 0, txt.length());
			canvas.drawText(txt, mXCenter - mTxtWidth / 2, mYCenter + mTxtHeight / 4, mTextPaint);
		}
	}

	public void setProgress(int progress) {
		mProgress = progress;
		//	      invalidate();  
		postInvalidate();
	}
	
	public void setCircleColor(int color) {
		mCircleColor = color;
		mCirclePaint.setColor(mCircleColor);
		LeLog.i("bubble circle set color:" + mCircleColor);
	}

    @Override
    public void onThemeChanged() {

    }
}