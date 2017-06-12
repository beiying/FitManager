package com.beiying.fitmanager.core.ui;

import android.view.animation.Interpolator;

public class LeLinearInterpolator implements Interpolator {

	public LeLinearInterpolator() {
		
	}

	@Override
	public float getInterpolation(float input) {
		return input;
	}

}
