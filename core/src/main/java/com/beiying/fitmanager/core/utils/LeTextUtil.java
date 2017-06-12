package com.beiying.fitmanager.core.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.text.TextUtils;

import com.beiying.fitmanager.core.LeLog;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LeTextUtil {

	private LeTextUtil() {
	}

	public static int calcXWhenAlignCenter(int width, Paint paint, String string) {
		if (paint == null) {
			return 0;
		}
		return (int) ((width - paint.measureText(string)) / 2);
	}

	/**
	 * 
	 * @return 提供文字垂直居中时,drawText方法中y值
	 */
	public static int calcYWhenAlignCenter(int height, Paint paint) {
		if (paint == null) {
			return 0;
		}

		final float fontHeight = paint.getFontMetrics().bottom
				- paint.getFontMetrics().top;
		return (int) ((height - fontHeight) / 2 - paint.getFontMetrics().top);
	}
	
	public static int calcYWhenTwoLinesAlignCenter(int height, Paint paint1, Paint paint2, int gap) {
		if (paint1 == null || paint2 == null) {
			return 0;
		}
		
		final float fontHeight = getPaintHeight(paint1) + getPaintHeight(paint2) + gap;
		return (int) ((height - fontHeight) / 2 - paint1.getFontMetrics().top);
	}

	public static int getPaintHeight(Paint paint) {
		return (int) (paint.getFontMetrics().bottom - paint.getFontMetrics().top);
	}

	public static void drawTextAtCanvasCenter(Canvas canvas, Paint paint,
											  String text) {
		if (text == null) {
			return;
		}

		final int width = canvas.getWidth();
		final int height = canvas.getHeight();
		int offsetX, offsetY;
		offsetX = calcXWhenAlignCenter(width, paint, text);
		offsetY = calcYWhenAlignCenter(height, paint);
		canvas.drawText(text, offsetX, offsetY, paint);
	}

    public static String getTruncateMiddleString(String srcString, Paint paint, int width) {
        if (srcString == null) {
            return "";
        }
        TextPaint textPaint = new TextPaint(paint);
        float[] charsWidth = new float[srcString.length()];
        paint.getTextWidths(srcString, charsWidth);

        float charsTotalWidth = 0;
        int head = 0, tail = srcString.length() - 1;
        while (head <= tail) {
            charsTotalWidth += charsWidth[head];
            if (head != tail) {
                charsTotalWidth += charsWidth[tail];
            }
            if (charsTotalWidth + getOmitStringWidths() > width) {
                srcString = srcString.substring(0, head) + "..." + srcString.substring(tail + 1);
                break;
            }
            head++;
            tail--;
        }
        return srcString;
    }

	public static String getTruncateEndString(String srcString, Paint paint,
											  int width) {
		if (srcString == null) {
			return "";
		}
		TextPaint textPaint = new TextPaint(paint);
//		textPaint.setTypeface(Typeface.DEFAULT);
		String result = TextUtils.ellipsize(srcString, textPaint,
				width, TextUtils.TruncateAt.END).toString();
		if (paint.measureText(result) > width) {
			return getSpecialTruncateEndString(result, paint, width);
		}
		return result;
	}

	private static String getSpecialTruncateEndString(String srcString,
													  Paint paint, int width) {
		float[] charsWidths = new float[srcString.length()];
		paint.getTextWidths(srcString, charsWidths);
		float charsTotalWidths = 0;
		for (int i = 0; i < srcString.length(); i++) {
			charsTotalWidths += charsWidths[i];
			if (charsTotalWidths + getOmitStringWidths() > width) {
				srcString = srcString.substring(0, i) + "...";
				break;
			}
		}
		return srcString;
	}

	private static int getOmitStringWidths() {
		int widths = 0;
		String ellipsisString = "...";
		float[] charsWidths = new float[ellipsisString.length()];
		new TextPaint().getTextWidths(ellipsisString, charsWidths);
		for (int i = 0; i < ellipsisString.length(); i++) {
			widths += charsWidths[i];
		}
		return widths;

	}

	/**
	 * 获取文本的显示长度
	 */
	public static float getTextShowLength(final String text) {
		float showLength = 0;
		if (text != null) {
			int length = text.length();
			for (int i = 0; i < length; i++) {
				char currentChar = text.charAt(i);
				if (currentChar == '"' || currentChar == '“' || currentChar == '”') {
					showLength += 0.3f;
				} else if (currentChar > 0x7f) {
					showLength += 1.0f;
				} else {
					showLength += 0.5f;
				}
			}
		}
		return showLength;
	}

	public static int getTextWidth(int size, String str) {
		Paint paint = new Paint();
		paint.setTextSize(size);
		int width = 0;
		if (str != null && str.length() > 0) {
			int len = str.length();
			float[] widths = new float[len];
			paint.getTextWidths(str, widths);
			for (int j = 0; j < len; j++) {
				width += (int) Math.ceil(widths[j]);
			}
		}
		return width;
	}

	public static int getTextWidth(Paint paint, String str) {
		int width = 0;
		if (str != null && str.length() > 0) {
			int len = str.length();
			float[] widths = new float[len];
			paint.getTextWidths(str, widths);
			for (int j = 0; j < len; j++) {
				width += (int) Math.ceil(widths[j]);
			}
		}
		return width;
	}

	public static List<String> getBreakTexts(final String text,
											 final int lineSize) {
		List<String> titles = new ArrayList<String>();
		if (!LeUtils.isEmptyString(text)) {
			int start = 0;
			while (start < text.length()) {
				String cutTitle = getBreakText(text, start, lineSize);
				start += cutTitle.length();
				titles.add(cutTitle);
			}
		}
		return titles;
	}

	/**
	 * 文本折行
	 */
	private static String getBreakText(String title, int start, int size) {
		int length = title.length();
		String cutString = "";
		float cutSize = 0;
		for (int i = start; i < length; i++) {
			char currentChar = title.charAt(i);
			if (currentChar == '"' || currentChar == '“' || currentChar == '”') {
				cutSize += 0.3f;
			} else if (currentChar > 0x7f) {
				cutSize += 1.0f;
			} else {
				cutSize += 0.5f;
			}
			if (cutSize > size) {
				return cutString;
			}
			cutString += currentChar;
		}
		return cutString;
	}

	public static boolean hasChinese(String key) {
		try {
			Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]+");
			Matcher matcher = pattern.matcher(key);
			if (matcher.find()) {
				return true;
			}
		} catch (Exception e) {
			LeLog.e(e);
		}
		return false;
	}

	public static final String full2HalfChange(String fullString) throws UnsupportedEncodingException {
		StringBuffer outStrBuf = new StringBuffer("");
		String Tstr = "";
		byte[] b = null;

		for (int i = 0; i < fullString.length(); i++) {
			Tstr = fullString.substring(i, i + 1);
			// 全角空格转换成半角空格
			if (Tstr.equals("　")) {
				outStrBuf.append(" ");
				continue;
			}

			b = Tstr.getBytes("unicode");
			// 得到 unicode 字节数据
			if (b[2] == -1) {
				// 表示全角？
				b[3] = (byte) (b[3] + 32);
				b[2] = 0;
				outStrBuf.append(new String(b, "unicode"));
			} else {
				outStrBuf.append(Tstr);
			}
		} // end for.
		return outStrBuf.toString();
	}

	// 半角转全角

	public static final String half2Fullchange(String halfString) throws UnsupportedEncodingException {
		StringBuffer outStrBuf = new StringBuffer("");
		String Tstr = "";
		byte[] b = null;
		
		for (int i = 0; i < halfString.length(); i++) {
			Tstr = halfString.substring(i, i + 1);
			if (Tstr.equals(" ")) {
				// 半角空格
				outStrBuf.append(Tstr);
				continue;
			}

			b = Tstr.getBytes("unicode");
			if (b[2] == 0) {
				// 半角?
				b[3] = (byte) (b[3] - 32);
				b[2] = -1;
				outStrBuf.append(new String(b, "unicode"));
			} else {
				outStrBuf.append(Tstr);
			}
		}
		return outStrBuf.toString();
	}

}
