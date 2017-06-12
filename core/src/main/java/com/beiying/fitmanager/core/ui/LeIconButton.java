package com.beiying.fitmanager.core.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;

public class LeIconButton extends LeBgButton {
	
	protected LeStateIconDrawable mIconDrawable;
	
	protected int mIconWidth;
	protected int mIconHeight;
	
	public LeIconButton(Context context) {
		super(context);
		
		setWillNotDraw(false);

		mIconDrawable = new LeStateIconDrawable();
	}
	
	public int getIntrinsicWidth() {
		return mIconDrawable.getIntrinsicWidth();
	}

	public int getIntrinsicHeight() {
		return mIconDrawable.getIntrinsicHeight();
	}
	
	public Drawable getStateIconDrawable() {
		return mIconDrawable;
	}
	
	public void setStateIconDrawable(Drawable iconDrawable) {
		mIconDrawable = (LeStateIconDrawable) iconDrawable;
		drawableStateChanged();
	}

	@Override
	protected void drawableStateChanged() {
		super.drawableStateChanged();
		
        if (mIconDrawable != null) {
        	mIconDrawable.setState(getDrawableState());
        }
	}

	@Override
	public void setEnabled(boolean enabled) {
		if (enabled) {
			setFocusable(true);
		} else {
			setFocusable(false);
		}
		super.setEnabled(enabled);
	}
	
	public void setIcon(int resId) {
		setIcon(getResources().getDrawable(resId));
	}
	
	public void setIcon(Drawable icon) {
		mIconDrawable.setIcon(icon);
		invalidate();
	}
	
	public void setNormalColorFilter(ColorFilter cf) {
		mIconDrawable.setEnabledColorFilter(cf);
	}
	
	public void setNormalColor(int color) {
		setEnabledColor(color);
	}
	
	public void setEnabledColor(int color) {
		mIconDrawable.setEnabledColor(color);
	}
	
	public void setPressedIcon(Drawable icon) {
		mIconDrawable.setPressedIcon(icon);
	}
	
	public void setPressedColor(int color) {
		mIconDrawable.setPressedColor(color);
	}
	
	public void setFocusedIcon(Drawable icon) {
		mIconDrawable.setFocusedIcon(icon);
	}
	
	public void setFocusedColor(int color) {
		mIconDrawable.setFocusedColor(color);
	}
	
	public void setDisabledIcon(Drawable icon) {
		mIconDrawable.setDisabledIcon(icon);
	}
	
	public void setDisabledColor(int color) {
		mIconDrawable.setDisabledColor(color);
	}
	
	public void setDisabledFocusedIcon(Drawable icon) {
		mIconDrawable.setDisabledFocusedIcon(icon);
	}
	
	public void setDisabledFocusedColor(int color) {
		mIconDrawable.setDisabledFocusedColor(color);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		
		if (width == 0 || height == 0) {
			if (mIconDrawable != null) {
				width = mIconDrawable.getIntrinsicWidth();
				height = mIconDrawable.getIntrinsicHeight();
			}
		}
		
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		mIconDrawable.setBounds(0, 0, getIntrinsicWidth(), getIntrinsicHeight());
		mIconDrawable.draw(canvas);
	}
	
	protected int getIconOffsetX() {
		return (getMeasuredWidth() - mIconWidth) / 2;
	}
}
