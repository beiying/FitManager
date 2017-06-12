package com.beiying.fitmanager.core.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class LeUIUtils {
	/**
	 * 获取圆角图片
	 * @param bitmap
	 * @param roundPixels
	 * @return
	 */
	public static Bitmap getRoundCornerBitmap(Bitmap bitmap, int roundPixels) {
		Bitmap roundCornerBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(roundCornerBitmap);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		//绘制圆角
		Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		RectF rectF = new RectF(rect);
		canvas.drawRoundRect(rectF, roundPixels, roundPixels, paint);
		//绘制图片
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, null, rect, paint);
		return roundCornerBitmap;
	}
}
