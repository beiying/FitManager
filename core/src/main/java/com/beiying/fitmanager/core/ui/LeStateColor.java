package com.beiying.fitmanager.core.ui;

import android.util.StateSet;

public class LeStateColor {
	
	static final int STATE_PRESSED = 0;
	static final int STATE_FOCUSED = 1;
	static final int STATE_DISABLED_FOCUSED = 2;
	static final int STATE_SELECTED = 3;
	static final int STATE_ENABLED = 4;

	static final int[][] mStateSets = new int[][] {
		new int[] {android.R.attr.state_pressed},
		new int[] {android.R.attr.state_focused, android.R.attr.state_enabled}, 
		new int[] {android.R.attr.state_focused},
		new int[] {android.R.attr.state_selected, android.R.attr.state_enabled},
		new int[] {android.R.attr.state_enabled}};

	
	protected int mCurrentState = -1;
	
	private int mColor = 0;
	private int mEnabledColor = 0;
	private int mPressedColor = 0;
	private int mSelectedColor = 0;
	private int mFocusedColor = 0;
	private int mDisabledColor = 0;
	private int mDisabledFocusedColor = 0;
	
	public int getColor() {
		return mColor;
	}
	
	public void setEnabledColor(int color) {
		mEnabledColor = color;
	}
	
	public void setPressedColor(int color) {
		mPressedColor = color;
	}
	
	public void setSelectedColor(int color) {
		mSelectedColor = color;
	}
	
	public void setFocusedColor(int color) {
		mFocusedColor = color;
	}
	
	public void setDisabledColor(int color) {
		mDisabledColor = color;
	}
	
	public void setDisabledFocusedColor(int color) {
		mDisabledFocusedColor = color;
	}
	
    public boolean setState(final int[] stateSet) {
    	return onStateChange(stateSet);
    }

	protected boolean onStateChange(int[] state) {
        mCurrentState = indexOfStateSet(state);

		if (mCurrentState == STATE_DISABLED_FOCUSED) {
			mColor = mDisabledFocusedColor;
		} else if (mCurrentState == STATE_FOCUSED) {
			mColor = mFocusedColor;
		} else if (mCurrentState == STATE_SELECTED) {
			mColor = mSelectedColor;
		} else if (mCurrentState == STATE_PRESSED) {
			mColor = mPressedColor;
		} else if (mCurrentState == STATE_ENABLED) {
			mColor = mEnabledColor;
		} else {
			mColor = mDisabledColor;
		}
		return true;
	}
	
    protected int indexOfStateSet(int[] stateSet) {
        final int[][] stateSets = mStateSets;
        for (int i = 0; i < stateSets.length; i++) {
            if (StateSet.stateSetMatches(stateSets[i], stateSet)) {
                return i;
            }
        }
        return -1;
    }

}
