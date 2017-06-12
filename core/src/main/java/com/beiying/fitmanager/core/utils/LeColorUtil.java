package com.beiying.fitmanager.core.utils;

import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;

import java.util.Random;

public class LeColorUtil {

	private static final float DEFUALT_NIGHT_DARK_RATIO = 0.4f;
	
	private static ColorMatrixColorFilter sNightColorFilter;
	
	private LeColorUtil(){}
	
	public static Integer parseHexColor(String colorStr) {
		Integer color = null;
		try {
			color = new Integer((int) Long.parseLong(colorStr, 16));
		} catch (NumberFormatException e) {
		}
		return color;
	}
	
	public static ColorMatrixColorFilter getNightColorFilter() {
		if (sNightColorFilter == null) {
			syncInitNightColorFilter();
		}
		return sNightColorFilter;
	}
	
	private static synchronized void syncInitNightColorFilter() {
		if (sNightColorFilter == null) {
			sNightColorFilter = createNightColorFilter();
		}
	}

	/**
	 * 得到一个夜间模式变暗的ColorFilter，一般用于位图的变暗
	 */
	private static ColorMatrixColorFilter createNightColorFilter() {
		return createDarkerColorFilter(DEFUALT_NIGHT_DARK_RATIO);
	}

	public static ColorMatrixColorFilter createDarkerColorFilter(
			float darkRatio) {
		final float[] array = new float[] { darkRatio, 0, 0, 0, 0, 0,
				darkRatio, 0, 0, 0, 0, 0, darkRatio, 0, 0, 0, 0, 0, 1, 0 };
		return new ColorMatrixColorFilter(array);
	}

	public static ColorMatrixColorFilter createRGBColorFilter(int red,
															  int green, int blue) {
		final float[] array = new float[] { 0, 0, 0, 0, red, 0, 0, 0, 0,
				green, 0, 0, 0, 0, blue, 0, 0, 0, 1, 0 };
		return new ColorMatrixColorFilter(array);
	}

	public static ColorMatrixColorFilter createRGBColorFilter(int red,
															  int green, int blue, float alpha) {
		final float[] array = new float[] { 0, 0, 0, 0, red, 0, 0, 0, 0,
				green, 0, 0, 0, 0, blue, 0, 0, 0, alpha, 0 };
		return new ColorMatrixColorFilter(array);
	}

	public static ColorMatrixColorFilter createColorFilterByColor(int color) {
		final int a = (color >> 24) & 255;
		final int r = (color >> 16) & 255;
		final int g = (color >> 8) & 255;
		final int b = color & 255;
		float alpha = a / 255.0f ;
		final float[] array = new float[] { 0, 0, 0, 0, r, 0, 0, 0, 0, g, 0, 0,
				0, 0, b, 0, 0, 0, alpha, 0 };
		return new ColorMatrixColorFilter(array);
	}
	
	public static ColorMatrixColorFilter createColorFilterByAlpha(float alpha) {
		final float[] array = new float[] { 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0,
				1, 0, 0, 0, 0, 0, alpha, 0 };
		return new ColorMatrixColorFilter(array);
	}

	public static ColorMatrixColorFilter createColorChangeFilter(int newColor) {
		final int r = (newColor >> 16) & 255;
		final int g = (newColor >> 8) & 255;
		final int b = newColor & 255;
		float rRatio = ((float) r) / 255.0f;
		float gRatio = ((float) g) / 255.0f;
		float bRatio = ((float) b) / 255.0f;
		final float[] array = new float[] { 
				rRatio, 0, 0, 0, 0, 
				0, gRatio, 0, 0, 0, 
				0, 0, bRatio, 0, 0, 
				0, 0, 0, 1, 0 };
		return new ColorMatrixColorFilter(array);
	}
	
	public static int getRamdonColor() {
		Random random = new Random(System.currentTimeMillis());
		final int r = random.nextInt() % 255;
		final int g = random.nextInt() % 255;
		final int b = random.nextInt() % 255;
		int color = (r << 16) | (g << 8) | b;
		return color;
	}
	
	public static int toGray(int color) {
		final int r = (color >> 16) & 255;
		final int g = (color >> 8) & 255;
		final int b = color & 255;
		int gray = (int) (r * 0.3 + g * 0.6 + b * 0.1);
		return gray;
	}
	
	public static int blendColor(int bgcolor, int fgcolor, float alpha) {
		float a = Color.alpha(fgcolor) * alpha / 255;
		int cr = (int) (a * Color.red(fgcolor) + (1 - a) * Color.red(bgcolor));
		int cg = (int) (a * Color.green(fgcolor) + (1 - a) * Color.green(bgcolor));
		int cb = (int) (a * Color.blue(fgcolor) + (1 - a) * Color.blue(bgcolor));
		return Color.argb(255, cr, cg, cb);
	}
	
	public static int darkColor(int oldColor) {
		return blendColor(oldColor, Color.BLACK, 0.2f);
	}
}
