package com.beiying.fitmanager.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
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

import com.beiying.fitmanager.core.LeLog;
import com.beiying.fitmanager.core.utils.LeMachineHelper;
import com.beiying.fitmanager.core.utils.LeUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BYBitmapUtil {

    private BYBitmapUtil() {
    }

    public static Bitmap qualityCompress(String file, int width, int height) {
        return null;
    }

    public static Bitmap sampleCompress(String file, int width, int height) {
        return null;
    };

    public static Bitmap matrixCompres(Bitmap src, float  scale) {
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        Bitmap bitmap = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        return bitmap;
    }

    /**
     * 获取图片指定指定宽高的缩略图
     * */
    public static Bitmap getThumbnail(String file, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap thumbnail = BitmapFactory.decodeFile(file, options);
        int sampleSize = getSampleSize(options, width, height);
        options.inSampleSize = sampleSize;
        options.inPreferredConfig = Config.RGB_565;
        options.inJustDecodeBounds = false;
        if (thumbnail != null && !thumbnail.isRecycled()) {
            thumbnail.recycle();
        }
        thumbnail = BitmapFactory.decodeFile(file, options);
        return thumbnail;

    }

    public static int getSampleSize(BitmapFactory.Options options, int maxWidth, int maxHeight) {
        int width = options.outWidth;
        int height = options.outHeight;

        int sampleSize = 1;
        if (width > maxWidth || height > maxHeight) {
            if (width > height) {
                sampleSize = Math.round(height / maxHeight);
            } else {
                sampleSize = Math.round(width / maxWidth);
            }
        }
        return sampleSize;
    }

    /**
     * 将Bitmap转成Base64字符串
     *
     *
     * */
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

    /**
     * 解析base64字符串为图片
     * */
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

    public static Bitmap getScaledBitmap(Bitmap src) {
        int sampleWidth = 10;
        int sampleHeight = sampleWidth * src.getHeight() / src.getWidth();
        sampleHeight = Math.max(sampleHeight, 10);
        Bitmap sample = Bitmap.createScaledBitmap(src, sampleWidth, sampleHeight, false);
        return sample;
    }

    public static Bitmap doBlur(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {
        Bitmap bitmap;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }


    public static Bitmap getScaledBitmap(Bitmap src, int mSampleWidth) {
        int sampleWidth = mSampleWidth;
        int sampleHeight = sampleWidth * src.getHeight() / src.getWidth();
        sampleHeight = Math.max(sampleHeight, sampleWidth);
        Bitmap sample = Bitmap.createScaledBitmap(src, sampleWidth, sampleHeight, false);
        return sample;
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
        Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
                : Config.RGB_565;
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

    /**
     * 获取将View转换成图片
     * */
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
            if (snapcache == null) {
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
        final float[] array = new float[]{alphaPercent, 0, 0, 0, (1 - alphaPercent) * R, 0,
                alphaPercent, 0, 0, (1 - alphaPercent) * G, 0, 0, alphaPercent, 0,
                (1 - alphaPercent) * B, 0, 0, 0, 1, 0};

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
                float hScale = (float) width / bWidht;
                float vScale = (float) height / bHeight;
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

    public static Bitmap createRoundCornerBitmap(Bitmap originBitmap, float cornerSize) {
        return createRoundCornerBitmap(originBitmap, cornerSize, true, true, true, true);
    }

    // 1┌───────┐2
    //..│.......│
    // 4└───────┘3
    public static Bitmap createRoundCornerBitmap(Bitmap originBitmap, float cornerSize, boolean isRound1, boolean isRound2, boolean isRound3, boolean isRound4) {
        if (originBitmap != null && !originBitmap.isRecycled()) {
            Bitmap roundBitmap = Bitmap.createBitmap(originBitmap.getWidth(), originBitmap.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(roundBitmap);

            final Paint paintR = new Paint();
            paintR.setAntiAlias(true);

            Path roundRect = PathUtil.createRoundRectPath(originBitmap.getWidth(), originBitmap.getHeight(), 0, 0, cornerSize, isRound1, isRound2, isRound3, isRound4);
            canvas.drawPath(roundRect, paintR);
            paintR.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

            canvas.drawBitmap(originBitmap, 0, 0, paintR);
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
