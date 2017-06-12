package com.beiying.fitmanager.history;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.View;

import com.beiying.fitmanager.R;
import com.beiying.fitmanager.core.LeLog;
import com.beiying.fitmanager.core.ui.LeGallery.LeGalleryListener;
import com.beiying.fitmanager.core.ui.LeUI;
import com.beiying.fitmanager.core.ui.LeView;
import com.beiying.fitmanager.core.utils.LeTextUtil;

import java.util.HashMap;
import java.util.Map;

public class BYHistoryTotalView extends LeView implements LeGalleryListener {

	private static final int UI_HEIGHT = 150;
	private static final int UI_TEXT_PADDING_LEFT = 50;
	private static final int UI_TEXt_PADDING_RIGHT = 15;
	private static final int UI_GALARY_HEIGHT = 100;
	
	private HashMap<Integer, HashMap<String, Object>> mProperties;
	
	private BYHistoryParameterGalary mHistoryParameterGalary;
	private BYHistoryTotalIndicator mHistoryTotalIndicator;
	
	private String mMonth;
	private String mActivity;
	
	private Paint mTextPaint;
	private int mPaddingLeft;
	private int mPaddingRight;
	private int mHeight;
	private int mGalaryHeight;
	public BYHistoryTotalView(Context context) {
		super(context);
		setBackgroundColor(getResources().getColor(R.color.common_theme_color));
		
		initView(context);
		initResource(context);
//		setWillNotDraw(false);
	}
	
	private void initView(Context context) {
		HashMap<String, Object> property = new HashMap<String, Object>();
		mProperties = new HashMap<Integer, HashMap<String,Object>>();
		property.put("距离", "0公里");
		property.put("时长", "00:00:00");
		property.put("卡路里", "7723");
		property.put("平均速度", "128");
		property.put("平均心率", "80");
		mProperties.put(0, property);
		
		mHistoryParameterGalary = new BYHistoryParameterGalary(context);
		for(Map.Entry<String, Object> entry : property.entrySet()) {
			BYHistoryPropertiesTotalView view = new BYHistoryPropertiesTotalView(context, entry.getKey(), entry.getValue().toString());
			mHistoryParameterGalary.addView(view);
		}
		mHistoryParameterGalary.setDefaultScreen(0);
		mHistoryParameterGalary.addGalleryChangeListener(this);
		addView(mHistoryParameterGalary);
		
		mHistoryTotalIndicator = new BYHistoryTotalIndicator(context, property.size());
		addView(mHistoryTotalIndicator);
	}

	private void initResource(Context context) {
		mTextPaint = new Paint();
		mTextPaint.setStyle(Style.FILL);
		mTextPaint.setColor(Color.WHITE);
		mTextPaint.setTextSize(getResources().getDimension(R.dimen.history_total_text_size));
		
		mPaddingLeft = LeUI.getDensityDimen(context, UI_TEXT_PADDING_LEFT);
		mPaddingRight = LeUI.getDensityDimen(context, UI_TEXt_PADDING_RIGHT);
		mHeight = LeUI.getDensityDimen(context, UI_HEIGHT);
		mGalaryHeight = LeUI.getDensityDimen(context, UI_GALARY_HEIGHT);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int height = mHeight;
		int width = MeasureSpec.getSize(widthMeasureSpec);

		LeUI.measureExactly(mHistoryParameterGalary, width, mGalaryHeight);
		LeUI.measureExactly(mHistoryTotalIndicator, width, height);
		LeLog.e("TotalView onMeasure height="+height+";width="+width);
		setMeasuredDimension(width, height);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		LeLog.e("TotalView onLayout");
		LeUI.layoutViewAtPos(mHistoryParameterGalary, 0, 0);
		LeUI.layoutViewAtPos(mHistoryTotalIndicator, 0, mHistoryParameterGalary.getMeasuredHeight());
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		LeLog.e("TotalView onDraw");
		super.onDraw(canvas);
		
		int offsetX = mPaddingLeft;
		int offsetY = mHistoryParameterGalary.getMeasuredHeight() + mHistoryTotalIndicator.getMeasuredHeight() + LeTextUtil.getPaintHeight(mTextPaint);
		
		canvas.drawText("四月", offsetX, offsetY, mTextPaint);
		
		canvas.drawText("1活动", getMeasuredWidth() - mPaddingRight - mTextPaint.measureText("1活动"), offsetY, mTextPaint);
		
	}
	
	class BYHistoryPropertiesTotalView extends LeView {
		private String mKey;
		private String mValue;
		
		private Paint mPaint;

		public BYHistoryPropertiesTotalView(Context context,String key, String value) {
			super(context);
			
			mKey = key;
			mValue = value;
			
			mPaint = new Paint();
			mPaint.setStyle(Style.FILL);
			mPaint.setColor(Color.WHITE);
			mPaint.setAntiAlias(true);
			
			this.setWillNotDraw(false);
		}
		
		@Override
		protected void onDraw(Canvas canvas) {
			LeLog.e("LY BYHistoryPropertiesTotalView onDraw");
			int offsetX = mPaddingLeft;
			int offsetY = 0;
			
			mPaint.setTextSize(getResources().getDimension(R.dimen.history_total_key_size));
			offsetY += LeTextUtil.getPaintHeight(mPaint);
			canvas.drawText(mKey, offsetX, offsetY, mPaint);
			
			mPaint.setTextSize(getResources().getDimension(R.dimen.history_total_value_size));
			offsetY += LeTextUtil.getPaintHeight(mPaint);
			canvas.drawText(mValue, offsetX, offsetY, mPaint);
			
		}
		
	}
	
	class BYHistoryTotalIndicator extends LeView {
		private static final int INDICATOR_GAP = 10;
		private static final int INDICATOR_HEIGHT = 4;
		private int mCount;
		private int mCurrentSelectedIndex = 0;
		private int mGap;
		private int mHeight;
		
		private Paint mIndicatorPaint;
		
		public BYHistoryTotalIndicator(Context context, int count) {
			this(context);
			mCount = count;
		}
		public BYHistoryTotalIndicator(Context context) {
			super(context);
			mGap = LeUI.getDensityDimen(context, INDICATOR_GAP);
			mHeight = LeUI.getDensityDimen(context, INDICATOR_HEIGHT);
			mIndicatorPaint = new Paint();
			mIndicatorPaint.setStyle(Style.FILL);
			mIndicatorPaint.setAntiAlias(true);
			
			setWillNotDraw(false);
		}
		
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			int width = MeasureSpec.getSize(widthMeasureSpec);
			int height = mHeight;
			
			setMeasuredDimension(width, height);
		}
		
		@Override
		protected void onDraw(Canvas canvas) {
			int offsetX = mPaddingLeft;
			
			for (int i = 0; i < mCount;i++) {
				if (i == mCurrentSelectedIndex) {
					mIndicatorPaint.setColor(Color.WHITE);
				} else {
					mIndicatorPaint.setColor(getResources().getColor(R.color.history_indicator_normal_color));
				}
				canvas.drawCircle(mHeight / 2 + offsetX, mHeight / 2, mHeight / 2, mIndicatorPaint);
				offsetX += mGap;
			}
		}
		
		public void setCount(int count) {
			this.mCount = count;
		}
		
		public void setCurrentSelectedIndex(int index) {
			if (index < mCount) {
				this.mCurrentSelectedIndex = index;
			} else {
				this.mCurrentSelectedIndex = 0;
			}
			invalidate();
		}
		
	}

	@Override
	public void onGalleryScreenChanged(View view, int screen) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGalleryScreenChangeComplete(View view, int screen) {
		mHistoryTotalIndicator.setCurrentSelectedIndex(screen);
		
	}

	@Override
	public void onGalleryScrolled(int l, int t, int oldl, int oldt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onXChange(int delta) {
		
	}

}
