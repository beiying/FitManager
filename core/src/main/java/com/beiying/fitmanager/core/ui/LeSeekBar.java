package com.beiying.fitmanager.core.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class LeSeekBar extends LeButton {

	public enum LeSeekBarStyle {
		LINE,
		ROUND_RECT
	}

	private static final int UI_THUMB_SIZE = 26;
	private static final int UI_TRACE_HEIGHT = 3;
	private static final int UI_SELECT_HEIGHT = 3;
	private static final int UI_DEFAULT_WIDTH = 50;
	private static final int UI_SPACE = 2;
	
	private static final int COLOR_TRACE = 0x33ffffff;
	private static final int COLOR_SELECTED = 0xff2cadf1;

	private static final float DEFAULT_MAX = 100f;

	private int mUISpace;
	private int mUITraceHeight;
	private int mDefaultWidth;
	private int mUISelectHeight;

	protected Bitmap mThumb;
	protected Bitmap mThumbFocus;
	protected float mMax;
	protected float mProgress;
	protected float mSecondaryProgress;
	private Paint mPaint;
	/**
	 * provide two styles: line style round rect style
	 */
	private LeSeekBarStyle mStyle;
	private LeSeekBarChangeListener mListener;
	protected Rect mDrawRect;

	public LeSeekBar(Context aContext) {
		this(aContext, LeSeekBarStyle.LINE);
	}

	public LeSeekBar(Context aContext, LeSeekBarStyle style) {
		super(aContext);
		
		setClickable(true);
		mStyle = style;
		initResource();
	}
	
	private void initResource() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mMax = DEFAULT_MAX;
		mProgress = 0;
		mSecondaryProgress = 0;
		mThumb = createDefaultThumb();
		mDrawRect = new Rect();

		mUISpace = LeUI.getDensityDimen(getContext(), UI_SPACE);
		mUITraceHeight = LeUI.getDensityDimen(getContext(), UI_TRACE_HEIGHT);
		mDefaultWidth = LeUI.getDensityDimen(getContext(), UI_DEFAULT_WIDTH);
		mUISelectHeight = LeUI.getDensityDimen(getContext(), UI_SELECT_HEIGHT);
	}
	
	private Bitmap createDefaultThumb() {
		final int thumbSize = LeUI.getDensityDimen(getContext(), UI_THUMB_SIZE);
		Bitmap thumb = Bitmap.createBitmap(thumbSize, thumbSize, Config.ARGB_8888);
		Canvas canvas = new Canvas(thumb);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(0x9925a5f2);
		paint.setStyle(Style.FILL);
		canvas.drawCircle((thumbSize / 2.0f), (thumbSize / 2.0f), (thumbSize / 2.0f), paint);
		final int thumbInnerSize = LeUI.getDensityDimen(getContext(), 14);
		paint.setColor(0xffffffff);
		canvas.drawCircle((thumbSize / 2.0f), (thumbSize / 2.0f), (thumbInnerSize / 2.0f), paint);
		return thumb;
	}

	public void setMax(float max) {
		mMax = max;
	}

	public void setProgress(int progress) {
		setProgress((float) progress);
	}

	private void setProgress(float progress) {
		mProgress = progress;
		invalidate();
	}

	public int getProgress() {
		return (int) mProgress;
	}
	
	public void setSecondaryProgress(int progress) {
		mSecondaryProgress = progress * 1.0f;
		invalidate();
	}

	public void setOnSeekBarChangeListener(LeSeekBarChangeListener listener) {
		mListener = listener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		if (x > mDrawRect.left && x < mDrawRect.right) {
			setProgress((x - mDrawRect.left) * mMax / (mDrawRect.right - mDrawRect.left));
			if (mListener != null) {
				mListener.onProgressChanged(this, (int) mProgress, true);
			}
		}
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (mListener != null) {
					mListener.onStartTrackingTouch(this);
				}
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				if (mListener != null) {
					mListener.onStopTrackingTouch(this);
				}
				break;
			default:
				break;
		}
		return super.onTouchEvent(event);
	}
	
	public boolean dispatchKeyEvent(KeyEvent event) {
		return super.dispatchKeyEvent(event)|| executeKeyEvent(event);
	}
	
	public boolean executeKeyEvent(KeyEvent event) {
		boolean handle = true;
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (event.getKeyCode()) {
				case KeyEvent.KEYCODE_DPAD_RIGHT:
					int x = getProgress();
					x = Math.min(100, x+10);
						setProgress(x);
						if (mListener != null) {
							mListener.onProgressChanged(this, (int) mProgress, true);
						}
					break;
				case KeyEvent.KEYCODE_DPAD_LEFT:
					x = getProgress();
					x = Math.max(0, x-10);
						setProgress(x);
						if (mListener != null) {
							mListener.onProgressChanged(this, (int) mProgress, true);
						}
					break;
				default:
					handle = false;
					break;
			}
		}
		return handle;
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//specifyPadding();
		int measuredHeight = measureHeight(heightMeasureSpec);
		int measuredWidth = measureWidth(widthMeasureSpec);
		setMeasuredDimension(measuredWidth, measuredHeight);
		computeDrawRect();
	}

	/***
	 * modify the value of the padding for this view called when measure
	 */
	private void specifyPadding() {
		int leftPadding = getPaddingLeft();
		if (leftPadding <= 0)
			leftPadding = mUISpace;
		int topPadding = getPaddingTop();
		if (topPadding <= 0)
			topPadding = mUISpace;
		int rightPadding = getPaddingRight();
		if (rightPadding <= 0)
			rightPadding = mUISpace;
		int bottomPadding = getPaddingBottom();
		if (bottomPadding <= 0)
			bottomPadding = mUISpace;
		setPadding(leftPadding, topPadding, rightPadding, bottomPadding);
	}

	/**
	 * compute the rect for draw the line
	 */
	private void computeDrawRect() {
		mDrawRect.left = getPaddingLeft();
		switch (mStyle) {
			case LINE:
				mDrawRect.top = (getMeasuredHeight() - mUITraceHeight + getPaddingTop() - getPaddingBottom()) >> 1;
				mDrawRect.bottom = mDrawRect.top + mUITraceHeight;
				break;
			case ROUND_RECT:
				mDrawRect.top = (getMeasuredHeight() - mUISelectHeight + getPaddingTop() - getPaddingBottom()) >> 1;
				mDrawRect.bottom = mDrawRect.top + mUISelectHeight;
				break;
		}
		mDrawRect.right = getMeasuredWidth() - getPaddingRight();
	}

	private int measureWidth(int widthMeasureSpec) {
		int specMode = MeasureSpec.getMode(widthMeasureSpec);
		int specSize = MeasureSpec.getSize(widthMeasureSpec);
		int width = mThumb.getWidth() + getPaddingLeft() + getPaddingRight() + mDefaultWidth;
		if (specMode == MeasureSpec.AT_MOST) {
			if (width - mDefaultWidth > specSize) {
				float scale = (float) (specSize - getPaddingLeft() - getPaddingRight()) / mThumb.getWidth();
				scaleThumb(scale);
				width = specSize;
			}
		} else if (specMode == MeasureSpec.EXACTLY) {
			if (width - mDefaultWidth > specSize) {
				float scale = (float) (specSize - getPaddingLeft() - getPaddingRight()) / mThumb.getHeight();
				scaleThumb(scale);
			}
			width = specSize;
		}
		return width;
	}

	private int measureHeight(int heightMeasureSpec) {
		int specMode = MeasureSpec.getMode(heightMeasureSpec);
		int specSize = MeasureSpec.getSize(heightMeasureSpec);
		int height = mThumb.getHeight() + getPaddingTop() + getPaddingBottom();
		if (specMode == MeasureSpec.AT_MOST) {
			if (height > specSize) {
				float scale = (float) (specSize - getPaddingTop() - getPaddingBottom()) / mThumb.getHeight();
				scaleThumb(scale);
				height = specSize;
			}
		} else if (specMode == MeasureSpec.EXACTLY) {
			if (height > specSize) {
				float scale = (float) (specSize - getPaddingTop() - getPaddingBottom()) / mThumb.getHeight();
				scaleThumb(scale);
			}
			height = specSize;
		}
		return height;
	}

	private void scaleThumb(float scale) {
		if (scale <= 0)
			return;
		Matrix m = new Matrix();
		m.postScale(scale, scale);
		mThumb = Bitmap.createBitmap(mThumb, 0, 0, mThumb.getWidth(), mThumb.getHeight(), m, true);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		switch (mStyle) {
			case LINE:
				mPaint.setColor(COLOR_TRACE);
				canvas.drawRect(mDrawRect, mPaint);
				
				int drawSecondaryProgress = (int) (mSecondaryProgress * (mDrawRect.right - mDrawRect.left) / mMax);
				mDrawRect.top += mUITraceHeight - mUISelectHeight >> 1;
				mDrawRect.bottom = mDrawRect.top + mUISelectHeight;
				mDrawRect.right = (int) (mDrawRect.left + drawSecondaryProgress);
				mPaint.setColor(0xffa8afb1);
				canvas.drawRect(mDrawRect, mPaint);
				
				mDrawRect.right = getMeasuredWidth() - getPaddingRight();
				int mDrawProgress = (int) (mProgress * (mDrawRect.right - mDrawRect.left) / mMax);
				mDrawRect.right = mDrawRect.left + mDrawProgress;
				mPaint.setColor(COLOR_SELECTED);
				canvas.drawRect(mDrawRect, mPaint);
				
				float top = mDrawRect.top + (mUISelectHeight - mThumb.getHeight() >> 1);
				canvas.drawBitmap(mThumb, mDrawRect.right - (mThumb.getWidth() >> 1), top, null);
				break;
			case ROUND_RECT:
				mPaint.setColor(COLOR_TRACE);
				canvas.drawRect(mDrawRect, mPaint);
				mPaint.setColor(COLOR_SELECTED);
				int mDrawProgressF = (int) (mProgress * (mDrawRect.right - mDrawRect.left) / mMax);
				mDrawRect.right = mDrawRect.left + mDrawProgressF;
				canvas.drawRect(mDrawRect, mPaint);
				float topF = (int) (mDrawRect.top + (mUISelectHeight - mThumb.getHeight() >> 1));
				canvas.drawBitmap(mThumb, mDrawRect.right - (mThumb.getWidth() >> 1), topF, null);
				break;
		}
		computeDrawRect();
	}
	
	public interface LeSeekBarChangeListener {
		void onProgressChanged(LeSeekBar seekBar, int progress, boolean fromUser);
		void onStartTrackingTouch(LeSeekBar seekBar);
		void onStopTrackingTouch(LeSeekBar seekBar);
	}
}
