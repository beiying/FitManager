package com.beiying.fitmanager.core.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.beiying.fitmanager.core.utils.LeTextUtil;


public class LeIconTextButton extends LeIconButton {
	
	private static final int UI_PADDING_X = 4;
	private static final int UI_PADDING_Y = 1;
	private static final int UI_ICON_TEXT_GAP = 2;
	
	public static final int COLOR_TEXT_NORMAL = Color.BLACK;
	private static final int COLOR_TEXT_PRESS = Color.GRAY;
	private static final int COLOR_TEXT_DISABLED = Color.GRAY;
	
	private int mPaddingX;
	private int mPaddingY;
	private int mIconTextGap;
	
	protected Paint mPaint;
	
	protected int mNormalColor;
	protected int mPressColor;
	private int mDisabledColor;
	
	private int mTextSize;
	protected String mText;

	public LeIconTextButton(Context context, int iconResId, String text, int textSize) {
		super(context);
		
		setIcon(context.getResources().getDrawable(iconResId));
		mText = text;
		mTextSize = textSize;
		
		initResources(context);
		
		setContentDescription("icontextbtn");
	}
	
	private void initResources(Context context) {

		mPaddingX = LeUI.getDensityDimen(context, UI_PADDING_X);
		mPaddingY = LeUI.getDensityDimen(context, UI_PADDING_Y);
		mIconTextGap = LeUI.getDensityDimen(context, UI_ICON_TEXT_GAP);
		
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setTextSize(LeUI.getDensityDimen(getContext(), mTextSize));
		
		mNormalColor = COLOR_TEXT_NORMAL;
		mPressColor = COLOR_TEXT_PRESS;
		mDisabledColor = COLOR_TEXT_DISABLED;
		mFocusedTextColor = DEFAULT_FOCUSED_COLOR;
		
	}
	
	public void setTextColor(int color) {
		mNormalColor = color;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int iconWidth = getIntrinsicWidth();
		final int iconHeight = getIntrinsicHeight();
		int width = (mPaddingX * 2 + iconWidth + mIconTextGap + LeTextUtil.getTextWidth(mPaint, mText));
		int height = (int) (mPaddingY * 2 + Math.max(iconHeight, mPaint.getTextSize()));
		
		setMeasuredDimension(width, height);
	}
	
	@Override
	protected int getIconOffsetX() {
		return mPaddingX;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if (!isEnabled()) {
			mPaint.setColor(mDisabledColor);
		} else if (isPressed()) {
			mPaint.setColor(mPressColor);
		} else if (isFocused()) {
			mPaint.setColor(mFocusedTextColor);
		} else {
			mPaint.setColor(mNormalColor);
		}
		int offsetX = (mPaddingX + getIntrinsicWidth() + mIconTextGap);
		int offsetY = LeTextUtil.calcYWhenAlignCenter(getMeasuredHeight(), mPaint);
		canvas.drawText(mText, offsetX, offsetY, mPaint);
	}
}
