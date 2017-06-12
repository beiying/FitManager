package com.beiying.fitmanager.core.ui;

import android.view.animation.Interpolator;

public class LeInOutInterpolator implements Interpolator {

	private float mPower;

	public LeInOutInterpolator(float aPower) {
		mPower = aPower;
		if (mPower < 1) {
			mPower = 1;
		}
	}

	@Override
	public float getInterpolation(float input) {
		if (input < 0.5) {
			return (float) Math.pow(input, mPower);
		} else {
			return (float) Math.pow(input, 1 / mPower);
		}
	}

}
