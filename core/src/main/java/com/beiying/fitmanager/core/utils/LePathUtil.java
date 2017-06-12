package com.beiying.fitmanager.core.utils;

import android.graphics.Path;
import android.graphics.RectF;

public class LePathUtil {
	
	private LePathUtil(){}

	/**
	 * 
	 * @return 提供形如 “﹥”的箭头
	 */
	public static Path createRightArrowPath(int x, int y, int width, int height) {
		Path path = new Path();

		path.moveTo(x, y);
		path.lineTo(x + width, y + (height >> 1));
		path.lineTo(x, y + height);

		return path;
	}

	/**
	 * 
	 * @return 提供形如 “<”的箭头
	 */
	public static Path createLeftArrowPath(int x, int y, int width, int height) {
		Path path = new Path();

		path.moveTo(x + width, y);
		path.lineTo(x, y + (height >> 1));
		path.lineTo(x + width, y + height);

		return path;
	}

	/**
	 * 
	 * @return 提供形如 “∨”的箭头
	 */
	public static Path createDownArrowPath(int x, int y, int width, int height) {
		Path path = new Path();

		path.moveTo(x, y);
		path.lineTo(x + (width >> 1), y + height);
		path.lineTo(x + width, y);

		return path;
	}
	
	/**
	 * 
	 * @return 提供形如 “∧”的箭头
	 */
	public static Path createUpArrowPath(int x, int y, int width, int height) {
		Path path = new Path();

		path.moveTo(x, y + height);
		path.lineTo(x + (width >> 1), y);
		path.lineTo(x + width, y + height);

		return path;
	}

	/**
	 * 
	 * @return 提供顶角朝上的等腰三角形
	 */
	public static Path createUpTrianglePath(int x, int y, int width, int height) {
		Path path = new Path();

		path.moveTo(x, y + height);
		path.lineTo(x + (width >> 1), y);
		path.lineTo(x + width, y + height);
		path.close();

		return path;
	}

	/**
	 * 
	 * @return 提供顶角朝下的等腰三角形
	 */
	public static Path createDownTrianglePath(int x, int y, int width,
											  int height) {
		Path path = new Path();

		path.moveTo(x, y);
		path.lineTo(x + width, y);
		path.lineTo(x + (width >> 1), y + height);
		path.close();

		return path;
	}

	/**
	 * @return 提供如下的Path：
	 */
	// ┌────∧────┐
	// └───────────────┘
	public static Path createTagPath(int width, int height, int triangleX,
									 int triangleSize, int xPadding, int yPadding) {

		return createTagPath(width, height, triangleX, triangleSize, xPadding,
				yPadding, false);
	}

	public static Path createTagPath(int width, int height, int triangleX,
									 int triangleSize, int xPadding, int yPadding, boolean orientDown) {

		Path path = new Path();

		float P1x = xPadding;
		float P1y = triangleSize + yPadding;
		float P2x = triangleX - triangleSize;
		float P2y = triangleSize + yPadding;
		float P3x = triangleX;
		float P3y = yPadding;
		float P4x = triangleX + triangleSize;
		float P4y = triangleSize + yPadding;
		float P5x = width - xPadding;
		float P5y = triangleSize + yPadding;
		float P6x = width - xPadding;
		float P6y = height - yPadding;
		float P7x = xPadding;
		float P7y = height - yPadding;

		if (orientDown) {
			float X = width - xPadding;
			float Y = height - yPadding;

			P1x = mirror(P1x, X);
			P2x = mirror(P2x, X);
			P3x = mirror(P3x, X);
			P4x = mirror(P4x, X);
			P5x = mirror(P5x, X);
			P6x = mirror(P6x, X);
			P7x = mirror(P7x, X);

			P1y = mirror(P1y, Y);
			P2y = mirror(P2y, Y);
			P3y = mirror(P3y, Y);
			P4y = mirror(P4y, Y);
			P5y = mirror(P5y, Y);
			P6y = mirror(P6y, Y);
			P7y = mirror(P7y, Y);
		}

		path.moveTo(P1x, P1y);
		path.lineTo(P2x, P2y);
		path.lineTo(P3x, P3y);
		path.lineTo(P4x, P4y);
		path.lineTo(P5x, P5y);
		path.lineTo(P6x, P6y);
		path.lineTo(P7x, P7y);
		path.close();

		return path;
	}

	private static float mirror(float point, float mirrorLength) {
		return mirrorLength - (point);
	}

	/**
	 * @param aWidth
	 *            整体宽度
	 * @param aHeight
	 *            整体高度
	 * @param aXPadding
	 *            横向的padding
	 * @param aYPadding
	 *            纵向的padding
	 * @param aCornerSize
	 *            圆角大小
	 * @param isRound1
	 *            1号角是否是圆角
	 * @param isRound2
	 *            2号角是否是圆角
	 * @param isRound3
	 *            3号角是否是圆角
	 * @param isRound4
	 *            4号角是否是圆角
	 * @return 提供如下的圆角矩形Path：
	 */
	// 1┌───────┐2
	//..│.......│
	// 4└───────┘3
	public static Path createRoundRectPath(float width, float height,
										   float xPadding, float yPadding, float cornerSize,
										   boolean isRound1, boolean isRound2, boolean isRound3,
										   boolean isRound4) {
		return createRoundRectPath(width, height, xPadding, xPadding,
				yPadding, yPadding, cornerSize, isRound1, isRound2,
				isRound3, isRound4);
	}

	/**
	 * @param width
	 *            整体宽度
	 * @param height
	 *            整体高度
	 * @param paddingLeft
	 *            左侧的padding
	 * @param paddingRight
	 *            右侧的padding
	 * @param paddingTop
	 *            上侧的padding
	 * @param paddingBottom
	 *            下侧的padding
	 * @param cornerSize
	 *            圆角大小
	 * @param isRound1
	 *            1号角是否是圆角
	 * @param isRound2
	 *            2号角是否是圆角
	 * @param isRound3
	 *            3号角是否是圆角
	 * @param isRound4
	 *            4号角是否是圆角
	 * @return 提供如下的圆角矩形Path：
	 */
	// 1┌───────┐2
	//..│.......│
	// 4└───────┘3
	public static Path createRoundRectPath(float width, float height,
										   float paddingLeft, float paddingRight, float paddingTop,
										   float paddingBottom, float cornerSize, boolean isRound1,
										   boolean isRound2, boolean isRound3, boolean isRound4) {
		Path path = new Path();
		RectF oval;

		/** 左上角起始 */
		if (isRound1) {
			path.moveTo(paddingLeft, paddingTop + cornerSize);
			oval = new RectF(paddingLeft, paddingTop, paddingLeft
					+ cornerSize * 2, paddingTop + cornerSize * 2);
			path.arcTo(oval, 180, 90);
		} else {
			path.moveTo(paddingLeft, paddingTop);
		}

		/** 上边框 */
		if (isRound2) {
			path.lineTo(width - paddingRight - cornerSize, paddingTop);
			oval = new RectF(width - paddingRight - cornerSize * 2,
					paddingTop, width - paddingRight, paddingTop
							+ cornerSize * 2);
			path.arcTo(oval, 270, 90);
		} else {
			path.lineTo(width - paddingRight, paddingTop);
		}

		/** 右边框 */
		if (isRound3) {
			path.lineTo(width - paddingRight, height - paddingBottom
					- cornerSize);
			oval = new RectF(width - paddingRight - cornerSize * 2, height
					- paddingBottom - cornerSize * 2, width - paddingRight,
					height - paddingBottom);
			path.arcTo(oval, 0, 90);
		} else {
			path.lineTo(width - paddingRight, height - paddingBottom);
		}

		/** 下边框 */
		if (isRound4) {
			path.lineTo(paddingLeft + cornerSize, height - paddingBottom);
			oval = new RectF(paddingLeft, height - paddingBottom
					- cornerSize * 2, paddingLeft + cornerSize * 2, height
					- paddingBottom);
			path.arcTo(oval, 90, 90);
		} else {
			path.lineTo(paddingLeft, height - paddingBottom);
		}

		/** 左边框 */
		path.close();

		return path;
	}

	public static Path createLTCornerReverse(int cornerSize) {
		Path path = new Path();
		RectF oval;
		path.moveTo(0, 0);
		path.lineTo(0, cornerSize);
		oval = new RectF(0, 0, cornerSize * 2, cornerSize * 2);
		path.arcTo(oval, 180, 90);
		path.close();
		return path;
	}
	
	public static Path createRTCornerReverse(int cornerSize) {
		Path path = new Path();
		RectF oval;
		path.moveTo(0, 0);
		oval = new RectF(-cornerSize, 0, cornerSize, cornerSize * 2);
		path.arcTo(oval, 270, 90);
		path.lineTo(cornerSize, 0);
		path.close();
		return path;
	}
	
	public static Path createRBCornerReverse(int cornerSize) {
		Path path = new Path();
		RectF oval;
		path.moveTo(cornerSize, 0);
		oval = new RectF(-cornerSize, -cornerSize, cornerSize, cornerSize);
		path.arcTo(oval, 0, 90);
		path.lineTo(cornerSize, cornerSize);
		path.close();
		return path;
	}
	
	public static Path createLBCornerReverse(int cornerSize) {
		Path path = new Path();
		RectF oval;
		path.moveTo(cornerSize, cornerSize);
		oval = new RectF(0, -cornerSize, cornerSize * 2, cornerSize);
		path.arcTo(oval, 90, 90);
		path.lineTo(0, cornerSize);
		path.close();
		return path;
	}
}
