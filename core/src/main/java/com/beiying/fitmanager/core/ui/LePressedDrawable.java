package com.beiying.fitmanager.core.ui;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.util.StateSet;

public class LePressedDrawable extends Drawable {
	
	private static final int DURATION = 100;
	
	static final int STATE_PRESSED = 0;

	static final int[][] mStateSets = new int[][] {
		new int[] {android.R.attr.state_pressed}};

	
	private int mCurrentState = -1;
	
	private LeProcessor mProcessor;

	private Drawable mPressedBg;
	
	public LePressedDrawable() {
		mProcessor = new LeProcessor();
		mProcessor.setCurrProcess(0);
	}
	
	public void setPressedBg(Drawable bg) {
		mPressedBg = bg;
	}
    
    @Override
    public boolean isStateful() {
        return true;
    }

	@Override
	protected boolean onStateChange(int[] state) {
		int newState = indexOfStateSet(state);
		if (mCurrentState != STATE_PRESSED && newState == STATE_PRESSED) {
			mProcessor.startProcess(mProcessor.getCurrProcess(), 1, DURATION);
		} else if (mCurrentState == STATE_PRESSED && newState != STATE_PRESSED) {
			mProcessor.startProcess(mProcessor.getCurrProcess(), 0, DURATION);
		}
        mCurrentState = newState;
        invalidateSelf();
		return super.onStateChange(state);
	}
	
    private int indexOfStateSet(int[] stateSet) {
        final int[][] stateSets = mStateSets;
        for (int i = 0; i < stateSets.length; i++) {
            if (StateSet.stateSetMatches(stateSets[i], stateSet)) {
                return i;
            }
        }
        return -1;
    }

	@Override
	public void draw(Canvas canvas) {
		if (mPressedBg != null) {
			mPressedBg.setAlpha((int) (mProcessor.getCurrProcess() * 255));
			mPressedBg.setBounds(getBounds());
			mPressedBg.draw(canvas);
		}
		
		computeAnimation();
	}
	
	private void computeAnimation() {
		if (mProcessor.computeProcessOffset()) {
			invalidateSelf();
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
