package com.beiying.fitmanager.core.ui;

import android.view.animation.Interpolator;

public class LeDecreaseInterpolator implements Interpolator {

	@Override
	public float getInterpolation(float input) {
		float f1 = input - 1.0F;
		float f2 = f1 * f1 * f1 * f1;
		return f1 * f2 + 1.0F;
	}
}
