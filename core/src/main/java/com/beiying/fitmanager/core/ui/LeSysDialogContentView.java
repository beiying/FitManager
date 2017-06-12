package com.beiying.fitmanager.core.ui;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.beiying.fitmanager.core.utils.LeMachineHelper;


public class LeSysDialogContentView extends ViewGroup {
	private static final int UI_SCROLLVIEW_MAX_HEIGHT = 148;
	private static final int UI_MSG_PADDING = 24;
	private static final int UI_MESSAGE_SIZE = 14;
	
	private static final int UI_CHECKBOX_HEIGHT = 60;
	private static final int UI_CHECKBOX_PADDING_LEFT = 24;

	private TextView mMessageView;
	private int mMsgPadding;
	private CheckBox mCheckBox;
	private LeScrollView mScrollView;
	private int mMsgHeight;

	public LeSysDialogContentView(Context context) {
		super(context);
//		mContext = context;
		
		mMessageView = new TextView(getContext());
		mMessageView.setTextSize(UI_MESSAGE_SIZE);
		if (!LeMachineHelper.isMachineAPad()) {
			mMessageView.setTextColor(0x89000000);
		}
		mMsgPadding = LeUI.getDensityDimen(getContext(), UI_MSG_PADDING);
		mMessageView.setPadding(mMsgPadding, 0, mMsgPadding, 0);
		
		mScrollView = new LeScrollView(getContext());
		mScrollView.addView(mMessageView);
		addView(mScrollView);
	}
	
	public void addSingleChoice(String text, boolean isChecked, CheckBox.OnCheckedChangeListener listener) {
		mCheckBox = new CheckBox(getContext());
		mCheckBox.setText(text);
		if (!LeMachineHelper.isMachineAPad()) {
			mCheckBox.setTextColor(0x89000000);
		}
		mCheckBox.setPadding(LeUI.getDensityDimen(getContext(), 12), 0, 0, 0);
		mCheckBox.setChecked(isChecked);
		mCheckBox.setOnCheckedChangeListener(listener);
		addView(mCheckBox);
	}
	
	

	public void setMessage(int messageResId) {
		setMessage(getResources().getString(messageResId));
	}
	
	public void setMessage(String message) {
		mMessageView.setText(message);
	}

	public void setMessage(CharSequence msg) {
		mMessageView.setText(msg);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = 0;
		
		mMessageView.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(),
				MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(0,
				MeasureSpec.UNSPECIFIED));
		
		int maxScrollViewHeight = LeUI.getDensityDimen(getContext(), UI_SCROLLVIEW_MAX_HEIGHT);
		mMsgHeight = Math.min(maxScrollViewHeight,
				mMessageView.getMeasuredHeight());
		LeUI.measureExactly(mScrollView, width, mMsgHeight);
		height += mMsgHeight;
		
		if (mCheckBox != null) {
			final int checkboxHeight = LeUI.getDensityDimen(getContext(), UI_CHECKBOX_HEIGHT);
			LeUI.measureExactly(mCheckBox, width, checkboxHeight);
			height += checkboxHeight;
		}
		
		setMeasuredDimension(width,  height);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int offsetX, offsetY;
		offsetX = 0;
		offsetY = 0;
		LeUI.layoutViewAtPos(mScrollView, offsetX, offsetY);
		
		if (mCheckBox != null) {
			offsetY += mMsgHeight;
			offsetX = LeUI.getDensityDimen(getContext(), UI_CHECKBOX_PADDING_LEFT);
			LeUI.layoutViewAtPos(mCheckBox, offsetX, offsetY);
		}
	}
	
}
