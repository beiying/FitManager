package com.beiying.fitmanager.core.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.StateSet;

public abstract class LeDrawable extends Drawable {
	
	static final int STATE_PRESSED = 0;
	static final int STATE_FOCUSED = 1;
	static final int STATE_DISABLED_FOCUSED = 2;
	static final int STATE_ENABLED = 3;
	
	static final int[][] mStateSets = new int[][] {
		new int[] {android.R.attr.state_pressed},
		new int[] {android.R.attr.state_focused, android.R.attr.state_enabled}, 
		new int[] {android.R.attr.state_focused}, 
		new int[] {android.R.attr.state_enabled}};
	
	protected Context mContext;
	private int mCurrentState = -1;
	
	public LeDrawable(Context context) {
		mContext = context;
	}
    
    @Override
    public boolean isStateful() {
        return true;
    }

	@Override
	protected boolean onStateChange(int[] state) {
        mCurrentState = indexOfStateSet(state);
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
		if (mCurrentState == STATE_PRESSED) {
			drawPressed(canvas);
		} else if (mCurrentState == STATE_DISABLED_FOCUSED) {
			drawDisabledFocused(canvas);
		} else if (mCurrentState == STATE_FOCUSED) {
			drawFocused(canvas);
		} else if (mCurrentState == STATE_ENABLED) {
			drawEnabled(canvas);
		} else {
			drawDisabled(canvas);
		}
	}
	
	public abstract void drawPressed(Canvas canvas);
	public abstract void drawDisabledFocused(Canvas canvas);
	public abstract void drawFocused(Canvas canvas);
	public abstract void drawEnabled(Canvas canvas);
	public abstract void drawDisabled(Canvas canvas);
}
