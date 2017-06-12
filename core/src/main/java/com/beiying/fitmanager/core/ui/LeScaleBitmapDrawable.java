package com.beiying.fitmanager.core.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class LeScaleBitmapDrawable extends Drawable {
	
	private Bitmap mBitmap;
	
	private Paint mPaint;
	
	private Rect mDstRect = new Rect();
	
	public LeScaleBitmapDrawable(String filepath) {
		this(BitmapFactory.decodeFile(filepath));
	}
	
	public LeScaleBitmapDrawable(Bitmap bitmap) {
		mBitmap = bitmap;
	}
	
	public Bitmap getBitmap() {
		return mBitmap;
	}
	
	public void setPaint(Paint paint) {
		mPaint = paint;
	}

	@Override
	public void draw(Canvas canvas) {
		if (mBitmap == null) {
			return;
		}
		
		if (getBounds().width() == mBitmap.getWidth() && getBounds().height() == mBitmap.getHeight()) {
			canvas.drawBitmap(mBitmap, getBounds().left, getBounds().top, mPaint);
		} else {
			final int dstHeight = getBounds().width() * mBitmap.getHeight() / mBitmap.getWidth();
			mDstRect.set(getBounds().left, getBounds().top, getBounds().right, getBounds().top + dstHeight);
			canvas.drawBitmap(mBitmap, null, mDstRect, mPaint);
		}
	}

	@Override
	public void setAlpha(int alpha) {
		
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		
	}

	@Override
	public int getOpacity() {
		return 0;
	}

}
