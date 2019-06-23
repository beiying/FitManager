package com.beiying.fitmanager.core.ui;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import com.beiying.fitmanager.core.utils.LePathUtil;


public class LeStateBgDrawable extends LeStateDrawable {
	
	private static final int DURATION = 80;
	private static final int DEFAULT_CORNER_SIZE = 6;
	
	private static int sDuration = DURATION;

	private Drawable mBg;
	private Drawable mPressedBg;
	private Drawable mFocusedBg;
	private Drawable mDisabledBg;
	private Drawable mDisabledFocusedBg;
	private Drawable mSelectedBg;
	
	private LeProcessor mProcessor;
	
	private int mCornerSize;
	private boolean mIsLTCornerRound = false;
	private boolean mIsLBCornerRound = false;
	private boolean mIsRTCornerRound = false;
	private boolean mIsRBCornerRound = false;
	private Paint mCornerPaint;
	
	public LeStateBgDrawable() {
		mProcessor = new LeProcessor();
		
		mCornerSize = DEFAULT_CORNER_SIZE;
		
		mCornerPaint = new Paint();
		mCornerPaint.setAlpha(0);
		mCornerPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		mCornerPaint.setAntiAlias(true);
		mCornerPaint.setStyle(Paint.Style.FILL);
	}
	
	public static void slow() {
		sDuration = DURATION * 5;
	}
	
	public static void cancelSlow() {
		sDuration = DURATION;
	}
	
	public LeStateBgDrawable copy() {
		LeStateBgDrawable dstDrawable = new LeStateBgDrawable();
		
		dstDrawable.setBg(mBg);
		dstDrawable.setPressedBg(mPressedBg);
		dstDrawable.setFocusedBg(mFocusedBg);
		dstDrawable.setDisabledBg(mDisabledBg);
		dstDrawable.setDisabledFocusedBg(mDisabledFocusedBg);
		dstDrawable.setSelectedBg(mSelectedBg);
		dstDrawable.setCornerSize(mCornerSize);
		
		dstDrawable.setIsLBCornerRound(mIsLBCornerRound);
		dstDrawable.setIsLTCornerRound(mIsLTCornerRound);
		dstDrawable.setIsRBCornerRound(mIsRBCornerRound);
		dstDrawable.setIsRTCornerRound(mIsRTCornerRound);
		
		return dstDrawable;
	}
	
	public void setBg(Drawable bg) {
		mBg = bg;
	}
	
	public void setEnabledColor(int color) {
		mBg = new ColorDrawable(color);
	}
	
	public void setPressedBg(Drawable bg) {
		mPressedBg = bg;
	}
	
	public void setPressedColor(int color) {
		mPressedBg = new ColorDrawable(color);
	}
	
	public void setFocusedBg(Drawable bg) {
		mFocusedBg = bg;
	}
	
	public void setFocusedColor(int color) {
		mFocusedBg = new ColorDrawable(color);
	}
	
	public void setDisabledBg(Drawable bg) {
		mDisabledBg = bg;
	}
	
	public void setDisabledColor(int color) {
		mDisabledBg = new ColorDrawable(color);
	}
	
	public void setDisabledFocusedBg(Drawable bg) {
		mDisabledFocusedBg = bg;
	}
	
	public void setDisabledFocusedColor(int color) {
		mDisabledFocusedBg = new ColorDrawable(color);
	}
	
	public void setSelectedBg(Drawable bg) {
		mSelectedBg = bg;
	}
	
	public void setSelectedColor(int color) {
		mSelectedBg = new ColorDrawable(color);
	}
	
	public void setCornerSize(int cornerSize) {
		mCornerSize = cornerSize;
	}
	
	public void setIsLTCornerRound(boolean isLTCornerRound) {
		mIsLTCornerRound = isLTCornerRound;
	}

	public void setIsLBCornerRound(boolean isLBCornerRound) {
		mIsLBCornerRound = isLBCornerRound;
	}

	public void setIsRTCornerRound(boolean isRTCornerRound) {
		mIsRTCornerRound = isRTCornerRound;
	}

	public void setIsRBCornerRound(boolean isRBCornerRound) {
		mIsRBCornerRound = isRBCornerRound;
	}

	@Override
	protected boolean onStateChange(int[] state) {
		int newState = indexOfStateSet(state);
		if (mCurrentState != STATE_PRESSED && newState == STATE_PRESSED) {
			mProcessor.startProcess(mProcessor.getCurrProcess(), 1, sDuration);
		} else if (mCurrentState == STATE_PRESSED && newState != STATE_PRESSED) {
			mProcessor.startProcess(mProcessor.getCurrProcess(), 0, sDuration);
		}
		return super.onStateChange(state);
	}
	
	@Override
	public void draw(Canvas canvas) {
		if (mCurrentState == STATE_DISABLED_FOCUSED) {
			drawDisabledFocused(canvas);
		} else if (mCurrentState == STATE_FOCUSED) {
			drawFocused(canvas);
		} else if (mCurrentState == STATE_SELECTED) {
			drawSelected(canvas);
		} else if (mCurrentState == STATE_ENABLED) {
			drawEnabled(canvas);
		} else {
			drawDisabled(canvas);
		}
		
		drawPressed(canvas);
		computeAnimation();
	}
	
	private void computeAnimation() {
		if (mProcessor.computeProcessOffset()) {
			invalidateSelf();
		}
	}

	@Override
	public void drawPressed(Canvas canvas) {
		if (mPressedBg == null) {
			return;
		}
		
		if (mIsLBCornerRound || mIsLTCornerRound || mIsRBCornerRound || mIsRTCornerRound) {
            int sc = canvas.saveLayer(getBounds().left, getBounds().top, getBounds().right, getBounds().bottom,
            		null, Canvas.ALL_SAVE_FLAG);

			mPressedBg.setAlpha((int) (mProcessor.getCurrProcess() * 255));
			mPressedBg.setBounds(getBounds());
            mPressedBg.draw(canvas);

			Path cornerPath;
			if (mIsLTCornerRound) {
				cornerPath = LePathUtil.createLTCornerReverse(mCornerSize);
				cornerPath.offset(getBounds().left, getBounds().top);
				canvas.drawPath(cornerPath, mCornerPaint);
			}
			if (mIsRTCornerRound) {
				cornerPath = LePathUtil.createRTCornerReverse(mCornerSize);
				cornerPath.offset(getBounds().right - mCornerSize, getBounds().top);
				canvas.drawPath(cornerPath, mCornerPaint);
			}
			if (mIsRBCornerRound) {
				cornerPath = LePathUtil.createRBCornerReverse(mCornerSize);
				cornerPath.offset(getBounds().right - mCornerSize, getBounds().bottom - mCornerSize);
				canvas.drawPath(cornerPath, mCornerPaint);
			}
			if (mIsLBCornerRound) {
				cornerPath = LePathUtil.createLBCornerReverse(mCornerSize);
				cornerPath.offset(getBounds().left, getBounds().bottom - mCornerSize);
				canvas.drawPath(cornerPath, mCornerPaint);
			}
			
			canvas.restoreToCount(sc);
		} else {
			mPressedBg.setAlpha((int) (mProcessor.getCurrProcess() * 255));
			mPressedBg.setBounds(getBounds());
            mPressedBg.draw(canvas);
		}
	}

	@Override
	public void drawDisabledFocused(Canvas canvas) {
		if (mDisabledFocusedBg != null) {
			mDisabledFocusedBg.setBounds(getBounds());
			mDisabledFocusedBg.draw(canvas);
		} else {
			drawEnabled(canvas);
		}
	}

	@Override
	public void drawFocused(Canvas canvas) {
		if (mFocusedBg != null) {
			mFocusedBg.setBounds(getBounds());
			mFocusedBg.draw(canvas);
		} else {
			drawEnabled(canvas);
		}
	}

	@Override
	public void drawEnabled(Canvas canvas) {
		if (mBg != null) {
			mBg.setBounds(getBounds());
			mBg.draw(canvas);
		}
	}

	@Override
	public void drawDisabled(Canvas canvas) {
		if (mDisabledBg != null) {
			mDisabledBg.setBounds(getBounds());
			mDisabledBg.draw(canvas);
		} else {
			drawEnabled(canvas);
		}
	}

	@Override
	public void drawSelected(Canvas canvas) {
		if (mSelectedBg != null) {
			mSelectedBg.setBounds(getBounds());
			mSelectedBg.draw(canvas);
		} else {
			drawEnabled(canvas);
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
