package com.beiying.fitmanager.framework.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;

import com.beiying.fitmanager.R;
import com.beiying.fitmanager.core.ui.LeDialogContent;
import com.beiying.fitmanager.core.ui.LeTextButton;
import com.beiying.fitmanager.core.ui.LeUI;
import com.beiying.fitmanager.core.utils.LeTextUtil;
import com.beiying.fitmanager.framework.BYSplitLineDrawable;

public class BYFrameDialogContent extends LeDialogContent {

	private static final int UI_HEIGHT = 209;
	private static final int UI_LINE_PADDING_BOTTOM = 78;
	private static final int UI_BUTTON_TEXT = 17;
	private static final int UI_TITLE_HEIGHT = 60;
	private static final int UI_BUTTON_AREA_HEIGHT = 60;
	private static final int UI_SHADOW_X = 0;
	private static final int UI_SHADOW_Y = 0;
	private static final int UI_VERTICAL_SPLIT_LINE_HEIHGT = 28;
	private static final int UI_SPLITLINE_COLOR = 0xFFA3A3A3;
	private static final int UI_SPLITLINE_COLOR_NIGHT = 0xFF757575;
	
	protected int mLinePaddingBottom;
	private int mShadowX;
	private int mShadowY;
	private int mLineColor;
	private int mLineColorNight;
	
	protected Drawable mBgDrawable;
	private BYSplitLineDrawable mSplitLineDrawable;
	private Paint mTitlePaint;
	
	protected LeFrameDialogButton mOkButton;
	protected LeFrameDialogButton mCancelButton;
	

	public BYFrameDialogContent(Context context) {
		super(context);
		
		setWillNotDraw(false);
		
		initResoureces();
		
		initViews();
		
		onThemeChanged();
	}
	
	private void initResoureces() {
		mPadding = LeDimen.getPadding();
		mLinePaddingBottom = LeUI.getDensityDimen(getContext(), UI_LINE_PADDING_BOTTOM);
		mShadowX = LeUI.getDensityDimen(getContext(), UI_SHADOW_X);
		mShadowY = LeUI.getDensityDimen(getContext(), UI_SHADOW_Y);
		mTitleHeight = LeUI.getDensityDimen(getContext(), UI_TITLE_HEIGHT);
		mLineColor = UI_SPLITLINE_COLOR;
		mLineColorNight = UI_SPLITLINE_COLOR_NIGHT;
		mTitlePaint = new Paint();
		mTitlePaint.setTextSize(R.dimen.common_title);
		mTitlePaint.setColor(getResources().getColor(R.color.common_title));
	}
	
	private void initViews() {
		mOkButton = new LeFrameDialogButton(getContext(), R.string.common_ok);
		mOkButton.setTextSize(R.dimen.dialog_title_text_size);
		mOkButton.setFocusable(true);
		setOkButton(mOkButton);
		
		mCancelButton = new LeFrameDialogButton(getContext(), R.string.common_cancel);
		mCancelButton.setTextSize(R.dimen.dialog_title_text_size);
		mCancelButton.setFocusable(true);
		setCancelButton(mCancelButton);
		
		mHasOkButton = true;
		mHasCancelButton = true;
		
		mMessageView.setGravity(Gravity.CENTER);
		mMessageView.setTextSize(TypedValue.COMPLEX_UNIT_PX, LeDimen.getTextSize(R.dimen.common_text_normal_size));
		
		mSplitLineDrawable = new BYSplitLineDrawable(getContext());
		mSplitLineDrawable.setLineColor(mLineColor);
	}
	
	@Override
	public void onThemeChanged() {
		mBgDrawable = getResources().getDrawable(R.drawable.unit_bg);
		mSplitLineDrawable.setLineColor(mLineColor);

		mMessageView.setTextColor(getResources().getColor(R.color.common_text));
	}

	public void setPositiveButtonText(int resId) {
		((LeTextButton) getOkButton()).setText(resId);
	}
	
	public void setPositiveButtonText(String text) {
		((LeTextButton) getOkButton()).setText(text);
	}
	
	public void setNegativeButtonText(int resId) {
		((LeTextButton) getCancelButton()).setText(resId);
	}
	
	public void setNegativeButtonText(String text) {
		((LeTextButton) getCancelButton()).setText(text);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		int height = LeUI.getDensityDimen(getContext(), UI_HEIGHT);
		
		mButtonHeight = LeUI.getDensityDimen(getContext(), UI_BUTTON_AREA_HEIGHT);
		if (!mHasOkButton && !mHasCancelButton) {
			height -= mButtonHeight;
			mButtonHeight = 0;
		} else if (!mHasOkButton && mHasCancelButton) {
			LeUI.measureExactly(mCancelButton, mWidth, mButtonHeight);
		} else if (mHasOkButton && !mHasCancelButton) {
			LeUI.measureExactly(mOkButton, mWidth, mButtonHeight);
		} else {
			mButtonWidth = mWidth / 2;
			LeUI.measureExactly(mOkButton, mButtonWidth, mButtonHeight);
			LeUI.measureExactly(mCancelButton, mButtonWidth, mButtonHeight);
		}
		setMeasuredDimension(mWidth, height);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		
		int offsetX, offsetY;
		offsetY = mTitleHeight + LeUI.getDensityDimen(getContext(), 8);
		
		offsetX = 0;
		offsetY = getMeasuredHeight() - mButtonHeight;
		if (mHasCancelButton) {
			LeUI.layoutViewAtPos(mCancelButton, offsetX, offsetY);
			offsetX += mButtonWidth;
		}
		if (mHasOkButton) {
			LeUI.layoutViewAtPos(mOkButton, offsetX, offsetY);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int width = getMeasuredWidth();
		mBgDrawable.setBounds(0, 0, width, getMeasuredHeight());
		mBgDrawable.draw(canvas);
		
		final int titleHeight = LeUI.getDensityDimen(getContext(), UI_TITLE_HEIGHT);
		mSplitLineDrawable.setBounds(mShadowX, titleHeight, getMeasuredWidth() - mShadowX, titleHeight);
		mSplitLineDrawable.draw(canvas);
		
		int offsetX, offsetY;
		offsetX = 0;
		final String title = getTitle();
		if (title != null) {
			int titleTextWidth = (int) mTitlePaint.measureText(title);
			if (titleTextWidth > width) {
				mTitlePaint.setTextSize(LeUI.getDensityDimen(getContext(), 17));
			}
			offsetX = LeTextUtil.calcXWhenAlignCenter(getMeasuredWidth(), mTitlePaint, title);
			offsetY = mShadowY + LeTextUtil.calcYWhenAlignCenter(titleHeight - mShadowY, mTitlePaint);
			canvas.drawText(title, offsetX, offsetY, mTitlePaint);
		}
		
		final int buttonAreaHeight = LeUI.getDensityDimen(getContext(), UI_BUTTON_AREA_HEIGHT);
		offsetY = getMeasuredHeight() - buttonAreaHeight;
		if (mHasCancelButton || mHasOkButton) {
			mSplitLineDrawable.setBounds(mShadowX, offsetY, getMeasuredWidth() - mShadowX, offsetY);
			mSplitLineDrawable.draw(canvas);
		}
		
		if (mHasOkButton && mHasCancelButton) {
			final int lineHeight = LeUI.getDensityDimen(getContext(), UI_VERTICAL_SPLIT_LINE_HEIHGT);
			offsetY += (buttonAreaHeight - lineHeight) / 2;
			offsetX = getMeasuredWidth() / 2;
			mSplitLineDrawable.setBounds(offsetX, offsetY, offsetX, offsetY + lineHeight);
			mSplitLineDrawable.draw(canvas);
		}
	}
	
	public class LeFrameDialogButton extends LeTextButton {

		public LeFrameDialogButton(Context context, int textResId) {
			super(context, textResId);
			
			setTextSize(UI_BUTTON_TEXT);
			onThemeChanged();
		}


		@Override
		protected void onDraw(Canvas canvas) {
			if (isPressed()) {
				canvas.drawColor(getResources().getColor(R.color.common_press_bg));
			} else if (isFocused()) {
				canvas.drawColor(getResources().getColor(R.color.common_contrast));
			}
			super.onDraw(canvas);
		}
	}

}
