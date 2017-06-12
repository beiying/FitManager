package com.beiying.fitmanager.core.ui;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class LePopMenuLayout extends FrameLayout implements LeThemable {

	// 手指按下的半径
	private static final int FINGER_RADIUS = 0;

	protected LePopMenuLayoutListener mListener;

	public LePopMenuLayout(Context context) {
		this(context, null);
	}

	public LePopMenuLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LePopMenuLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		setClickable(true);

		setVisibility(View.INVISIBLE);
	}

	public void setPopMenuLayoutListener(LePopMenuLayoutListener listener) {
		mListener = listener;
	}

	public boolean isShowing() {
		return (getVisibility() == View.VISIBLE);
	}

	public View getPopContent() {
		if (getChildCount() != 0) {
			return getChildAt(0);
		} else {
			return null;
		}
	}

	public void showPopView(LePopMenu popMenu) {
		final FrameLayout.LayoutParams lParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		lParams.gravity = Gravity.CENTER;

		showPopView(popMenu, lParams, null);
	}

	/**
	 *
	 * 在某个点附近弹出菜单 注意：仅当PopMenuLayout是全屏时才可以使用该方法
	 */
	public void showPopView(LePopContent popContent, Point position) {
		showPopView(popContent, position, 0, 0);
	}

	public void showPopView(LePopContent popContent, Point position, int paddingTop, int paddingBottom) {
		showPopViewExactlyAt(popContent, position, paddingTop, paddingBottom, 0);
	}

	public void showPopViewExactlyAt(LePopContent popContent, Point position, int paddingTop,
									 int paddingBottom, int marginTop) {
		final DisplayMetrics dm = getResources().getDisplayMetrics();
		final int menuWidth = popContent.getContentWidth();
		final int menuHeight = popContent.getContentHeight();

		FrameLayout.LayoutParams lParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		lParams.gravity = Gravity.NO_GRAVITY;

		if (position.y + menuHeight < getMeasuredHeight() - paddingBottom) {
			lParams.topMargin = position.y;
		} else if (position.y - paddingTop > menuHeight) {
			lParams.topMargin = position.y - menuHeight;
		} else {
			if (position.y - paddingTop >= getMeasuredHeight() - position.y - paddingBottom) {
				lParams.topMargin = paddingTop;
			} else {
				lParams.topMargin = getMeasuredHeight() - menuHeight - paddingBottom;
			}
		}
		lParams.leftMargin = position.x;
		int popMenuWidth = getMeasuredWidth();
		if (popMenuWidth == 0) {
			popMenuWidth = dm.widthPixels;
		}
		if (position.x + menuWidth < popMenuWidth) {
			lParams.leftMargin = position.x;
		} else if (position.x > menuWidth) {
			lParams.leftMargin = position.x - menuWidth;
		} else {
			if (popMenuWidth - position.x > position.x) {
				lParams.leftMargin = popMenuWidth - menuWidth;
			} else {
				lParams.leftMargin = 0;
			}
		}
		showPopView(popContent, lParams, position);
	}

	public void showPopView(LePopContent popContent, Point position, int paddingTop, int paddingBottom,
							int marginTop) {
		final DisplayMetrics dm = getResources().getDisplayMetrics();
		final int menuWidth = popContent.getContentWidth();
		final int menuHeight = popContent.getContentHeight();

		//		final int statusbarHeight = LeUtils.getStatusbarHeight((Activity) getContext());
		//		position.y -= (statusbarHeight + marginTop);

		final int fingerRadius = (int) (FINGER_RADIUS * dm.density);

		FrameLayout.LayoutParams lParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		lParams.gravity = Gravity.NO_GRAVITY;

		if (position.y + menuHeight + fingerRadius < getMeasuredHeight() - paddingBottom) {
			// 如果上方放不下，下方放得下，放在下方
			lParams.topMargin = position.y + fingerRadius;
		} else if (position.y - paddingTop > menuHeight + fingerRadius) {
			// 如果点击上方放得下，则放在上方
			lParams.topMargin = position.y - menuHeight - fingerRadius;
		} else {
			// 如果上方下方都放不下
			if (position.y >= getMeasuredHeight() - position.y) {
				// 上方空间更大
				lParams.topMargin = paddingTop;
			} else {
				// 下方空间更大
				lParams.topMargin = getMeasuredHeight() - menuHeight - paddingBottom;
			}
		}

		lParams.leftMargin = position.x - menuWidth / 2;
		if (lParams.leftMargin < 0) {
			lParams.leftMargin = 0;
		}
		int popMenuWidth = getMeasuredWidth();
		if (popMenuWidth == 0) {
			popMenuWidth = dm.widthPixels;
		}
		if (lParams.leftMargin + menuWidth > popMenuWidth) {
			lParams.leftMargin = popMenuWidth - menuWidth;
		}

		showPopView(popContent, lParams, position);
	}

	public void showPopView(View popContent, FrameLayout.LayoutParams layoutParams, Point position) {
		if (getChildCount() != 0) {
			recycleAndDestoryMenus();
			removeAllViews();
		}

		if (popContent instanceof LePopContent) {
			popContent.setDrawingCacheEnabled(true);
		}
		addView(popContent, layoutParams);

		setVisibility(View.VISIBLE);
	}

	public void dismissPopView() {
		if (mListener != null) {
			mListener.onMenuDismiss();
		}

		recycleAndDestoryMenus();
		removeAllViews();
		setVisibility(View.INVISIBLE);
	}

	protected void recycleAndDestoryMenus() {
		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).clearAnimation();
			if (!(getChildAt(i) instanceof LePopMenu)) {
				continue;
			}
			((LePopMenu) getChildAt(i)).recyclePopMenu();
			getChildAt(i).destroyDrawingCache();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				dismissPopView();
				break;
			default:
				break;
		}
		return super.onTouchEvent(event);
	}

    @Override
    public void onThemeChanged() {

    }

    public interface LePopMenuLayoutListener {
		void onMenuDismiss();
	}

}
