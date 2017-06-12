package com.beiying.fitmanager.core.ui;

import android.content.Context;
import android.view.View;

public class LeGridItem extends LeFrameViewGroup {

	protected boolean mIsInDrag = false;
	
	private int mPosX;
	private int mPosY;

	public LeGridItem(Context context) {
		super(context);

		setClickable(true);
		setLongClickable(true);
		setWillNotDraw(false);
	}

	public int getPosX() {
		return mPosX;
	}

	public void setPosX(int posX) {
		mPosX = posX;
	}

	public int getPosY() {
		return mPosY;
	}

	public void setPosY(int posY) {
		mPosY = posY;
	}

	public void setIsInDrag(boolean isInDrag) {
		mIsInDrag = isInDrag;
		postInvalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int width = View.MeasureSpec.getSize(widthMeasureSpec);
		final int height = View.MeasureSpec.getSize(heightMeasureSpec);

		setMeasuredDimension(width, height);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		
	}
}
