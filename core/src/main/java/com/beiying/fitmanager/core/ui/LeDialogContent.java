package com.beiying.fitmanager.core.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class LeDialogContent extends LeFrameViewGroup {
	
	private static final int UI_WIDTH = 296;
//	private static final int UI_WIDTH_AT_LARGE_SCREEN = 328;
	private static final int UI_MAX_HEIGHT = 209;
	private static final int UI_MIN_HEIGHT = 200;
	private static final int UI_TITLE_HEIGHT = 48;
	private static final int UI_MESSAGE_SIZE = 16;
	private static final int UI_PADDING = 24;
	private static final int UI_BUTTON_HEIGHT = 34;
	private static final int UI_BUTTON_WIDTH = 125;
	private static final int UI_BUTTON_GAP = 12;
	private static final int UI_BUTTON_PADDING_BOTTOM = 18;
	
	private static final String OK_STRING = "OK";
	private static final String CANCEL_STRING = "CANCEL";
	
	protected TextView mMessageView;
	private View mOkButton;
	private View mCancelButton;
	protected LeScrollView mScrollView;
	
	protected boolean mHasOkButton;
	protected boolean mHasCancelButton;

	protected int mWidth;
	protected int mContentWidth;
	protected int mTitleHeight;
	protected int mPadding;
	protected int mMaxHeight;
	
	protected int mButtonWidth;
	protected int mButtonHeight;
	private int mButtonGap;
	
	private String mTitle;

	public LeDialogContent(Context context) {
		super(context);
		
		initResource();
	
		initViews();
	}
	
	private void initResource() {
//		final DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
//		final int screenWidth = Math.min(dm.widthPixels, dm.heightPixels);
//		if (screenWidth > 320 * dm.density) {
//			mWidth = LeUI.getDensityDimen(getContext(), UI_WIDTH_AT_LARGE_SCREEN);			
//		} else {
//			mWidth = LeUI.getDensityDimen(getContext(), UI_WIDTH);
//		}
		mWidth = LeUI.getDensityDimen(getContext(), UI_WIDTH);
		mMaxHeight = LeUI.getDensityDimen(getContext(), UI_MAX_HEIGHT);
		mTitleHeight = LeUI.getDensityDimen(getContext(), UI_TITLE_HEIGHT);
		mPadding = LeUI.getDensityDimen(getContext(), UI_PADDING);
		mButtonWidth = LeUI.getDensityDimen(getContext(), UI_BUTTON_WIDTH);
		mButtonHeight = LeUI.getDensityDimen(getContext(), UI_BUTTON_HEIGHT);
		mButtonGap = LeUI.getDensityDimen(getContext(), UI_BUTTON_GAP);
		mContentWidth = mWidth - mPadding * 2;
	}
	
	private void initViews() {
		mMessageView = new TextView(getContext());
		mMessageView.setTextSize(UI_MESSAGE_SIZE);
		mMessageView.setPadding(0, 0, 0, 0);
		addView(mMessageView);
		
		Button button = new Button(getContext());
		button.setText(OK_STRING);
		button.setTextColor(Color.BLACK);
		mOkButton = button;
		addView(mOkButton);
		
		button = new Button(getContext());
		button.setText(CANCEL_STRING);
		button.setTextColor(Color.BLACK);
		mCancelButton = button;
		addView(mCancelButton);
	}
	
	public String getTitle() {
		return mTitle;
	}
	
	public void setTitle(int titleResId) {
		setTitle(getResources().getString(titleResId));
	}
	
	public void setTitle(String title) {
		mTitle = title;
	}
	
	public void setMessage(int messageResId) {
		setMessage(getResources().getString(messageResId));
	}
	
	public void setMessage(String message) {
		mMessageView.setText(message);
		
		measureMessageView();
		
		if (isNeedScrollview()) {
			removeView(mMessageView);
			mScrollView = new LeScrollView(getContext());
			addView(mScrollView);
			LeUI.removeFromParent(mMessageView);
			mScrollView.addView(mMessageView);
		}
	}
	
	public void setHasOkButton(boolean hasOkButton)	{
		mHasOkButton = hasOkButton;
		if (mHasOkButton) {
			mOkButton.setVisibility(View.VISIBLE);
		} else {
			mOkButton.setVisibility(View.GONE);
		}
	}
	
	public void setHasCancelButton(boolean hasCancelButton) {
		mHasCancelButton = hasCancelButton;
		if (mHasCancelButton) {
			mCancelButton.setVisibility(View.VISIBLE);
		} else {
			mCancelButton.setVisibility(View.GONE);
		}
	}
	
	public View getOkButton() {
		return mOkButton;
	}
	
	public void setOkButton(View okButton) {
		removeView(mOkButton);
		mOkButton = okButton;
		addView(mOkButton);
	}
	
	public View getCancelButton() {
		return mCancelButton;
	}
	
	public void setCancelButton(View cancelButton) {
		removeView(mCancelButton);
		mCancelButton = cancelButton;
		addView(mCancelButton);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int height = 0;
		
		if (!mHasOkButton && !mHasCancelButton) {
			mButtonHeight = 0;
		} 

		height = mTitleHeight + mMessageView.getMeasuredHeight() + mButtonHeight + mPadding;
		
		measureMessageView();
	
		if (hasButtonArea()) {
			LeUI.measureExactly(mOkButton, mButtonWidth, mButtonHeight);
			LeUI.measureExactly(mCancelButton, mButtonWidth, mButtonHeight);
		}
		
		final int minHeight = LeUI.getDensityDimen(getContext(), UI_MIN_HEIGHT);
		if (height < minHeight) {
			height = minHeight;
		}
		setMeasuredDimension(mWidth, height);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int offsetX, offsetY;
		offsetY = 0;
		
		offsetY += mTitleHeight;

		if (mMessageView != null) {
			offsetX = (getMeasuredWidth() - mMessageView.getMeasuredWidth()) / 2;
			offsetY = offsetY
					+ (getMeasuredHeight() - mPadding - mButtonHeight - offsetY - mMessageView
							.getMeasuredHeight()) / 2;
			if (mMessageView != null && indexOfChild(mMessageView) >= 0) {
				LeUI.layoutViewAtPos(mMessageView, offsetX, offsetY);
			}
		}
		
		if (hasButtonArea()) {
			int buttonTotalWidth = 0;
			if (mOkButton.getVisibility() == View.VISIBLE) {
				buttonTotalWidth += mOkButton.getMeasuredWidth();
			}
			if (mCancelButton.getVisibility() == View.VISIBLE) {
				buttonTotalWidth += mCancelButton.getMeasuredWidth() + mButtonGap;
			}
			offsetX = (getMeasuredWidth() - buttonTotalWidth) / 2;
			offsetY = getMeasuredHeight() - LeUI.getDensityDimen(getContext(), UI_BUTTON_PADDING_BOTTOM) - mButtonHeight;
			if (mOkButton.getVisibility() == View.VISIBLE) {
				LeUI.layoutViewAtPos(mOkButton, offsetX, offsetY);
				offsetX += mButtonGap;
			}
			offsetX += mOkButton.getMeasuredWidth();
			if (mCancelButton.getVisibility() == View.VISIBLE) {
				LeUI.layoutViewAtPos(mCancelButton, offsetX, offsetY);
			}
		}
	}
	
	protected boolean hasButtonArea() {
		return (mOkButton.getVisibility() != View.GONE || mCancelButton.getVisibility() != View.GONE);
	}

	private void measureMessageView() {
		int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(mContentWidth, View.MeasureSpec.EXACTLY);
		int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(mMaxHeight, View.MeasureSpec.UNSPECIFIED);
		mMessageView.measure(widthMeasureSpec, heightMeasureSpec);
	}
	
	protected boolean isNeedScrollview() {
		int contentHeight = mTitleHeight + mMessageView.getMeasuredHeight() + mPadding;
		if (hasButtonArea()) {
			contentHeight += mButtonHeight;
		}
		return (contentHeight > mMaxHeight);
	}
	
	public TextView getMessageView() {
		return mMessageView;
	}
	
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		return super.dispatchKeyEvent(event) || executeKeyEvent(event);
	}
	
	public boolean executeKeyEvent(KeyEvent event) {
		if (event.getAction() != KeyEvent.ACTION_DOWN) {
			return false;
		}
		
		View viewToFoucus = null;
		View currentFocused = findFocus();
		if (currentFocused == this) {
			currentFocused = null;
		}
		switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_DPAD_UP:
				viewToFoucus = FocusFinder.getInstance().findNextFocus(this, currentFocused, FOCUS_UP);
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				viewToFoucus = FocusFinder.getInstance().findNextFocus(this, currentFocused, FOCUS_DOWN);
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				viewToFoucus = FocusFinder.getInstance().findNextFocus(this, currentFocused, FOCUS_RIGHT);
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				viewToFoucus = FocusFinder.getInstance().findNextFocus(this, currentFocused, FOCUS_LEFT);
				break;
			default:
				return false;
		}

		if (viewToFoucus != null) {
			viewToFoucus.requestFocus();
		} else {
			currentFocused.requestFocus();
		}
		return true;
	}

}
