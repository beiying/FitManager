package com.beiying.fitmanager.core.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Base64;
import android.view.View;

import com.beiying.fitmanager.core.INoProGuard;
import com.beiying.fitmanager.core.LeLog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class LeBitmapUtil implements INoProGuard {
	
	private LeBitmapUtil() {}
	
	public static String bitmapToBase64(Bitmap bitmap) {
		String result = null;
		ByteArrayOutputStream baos = null;
		try {
			if (bitmap != null) {
				baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

				baos.flush();
				baos.close();

				byte[] bitmapBytes = baos.toByteArray();
				result = "data:image/jpg;base64," + Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.flush();
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	

	public static Bitmap stringtoBitmap(String string) {
		Bitmap bitmap = null;
		try {
			LeUtils.markBegin();
			byte[] bitmapArray;
			if (string.startsWith("data:image/gif;base64,")) {
				string = string.substring(22);
			} else if (string.startsWith("data:image/png;base64,")) {
				string = string.substring(22);
			} else if (string.startsWith("data:image/jpg;base64,")) {
				string = string.substring(22);
			} else if (string.startsWith("data:image/jpeg;base64,")) {
				string = string.substring(23);
			} else if (string.startsWith("data:image/x-icon;base64,")) {
				string = string.substring(24);
			}
			bitmapArray = Base64.decode(string, Base64.DEFAULT);
			bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
			LeUtils.markEnd("gyy:decode----");
		} catch (Exception e) {
			LeLog.e(e.toString());
		}
		return bitmap;
	}
	
	public static int getAvgColor(Bitmap src) {
		int sampleWidth = 10;
		int sampleHeight = sampleWidth * src.getHeight() / src.getWidth();
		sampleHeight = Math.max(sampleHeight, 10);
		Bitmap sample = Bitmap.createScaledBitmap(src, sampleWidth, sampleHeight, false);
		
		int count = sampleWidth * sampleHeight;
		int r = 0;
		int g = 0;
		int b = 0;
		for (int i = 0; i < sampleWidth; i++) {
			for (int j = 0; j < sampleHeight; j++) {
				int p = sample.getPixel(i, j);
				final int sr = (p >> 16) & 255;
				final int sg = (p >> 8) & 255;
				final int sb = p & 255;
				r += sr;
				g += sg;
				b += sb;
			}
		}
		r = r / count;
		g = g / count;
		b = b / count;
		LeLog.e("gyy:r:" + r + "|g:" + g + "|b:" + b);
		int a = 255;
		int color = (a << 24) | (r << 16) | (g << 8) | b;
		return color;
	}
	
	public static Bitmap getBitmap(Context context, int resId) {
		Drawable d = context.getResources().getDrawable(resId);
		if (d instanceof BitmapDrawable) {
			BitmapDrawable drawable = (BitmapDrawable) d;
			return drawable.getBitmap();
		} else {
			return createDefaultBitmap();
		}
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable == null) {
			return null;
		}
		// 取 drawable 的长宽  
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();

		// 取 drawable 的颜色格式  
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
		// 建立对应 bitmap  
		Bitmap bitmap = null;
		try {
			bitmap = Bitmap.createBitmap(w, h, config);
		} catch (OutOfMemoryError e) {
			LeLog.w("bitmap outofmemory error");
		} catch (Exception e) {
			LeLog.w("unknow exception");
		}
		// 建立对应 bitmap 的画布  
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		// 把 drawable 内容画到画布中  
		drawable.draw(canvas);
		return bitmap;
	}

	public static Bitmap getSnapBitmap(View view) {
		if (LeMachineHelper.isHighSpeedPhone()) {
			return getSnapBitmap(view, Config.ARGB_8888);
		} else {
			return getSnapBitmap(view, Config.RGB_565);
		}
	}

	public static Bitmap getSnapBitmap(View view, Config config) {
		try {
			Bitmap snapcache;
			snapcache = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), config);
			Canvas c = new Canvas(snapcache);
			c.drawColor(Color.WHITE);
			view.draw(c);
			return snapcache;
		} catch (OutOfMemoryError out) {
			out.printStackTrace();
			LeLog.e(out.getMessage());
			gc();
		} catch (Exception e) {
			LeLog.e(e.getMessage());
			e.printStackTrace();
		} finally {
			gc();
		}
		return createDefaultBitmap();
	}

	public static Bitmap getSnapBitmapWithAlpha(View view) {
		try {
			Bitmap snapcache;
			snapcache = Bitmap.createBitmap(view.getMeasuredWidth() + 1, view.getMeasuredHeight(),
					Config.ARGB_8888);

			Canvas c = new Canvas(snapcache);
			view.draw(c);
			return snapcache;
		} catch (OutOfMemoryError out) {
			out.printStackTrace();
			LeLog.e(out.getMessage());
			gc();
		} catch (Exception e) {
			LeLog.e(e.getMessage());
			e.printStackTrace();
		} finally {
			gc();
		}
		return null;
	}
	
	public static Bitmap getSnapBitmap(Bitmap snapcache, View view, Config config) {
		try {
			if(snapcache == null) {
				snapcache = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), config);
			}
			Canvas c = new Canvas(snapcache);
			c.drawColor(Color.WHITE);
			view.draw(c);
			return snapcache;
		} catch (OutOfMemoryError out) {
			out.printStackTrace();
			// LeLog.e(out.getMessage());
			System.gc();
		} catch (Exception e) {
			// LeLog.e(e.getMessage());
			e.printStackTrace();
		} finally {
			System.gc();
		}
		return createDefaultBitmap();
	}

	public static Bitmap getShadowSnapBitmap(View view, int shadowSize) {
		try {
			Bitmap snapcache;
			snapcache = Bitmap.createBitmap(view.getMeasuredWidth() + shadowSize * 2,
					view.getMeasuredHeight() + shadowSize * 2, Config.ARGB_8888);

			Canvas c = new Canvas(snapcache);
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setColor(0x00000000);
			paint.setShadowLayer(shadowSize, 0, 0, 0x66000000);
			c.drawRect(shadowSize, shadowSize, shadowSize + view.getMeasuredWidth(),
					shadowSize + view.getMeasuredHeight(), paint);

			c.save();
			c.translate(shadowSize, shadowSize);
			c.clipRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
			view.draw(c);

			c.restore();
			c.clipRect(0, 0, c.getWidth(), c.getHeight());
			return snapcache;
		} catch (OutOfMemoryError out) {
			out.printStackTrace();
			LeLog.e(out.getMessage());
			gc();
		} catch (Exception e) {
			LeLog.e(e.getMessage());
			e.printStackTrace();
		} finally {
			gc();
		}
		return null;
	}

	public static void recycleBitmap(Bitmap bitmap) {
		if (bitmap != null) {
			if (Build.VERSION.SDK_INT <= LeMachineHelper.RECYCLE_MAX_VERSION)
				bitmap.recycle();
		}
	}

	/**
	 * 依据colorPercent， 得到降低Alpha的画笔。
	 * 
	 * @param alphaPercent
	 * @param backgroundColor
	 * @param paint
	 * @return
	 */
	public static Paint getDecendAlphaPainter(float alphaPercent, int backgroundColor, Paint paint) {
		int R = (backgroundColor & (0x00ff0000)) >> 16;
		int G = (backgroundColor & (0x0000ff00)) >> 8;
		int B = backgroundColor & (0x000000ff);
		final float[] array = new float[] { alphaPercent, 0, 0, 0, (1 - alphaPercent) * R, 0,
				alphaPercent, 0, 0, (1 - alphaPercent) * G, 0, 0, alphaPercent, 0,
				(1 - alphaPercent) * B, 0, 0, 0, 1, 0 };

		if (paint == null) {
			paint = new Paint();
		}
		paint.setColorFilter(new ColorMatrixColorFilter(array));
		return paint;
	}

	public static Bitmap getViewScreenShot(View view, Bitmap bitmap, int width, int height) {

		if (view == null) {
			return null;
		}

		if (bitmap != null
				&& (bitmap.getWidth() != view.getWidth() || bitmap.getHeight() != view.getHeight())) {
			recycleBitmap(bitmap);
			bitmap = null;
		}

		try {
			if (bitmap == null) {
				bitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
			}
			Canvas c = new Canvas(bitmap);
			c.translate(-view.getScrollX(), -view.getScrollY());
			view.draw(c);
		} catch (Exception e) {
			LeLog.e(e.getMessage());
			return null;
		} catch (Error e) {
			LeLog.e(e.getMessage());
			return null;
		}

		return bitmap;
	}
	
	private static Bitmap createDefaultBitmap() {
		Bitmap bitmap = Bitmap.createBitmap(1, 1, Config.ARGB_8888);
		return bitmap;
	}
	
	/**
	 * 获取恰好等比覆盖一个区域的图片
	 */
	public static Bitmap getSuitCoverBitmap(final Bitmap srcBitmap, final int width, final int height) {
		Bitmap desBitmap = srcBitmap;
		if (desBitmap != null) {
			int bWidht = srcBitmap.getWidth();
			int bHeight = srcBitmap.getHeight();
			if ((width > bWidht || height > bHeight) ||
				(width < bWidht && height < bHeight)) {
				float hScale = (float)width / bWidht;
				float vScale = (float)height / bHeight;
				float scale = (hScale > vScale) ? hScale : vScale;
				Matrix matrix = new Matrix();
				matrix.postScale(scale, scale);
				desBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, bWidht, bHeight, matrix, true);
			}
		}
		return desBitmap;
	}
	
	/**
	 * 设置恰好等比覆盖的区域
	 */
	public static void setSuitCoverRect(Rect rect, int tWidth, int tHeight, int bWidht, int bHeight, float leftScale, float topScale) {
		float hScale = (float) tWidth / bWidht;
		float vScale = (float) tHeight / bHeight;
		if (hScale < vScale) {
			int suitWidth = (int) (bWidht * hScale / vScale);
			int offsetX = (int) ((bWidht - suitWidth) * leftScale);
			rect.set(offsetX, 0, offsetX + suitWidth, bHeight);
		} else {
			int suitHeight = (int) (bHeight * vScale / hScale);
			int offsetY = (int) ((bHeight - suitHeight) * topScale);
			rect.set(0, offsetY, bWidht, offsetY + suitHeight);
		}
	}
	
	/**
	 * 设置恰好等比覆盖的区域
	 */
	public static void setSuitCoverRect(Rect rect, int tWidth, int tHeight, int bWidht, int bHeight) {
		float hScale = (float) tWidth / bWidht;
		float vScale = (float) tHeight / bHeight;
		if (hScale < vScale) {
			int suitWidth = (int) (bWidht * hScale / vScale);
			rect.set(0, 0, suitWidth, bHeight);
		} else {
			int suitHeight = (int) (bHeight * vScale / hScale);
			rect.set(0, 0, bWidht, suitHeight);
		}
	}
	
	private static void gc() {
		System.gc();
	}
	
	public static Bitmap createRoundCornerIcon(Bitmap originBitmap, int iconSize, int roundCornerSize,
											   int padding) {
		if (originBitmap != null && !originBitmap.isRecycled() && iconSize > 0) {
			Bitmap roundBitmap = Bitmap.createBitmap(iconSize, iconSize, Config.ARGB_8888);
			Canvas canvas = new Canvas(roundBitmap);
			int roundSize = 0;
			if (roundCornerSize == 0) {
				roundSize = iconSize * 40 / 172;
			} else {
				roundSize = roundCornerSize;
			}

			final Paint paintR = new Paint();
			paintR.setAntiAlias(true);
			RectF rect = new RectF(0, 0, iconSize, iconSize);
			canvas.drawRoundRect(rect, roundSize, roundSize, paintR);
			paintR.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

			Rect iconCenterSrc = new Rect();
			iconCenterSrc.set(padding, padding, originBitmap.getWidth() - padding, originBitmap.getHeight()
					- padding);
			Rect iconCenterDst = new Rect();
			iconCenterDst.set(0, 0, iconSize, iconSize);

			canvas.drawBitmap(originBitmap, iconCenterSrc, iconCenterDst, paintR);
			return roundBitmap;
		}
		return null;
	}
	
	public static Bitmap createRoundCornerIconByMask(Bitmap originBitmap, Bitmap maskBitmap, int padding) {
		if (originBitmap != null && !originBitmap.isRecycled() && maskBitmap != null && !maskBitmap.isRecycled()) {
			int iconSize = maskBitmap.getWidth();
			Bitmap roundBitmap = Bitmap.createBitmap(iconSize, iconSize, Config.ARGB_8888);
			Canvas canvas = new Canvas(roundBitmap);
			

			final Paint paintR = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
			paintR.setAntiAlias(true);
			RectF rect = new RectF(0, 0, iconSize, iconSize);
			canvas.drawBitmap(maskBitmap, null, rect, paintR);
			paintR.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

			Rect iconCenterSrc = new Rect();
			iconCenterSrc.set(padding, padding, originBitmap.getWidth() - padding, originBitmap.getHeight()
					- padding);
			Rect iconCenterDst = new Rect();
			iconCenterDst.set(0, 0, iconSize, iconSize);

			canvas.drawBitmap(originBitmap, iconCenterSrc, iconCenterDst, paintR);
			return roundBitmap;
		}
		return null;
	}

	/***
	 * 按照指定半径裁剪出圆形图片
	 * @param originBitmap
	 * @param radius
     * @return
     */
	public static Bitmap createCircleIcon(Bitmap originBitmap, int radius) {
		int min = originBitmap.getWidth() >= originBitmap.getHeight() ?
				originBitmap.getHeight() : originBitmap.getWidth();

		Matrix matrix = new Matrix();
		float scale = (float) radius * 2 / min;//圆形图片的直径与最小值一样
		matrix.postScale(scale , scale);
		Bitmap scaleBitmap = Bitmap.createBitmap(originBitmap, 0, 0, originBitmap.getWidth(), originBitmap.getHeight(), matrix, true);

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		Bitmap circleBitmap = Bitmap.createBitmap(radius * 2, radius * 2, Config.ARGB_8888);
		Canvas canvas = new Canvas(circleBitmap);
		canvas.drawCircle(radius, radius, radius, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(scaleBitmap, 0, 0, paint);
		return circleBitmap;
	}
	
	public static byte[] bitmapToBytes(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}
		byte[] bytes = null;
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
			bytes = baos.toByteArray();
		} finally {
			try {
				if (baos != null) {
					baos.flush();
					baos.close();
				}
			} catch (IOException e) {
			}
		}
		return bytes;
	}
	
	public static Bitmap bytesToBitmap(byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	}
	
}
