package com.beiying.fitmanager.datacollect;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;

import com.beiying.fitmanager.R;
import com.beiying.fitmanager.core.ui.LeUI;
import com.beiying.fitmanager.core.ui.LeView;
import com.beiying.fitmanager.core.utils.LeTextUtil;

public class BYKeyValueVerticalView extends LeView {
	public static final int COLOR_TEXT_NORMAL = Color.BLACK;
	private static final int COLOR_TEXT_FOCUSE = Color.GRAY;
	private static final int COLOR_TEXT_DISABLED = Color.GRAY;
	
	private static final int UI_TEXT_LEADING = 2;
	private static final int UI_WIDTH = 500;
	private static final int UI_HEIGHT = 100;
	
	private BYFitnessBikeMetaDataMode mMetaDataMode;
	private String mKey;
	private String mValue;

	private Paint mKeyPaint;
	private Paint mValuePaint;
	
	private TextSizeLevel mTextSizeLevel;
	private int mKeyNormalColor;
	private int mValueNormalColor;
	private int mDisableColor;
	private int mEditColor;
	private int mFocuseColor;
	
	private int mWidth;
	private int mHeight;
	
	public BYKeyValueVerticalView(Context context) {
		super(context);
	}
	
	public BYKeyValueVerticalView(Context context, BYFitnessBikeMetaDataMode metaData, TextSizeLevel textSizeLevel) {
		super(context);
		this.mMetaDataMode = metaData;
		this.mTextSizeLevel = textSizeLevel;
		
		this.mKey = metaData.getDescription();
		this.mValue = metaData.getValue();
		
		initResources();
	}
	private void initResources() {
		mKeyPaint = new Paint();
		mKeyPaint.setAntiAlias(true);
		mValuePaint = new Paint();
		mValuePaint.setAntiAlias(true);
		setTextSize();
	
		mKeyNormalColor = getResources().getColor(R.color.data_collect_key_text_color);
		mValueNormalColor = getResources().getColor(R.color.common_theme_color);
		mDisableColor = COLOR_TEXT_DISABLED;
		mFocuseColor = COLOR_TEXT_FOCUSE;
		
		setBackgroundColor(Color.WHITE);
	}

	private void setTextSize() {
		switch (mTextSizeLevel) {
		case SMALL:
			mKeyPaint.setTextSize(getResources().getDimension(R.dimen.text_size_small_medium));
			mValuePaint.setTextSize(getResources().getDimension(R.dimen.text_size_small_large));
			break;
		case NORMALL:
			mKeyPaint.setTextSize(getResources().getDimension(R.dimen.text_size_normal_medium));
			mValuePaint.setTextSize(getResources().getDimension(R.dimen.text_size_normal_large));
			break;
		case LARGE:
			mKeyPaint.setTextSize(getResources().getDimension(R.dimen.text_size_large_medium));
			mValuePaint.setTextSize(getResources().getDimension(R.dimen.text_size_large_large));
			break;
		}
	}
	
	
	public BYFitnessBikeMetaDataMode getMetaDataMode() {
		return mMetaDataMode;
	}

	public void setMetaDataMode(BYFitnessBikeMetaDataMode metaDataMode) {
		this.mMetaDataMode = metaDataMode;
		this.mKey = metaDataMode.getDescription();
		this.mValue = metaDataMode.getValue();
		postInvalidate();
	}

	public void setNormalColor(int mNormalColor) {
		this.mValueNormalColor = mNormalColor;
	}

	public void setDisableColor(int mDisableColor) {
		this.mDisableColor = mDisableColor;
	}

	public void setEditColor(int mEditColor) {
		this.mEditColor = mEditColor;
	}

	public void setFocuseColor(int mFocuseColor) {
		this.mFocuseColor = mFocuseColor;
	}

	public TextSizeLevel getTextSizeLevel() {
		return mTextSizeLevel;
	}
	
	public void setTextSizeLevel(TextSizeLevel textSizeLevel) {
		this.mTextSizeLevel = textSizeLevel;
		setTextSize();
		postInvalidate();
	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		
		int width = (int) (mWidth * dm.density);
		int height = (int) (mHeight * dm.density);
		
		if (width == 0 || height == 0) {
			
			int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
			int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);
			
			if (measuredHeight == 0 || measuredWidth == 0) {
				width = LeUI.getDensityDimen(getContext(), UI_WIDTH);
				height = LeUI.getDensityDimen(getContext(), UI_HEIGHT);
			} else {
				height = measuredHeight;
				width = measuredWidth;
			}
		}
		setMeasuredDimension(width, height);
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (!isEnabled()) {
			mValuePaint.setColor(mDisableColor);
			mKeyPaint.setColor(mDisableColor);
		} else {
			mValuePaint.setColor(mValueNormalColor);
			mKeyPaint.setColor(mKeyNormalColor);
		}
		
		int offsetX,offsetY;
		int valueFontHeight = LeTextUtil.getPaintHeight(mValuePaint);
		int keyFontHeight = LeTextUtil.getPaintHeight(mKeyPaint);
		
//		LeLog.e("LY KeyValueVerticalView onDraw  mValuePaint top="+mValuePaint.getFontMetrics().top+";bottom="+mValuePaint.getFontMetrics().bottom+";ascent="+mValuePaint.getFontMetrics().ascent+";descent="+mValuePaint.getFontMetrics().descent+";leading="+mValuePaint.getFontMetrics().leading);
//		LeLog.e("LY KeyValueVerticalView onDraw  mValuePaint top="+mKeyPaint.getFontMetrics().top+";bottom="+mKeyPaint.getFontMetrics().bottom+";ascent="+mKeyPaint.getFontMetrics().ascent+";descent="+mKeyPaint.getFontMetrics().descent+";leading="+mKeyPaint.getFontMetrics().leading);
		
		offsetX = LeTextUtil.calcXWhenAlignCenter(getMeasuredWidth(), mValuePaint, mValue);
		offsetY = (int) ((getMeasuredHeight() - valueFontHeight - keyFontHeight - UI_TEXT_LEADING)/2 - mValuePaint.getFontMetrics().top);
		canvas.drawText(mValue, offsetX, offsetY, mValuePaint);//drawText中的坐标是指文字的baseline的坐标
//		canvas.drawLine(0, offsetY, offsetX, offsetY, mValuePaint);
//		canvas.drawLine(0, offsetY + mValuePaint.getFontMetrics().top , offsetX, offsetY + mValuePaint.getFontMetrics().top, mValuePaint);
//		canvas.drawLine(0, offsetY + mValuePaint.getFontMetrics().top + mKeyPaint.getFontMetrics().bottom, offsetX, offsetY + mValuePaint.getFontMetrics().top + mKeyPaint.getFontMetrics().bottom, mValuePaint);
		
		offsetX = LeTextUtil.calcXWhenAlignCenter(getMeasuredWidth(), mKeyPaint, mKey);
		offsetY = (int) (offsetY - mKeyPaint.getFontMetrics().top);
		canvas.drawText(mKey, offsetX, offsetY, mKeyPaint);
//		canvas.drawLine(0, offsetY, offsetX, offsetY, mValuePaint);
//		canvas.drawLine(0, offsetY + mKeyPaint.getFontMetrics().top , offsetX, offsetY + mKeyPaint.getFontMetrics().top, mKeyPaint);
//		canvas.drawLine(0, offsetY + mKeyPaint.getFontMetrics().top + mKeyPaint.getFontMetrics().bottom, offsetX, offsetY + mKeyPaint.getFontMetrics().top + mKeyPaint.getFontMetrics().bottom, mKeyPaint);

	}
	public enum TextSizeLevel {
		SMALL,NORMALL,LARGE
	}
}
