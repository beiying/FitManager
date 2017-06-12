package com.beiying.fitmanager.history;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;

import com.beiying.fitmanager.R;
import com.beiying.fitmanager.core.LeLog;
import com.beiying.fitmanager.core.ui.LeUI;
import com.beiying.fitmanager.core.ui.LeView;
import com.beiying.fitmanager.core.utils.LeDateUtils;
import com.beiying.fitmanager.core.utils.LeTextUtil;
import com.beiying.fitmanager.framework.model.BYBikeTrainModel;

public class BYHistoryItemView extends LeView{
	private static final String DATE_FORMAT_PATTERN = "EEE, yy/MM/dd";
	private static final int UI_PADDING_LEFT = 15;
	private static final int UI_PADDING_TOP = 8;
	private static final int UI_PADDING_BOTTOM = 8;
	private static final int UI_PADDING_RIGHT = 8;
	
	private Context mContext;
	private BYBikeTrainModel mModel;
	private String mTimeText;
	
	private Paint mPaint;
	private Drawable mBgDrawable;
	private Drawable mHistoryListIcon;
	private int mTextColor;
	private int mPaddingLeft;
	private int mPaddingTop;
	private int mPaddingBottom;
	private int mPaddingRight;
	
	public BYHistoryItemView(Context context) {
		super(context);
		mContext = context;
		
		initResource();
		setClickable(true);
		setWillNotDraw(false);
	}
	
	public void setModel(BYBikeTrainModel model) {
		this.mModel = model;
		invalidate();
	}
	
	private void initResource() {
		
		mPaint = new Paint();
		mPaint.setStyle(Style.FILL);
		mPaint.setAntiAlias(true);
		
		mBgDrawable = getResources().getDrawable(R.drawable.card_panel_bg);
		mHistoryListIcon = getResources().getDrawable(R.drawable.history_list_icon);
		mTextColor = getResources().getColor(R.color.common_theme_color);
		
		mPaddingTop = LeUI.getDensityDimen(mContext, UI_PADDING_TOP);
		mPaddingLeft = LeUI.getDensityDimen(mContext, UI_PADDING_LEFT);
		mPaddingBottom = LeUI.getDensityDimen(mContext, UI_PADDING_BOTTOM);
		mPaddingRight = LeUI.getDensityDimen(mContext, UI_PADDING_RIGHT);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = LeUI.getDensityDimen(getContext(), 50);
		int iconHeight = mHistoryListIcon.getIntrinsicHeight() + mPaddingBottom + mPaddingTop;
		LeLog.e("HistoryItemView icon height ="+iconHeight);
		height = iconHeight > height ? iconHeight : height;
		setMeasuredDimension(width, height);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		int offsetX = mPaddingLeft,offsetY = mPaddingTop;
		if (isPressed()) {
			canvas.drawColor(getResources().getColor(R.color.common_clicked_bg_color));
		} else {
			canvas.drawColor(Color.WHITE);
		}
		mHistoryListIcon.setBounds(offsetX, offsetY, offsetX + mHistoryListIcon.getIntrinsicWidth(), offsetY + mHistoryListIcon.getIntrinsicHeight());
		mHistoryListIcon.draw(canvas);
		
		offsetX += mHistoryListIcon.getIntrinsicWidth();
		mPaint.setColor(Color.BLACK);
		mPaint.setTextSize(getResources().getDimension(R.dimen.history_total_text_size));
		offsetY += LeTextUtil.getPaintHeight(mPaint);
		canvas.drawText(mModel.mCalorie+" 卡路里", offsetX, offsetY, mPaint);
		
		mPaint.setColor(Color.GRAY);
		offsetY += LeTextUtil.getPaintHeight(mPaint);
		canvas.drawText(getFormatDuration(mModel.mDuration), offsetX, offsetY, mPaint);
		
		mTimeText = LeDateUtils.format(mModel.mTime, DATE_FORMAT_PATTERN);
		offsetX = getMeasuredWidth() - mPaddingRight - (int)mPaint.measureText(mTimeText);
		canvas.drawText(mTimeText, offsetX, offsetY, mPaint);
		
	}
	
	private String getFormatDuration(long duration) {
		StringBuilder sb = new StringBuilder();
		int hour = (int) (duration / 3600);
		int minitues = (int) (duration % 3600 / 60);
		int seconds = (int) (duration % 3600 % 60);
		
		sb.append(getFormatTime(hour)).append(":").append(getFormatTime(minitues)).append(":").append(getFormatTime(seconds));
		return sb.toString();
	}
	
	private String getFormatTime(int t) {
		return t > 10 ? t + "" : "0" + t;
	}
	
	class BYHistorySingleItemView extends LeView {
		private static final int UI_SINGLE_ITEM_HEIGHT = 40;
		
		private Paint mTextPaint;
		private int mHeight;
		
		public BYHistorySingleItemView(Context context) {
			super(context);
			mPaint = new Paint();
			mHeight = LeUI.getDensityDimen(mContext, UI_SINGLE_ITEM_HEIGHT);
		}
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			int width = MeasureSpec.getSize(widthMeasureSpec);
			int height = mHeight;
			
			setMeasuredDimension(width, height);
		}
		@Override
		protected void onDraw(Canvas canvas) {
			
		}
	}

}
