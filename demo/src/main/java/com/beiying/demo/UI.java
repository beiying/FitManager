package com.beiying.demo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;

public class UI {
	
	public static final int UI_DEFAULT_STATUSBAR_HEIGHT = 18;
	
	public static Point getScreenPoint(View view) {
		if (view == null) {
			return null;
		}
		int[] pos = new int[2];
		view.getLocationOnScreen(pos);
		return new Point(pos[0], pos[1]);
	}

	/**
	 * 强制对View及其子视图进行递归刷新
	 * 
	 * @param view
	 */
	public static void forceChildrenInvalidateRecursively(View view) {
		if (view instanceof ViewGroup) {
			ViewGroup childGroup = (ViewGroup) view;
			int childCnt = childGroup.getChildCount();
			for (int i = 0; i < childCnt; i++) {
				View childView = childGroup.getChildAt(i);
				forceChildrenInvalidateRecursively(childView);
			}
		}
		if (view != null) {
			view.invalidate();
		}
	}
	
	public static void forceChildrenRelayoutRecursively(View view) {
		if (view instanceof ViewGroup) {
			ViewGroup childGroup = (ViewGroup) view;
			int childCnt = childGroup.getChildCount();
			for (int i = 0; i < childCnt; i++) {
				View childView = childGroup.getChildAt(i);
				forceChildrenRelayoutRecursively(childView);
			}
		}
		if (view != null) {
			view.requestLayout();
		}
	}

	public static View removeFromParent(View child) {
		if (child != null) {
			View parent = (View) child.getParent();
			if (parent != null && parent instanceof ViewGroup) {
				((ViewGroup) parent).removeView(child);
			}
		}
		return child;
	}
	
	public static void setBackground(View view, Drawable drawable) {
		view.setBackgroundDrawable(drawable);
	}
	
	public static int getLeftOnScreen(View view) {
		int pos[] = new int[2];
		view.getLocationOnScreen(pos);
		return pos[0];
	}
	
	public static int getRightOnScreen(View view) {
		int pos[] = new int[2];
		view.getLocationOnScreen(pos);
		return pos[0] + view.getWidth();
	}
	
	public static int getTopOnScreen(View view) {
		int pos[] = new int[2];
		view.getLocationOnScreen(pos);
		return pos[1];
	}
	
	public static int getBottomOnScreen(View view) {
		int pos[] = new int[2];
		view.getLocationOnScreen(pos);
		return pos[1] + view.getHeight();
	}
	
	@SuppressLint("NewApi")
	public static boolean isViewVisible(View view) {
		boolean isVisible = true;
		if (view == null) {
			return false;
		}
		if (view.getWidth() == 0 || view.getHeight() == 0) {
			isVisible = false;
		}
		if (!view.isShown()) {
			isVisible = false;
		}
		if (view.getAlpha() == 0) {
			isVisible = false;
		}
		if (getBottomOnScreen(view) <= 1 || getRightOnScreen(view) <= 1 || getTopOnScreen(view) >= getScreenHeight(view.getContext()) - 1
				|| getLeftOnScreen(view) >= getScreenWidth(view.getContext()) - 1) {
			isVisible = false;
		}
		View parent = (View) view.getParent();
		if (getBottomOnScreen(view) <= getTopOnScreen(parent) || getRightOnScreen(view) <= getLeftOnScreen(parent) 
				|| getTopOnScreen(view) >= getBottomOnScreen(parent) || getLeftOnScreen(view) >= getRightOnScreen(parent)) {
			isVisible = false;
		}
		return isVisible;
	}
	
	public static int getScreenWidth(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.widthPixels;
	}
	
	public static int getScreenHeight(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.heightPixels;
	}
	
	public static float getDensity(Context context) {
		final DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.density;
	}

	public static int getDensityDimen(Context context, int dimen) {
		final DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return (Math.round(dimen * dm.density));
	}
	
	public static int getScaledDensityDimen(Context context, int dimen){
		final DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return (Math.round(dimen * dm.scaledDensity));
	}
	
	public static final int centerSize(int total, int inside) {
		return (total - inside) >> 1;
	}

	public static final void layoutChildAbsoluteCenter(ViewGroup parent,
													   View child, int tWidth, int tHeight) {
		int w, h, x, y;
		w = child.getMeasuredWidth();
		h = child.getMeasuredHeight();
		x = centerSize(tWidth, w);
		y = centerSize(tHeight, h);
		child.layout(x, y, x + w, y + h);
	}
	
	public static void measureExactly(View view, int width, int height) {
		int widthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
		int heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
		view.measure(widthSpec, heightSpec);
	}

	public static void layoutViewAtPos(View view, int offsetX, int offsetY) {
		view.layout(offsetX, offsetY, offsetX + view.getMeasuredWidth(), offsetY + view.getMeasuredHeight());
	}
	
	public static void drawDrawable(Canvas canvas, Drawable drawable, int offsetX, int offsetY) {
		drawable.setBounds(offsetX, offsetY, offsetX + drawable.getIntrinsicWidth(), offsetY + drawable.getIntrinsicHeight());
		drawable.draw(canvas);
	}

	/**
	 * 缓存一下这个值，不是每次都去取。
	 */
	private static int sStatusbarHeight = -1;
	
	public static int getStatusbarHeight(Context context) {
		if (sStatusbarHeight >= 0) {
			return sStatusbarHeight;
		}
		int statusbarHeight = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			statusbarHeight = context.getResources().getDimensionPixelSize(resourceId);
		}
		if (statusbarHeight > 0) {
			sStatusbarHeight = statusbarHeight;
			return statusbarHeight;
		}
		statusbarHeight = getDensityDimen(context, UI_DEFAULT_STATUSBAR_HEIGHT);
		sStatusbarHeight = statusbarHeight;
		return statusbarHeight;
	}
}
