package com.beiying.fitmanager.core.ui;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;

import com.beiying.fitmanager.core.utils.LeColorUtil;


public class LeStateIconDrawable extends LeStateDrawable {
	
	private Drawable mIcon;
	private Drawable mPressedIcon;
	private Drawable mFocusedIcon;
	private Drawable mDisabledIcon;
	private Drawable mDisabledFocusedIcon;
	private Drawable mSelectedIcon;

	private ColorFilter mEnabledColorFilter;
	private ColorFilter mPressedColorFilter;
	private ColorFilter mFocusedColorFilter;
	private ColorFilter mDisabledColorFilter;
	private ColorFilter mDisabledFocusedColorFilter;
	private ColorFilter mSelectedColorFilter;
	
	public LeStateIconDrawable() {
	}
	
	public void setIcon(Drawable icon) {
		mIcon = icon;
	}
	
	public void setEnabledColor(int color) {
		mEnabledColorFilter = LeColorUtil.createColorFilterByColor(color);
	}
	
	public void setEnabledColorFilter(ColorFilter cf) {
		mEnabledColorFilter = cf;
	}
	
	public void setPressedIcon(Drawable icon) {
		mPressedIcon = icon;
	}
	
	public void setPressedColor(int color) {
		mPressedColorFilter = LeColorUtil.createColorFilterByColor(color);
	}
	
	public void setFocusedIcon(Drawable icon) {
		mFocusedIcon = icon;
	}
	
	public void setFocusedColor(int color) {
		mFocusedColorFilter = LeColorUtil.createColorFilterByColor(color);
	}
	
	public void setDisabledIcon(Drawable icon) {
		mDisabledIcon = icon;
	}
	
	public void setDisabledColor(int color) {
		mDisabledColorFilter = LeColorUtil.createColorFilterByColor(color);
	}
	
	public void setDisabledFocusedIcon(Drawable icon) {
		mDisabledFocusedIcon = icon;
	}
	
	public void setDisabledFocusedColor(int color) {
		mDisabledFocusedColorFilter = LeColorUtil.createColorFilterByColor(color);
	}
	
	public void setSelectedIcon(Drawable icon) {
		mSelectedIcon = icon;
	}
	
	public void setSelectedColor(int color) {
		mSelectedColorFilter = LeColorUtil.createColorFilterByColor(color);
	}

	@Override
	public int getIntrinsicWidth() {
		if (mIcon == null) {
			return 0;
		}
		return mIcon.getIntrinsicWidth();
	}

	@Override
	public int getIntrinsicHeight() {
		if (mIcon == null) {
			return 0;
		}
		return mIcon.getIntrinsicHeight();
	}

	@Override
	public void drawPressed(Canvas canvas) {
		if (mPressedIcon != null) {
			drawIcon(canvas, mPressedIcon);
		} else if (mPressedColorFilter != null) {
			if (mIcon != null) {
				mIcon.setColorFilter(mPressedColorFilter);
				drawIcon(canvas, mIcon);
				mIcon.setColorFilter(null);
			}
		} else {
			drawEnabled(canvas);
		}
	}

	@Override
	public void drawDisabledFocused(Canvas canvas) {
		if (mDisabledFocusedIcon != null) {
			drawIcon(canvas, mDisabledFocusedIcon);
		} else if (mDisabledFocusedColorFilter != null) {
			if (mIcon != null) {
				mIcon.setColorFilter(mDisabledFocusedColorFilter);
				drawIcon(canvas, mIcon);
				mIcon.setColorFilter(null);
			}
		} else {
			drawEnabled(canvas);
		}
	}

	@Override
	public void drawFocused(Canvas canvas) {
		if (mFocusedIcon != null) {
			drawIcon(canvas, mFocusedIcon);
		} else if (mFocusedColorFilter != null) {
			if (mIcon != null) {
				mIcon.setColorFilter(mFocusedColorFilter);
				drawIcon(canvas, mIcon);
				mIcon.setColorFilter(null);
			}
		} else {
			drawEnabled(canvas);
		}
	}

	@Override
	public void drawEnabled(Canvas canvas) {
		if (mIcon == null) {
			return;
		}
		if (mEnabledColorFilter != null) {
			mIcon.setColorFilter(mEnabledColorFilter);
		}
		drawIcon(canvas, mIcon);
	}

	@Override
	public void drawDisabled(Canvas canvas) {
		if (mDisabledIcon != null) {
			drawIcon(canvas, mDisabledIcon);
		} else if (mDisabledColorFilter != null) {
			if (mIcon != null) {
				mIcon.setColorFilter(mDisabledColorFilter);
				drawIcon(canvas, mIcon);
				mIcon.setColorFilter(null);
			}
		} else {
			drawEnabled(canvas);
		}
	}
	
	@Override
	public void drawSelected(Canvas canvas) {
		if (mSelectedIcon != null) {
			drawIcon(canvas, mSelectedIcon);
		} else if (mSelectedColorFilter != null) {
			if (mIcon != null) {
				mIcon.setColorFilter(mSelectedColorFilter);
				drawIcon(canvas, mIcon);
				mIcon.setColorFilter(null);
			}
		} else {
			drawEnabled(canvas);
		}
	}

	@Override
	public void setAlpha(int alpha) {
		
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		mEnabledColorFilter = cf;
	}

	@Override
	public int getOpacity() {
		return 0;
	}
	
	private void drawIcon(Canvas canvas, Drawable icon) {
		setIconBounds(icon);
		icon.draw(canvas);
	}

	private void setIconBounds(Drawable icon) {
		int offsetX = getBounds().left;
		int offsetY = getBounds().top;
		offsetX += (getBounds().width() - icon.getIntrinsicWidth()) / 2;
		offsetY += (getBounds().height() - icon.getIntrinsicHeight()) / 2;
		icon.setBounds(offsetX, offsetY, offsetX + icon.getIntrinsicWidth(), offsetY + icon.getIntrinsicHeight());
	}
}
