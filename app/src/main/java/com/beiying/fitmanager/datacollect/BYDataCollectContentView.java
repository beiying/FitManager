package com.beiying.fitmanager.datacollect;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Chronometer;

import com.beiying.fitmanager.R;
import com.beiying.fitmanager.bluetooth.BYBluetoothManager;
import com.beiying.fitmanager.core.LeLog;
import com.beiying.fitmanager.core.ui.LeButton;
import com.beiying.fitmanager.core.ui.LeUI;
import com.beiying.fitmanager.core.ui.LeViewGroup;
import com.beiying.fitmanager.core.utils.LeTextUtil;
import com.beiying.fitmanager.datacollect.BYKeyValueVerticalView.TextSizeLevel;
import com.beiying.fitmanager.framework.BYDialog;
import com.beiying.fitmanager.framework.BYSplitLineDrawable;

public class BYDataCollectContentView extends LeViewGroup implements
		OnClickListener {
	private static final int UI_PADDING = 10;
	private static final int UI_BEGIN_BUTTON_HEIGHT = 60;
	private static final int UI_BEGIN_BUTTON_TOP = 10;

	private BYLineChartView mLineChart;
	private BYKeyValueVerticalView mFirstVerticalViewView;
	private BYKeyValueVerticalView mSecondVerticalView;
	private BYKeyValueVerticalView mThirdVerticalView;
	private BYKeyValueVerticalView mFourthVerticalView;
	private BYControlButton mControlBtn;
	private BYSplitLineDrawable mSplitLine;

	private Chronometer mChronometer;

	private int mPadding;
	private int mBeginBtnHeight;
	private int mBeginBtnTop;

	private Context mContext;

	private boolean isRun = false;

	public BYDataCollectContentView(Context context) {
		super(context);
		this.mContext = context;
		initResource();
		initViews();
		
		setWillNotDraw(false);
	}

	private void initResource() {
		mPadding = LeUI.getDensityDimen(mContext, UI_PADDING);
		mBeginBtnHeight = LeUI.getDensityDimen(mContext,
				UI_BEGIN_BUTTON_HEIGHT);
		mBeginBtnTop = LeUI.getDensityDimen(mContext, UI_BEGIN_BUTTON_TOP);

	}

	private void initViews() {
		
		mSplitLine = new BYSplitLineDrawable(mContext);
		mLineChart = new BYLineChartView(mContext);
		addView(mLineChart);

		mChronometer = new Chronometer(mContext);
		mChronometer.setBackgroundColor(Color.WHITE);
		mChronometer.setTextColor(getResources().getColor(
				R.color.common_theme_color));
		mChronometer.setGravity(Gravity.CENTER);
		mChronometer.setTextSize(getResources().getDimension(
				R.dimen.navigation_menu_text_size));
		addView(mChronometer);

		// BYFitnessBikeMetaDataMode metaDataMode1 = new
		// BYFitnessBikeMetaDataMode("时长", "00:00:00", "");
		// mFirstVerticalViewView = new BYKeyValueVerticalView(mContext,
		// metaDataMode1, TextSizeLevel.LARGE);
		// addView(mFirstVerticalViewView);

		BYFitnessBikeMetaDataMode metaDataMode2 = new BYFitnessBikeMetaDataMode(
				"里程", "0.00", "公里");
		mSecondVerticalView = new BYKeyValueVerticalView(mContext,
				metaDataMode2, TextSizeLevel.NORMALL);
		addView(mSecondVerticalView);

		BYFitnessBikeMetaDataMode metaDataMode3 = new BYFitnessBikeMetaDataMode(
				"卡路里", "0", "卡路里");
		mThirdVerticalView = new BYKeyValueVerticalView(mContext,
				metaDataMode3, TextSizeLevel.NORMALL);
		addView(mThirdVerticalView);

		BYFitnessBikeMetaDataMode metaDataMode4 = new BYFitnessBikeMetaDataMode(
				"速度", "00:00", "");
		mFourthVerticalView = new BYKeyValueVerticalView(mContext,
				metaDataMode4, TextSizeLevel.NORMALL);
		addView(mFourthVerticalView);

		mControlBtn = new BYControlButton(mContext);
		mControlBtn.setOnClickListener(this);
		addView(mControlBtn);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec) - 2 * mPadding;
		int height = MeasureSpec.getSize(heightMeasureSpec);
		LeLog.e("LY width=" + MeasureSpec.getSize(widthMeasureSpec)
				+ ";screen width="
				+ getResources().getDisplayMetrics().widthPixels);
		LeUI.measureExactly(mLineChart, width, height / 2 - 2 * mPadding
				- mBeginBtnHeight - mBeginBtnTop);
		LeUI.measureExactly(mChronometer, width, height / 4);
		// LeUtils.measureExactly(mFirstVerticalViewView, width, height / 4);
		LeUI.measureExactly(mSecondVerticalView, width / 3, height / 4);
		LeUI.measureExactly(mThirdVerticalView, width / 3, height / 4);
		LeUI.measureExactly(mFourthVerticalView, width / 3, height / 4);
		LeUI.measureExactly(mControlBtn, width, mBeginBtnHeight);

		setMeasuredDimension(width + 2 * mPadding, height);

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		LeLog.e("LY BYDataCollectContentView onLayout left=" + l + ";top=" + t
				+ ";right=" + r + ";bottom=" + b);
		int offsetX, offsetY;
		offsetX = mPadding;
		offsetY = mPadding;
		LeLog.e("LY onLayout mlineChart width=" + mLineChart.getMeasuredWidth());
		LeUI.layoutViewAtPos(mLineChart, offsetX, offsetY);

		offsetY += mLineChart.getMeasuredHeight();
		// LeUtils.layoutViewAtPos(mFirstVerticalViewView, offsetX, offsetY);
		LeUI.layoutViewAtPos(mChronometer, offsetX, offsetY);

		// offsetY += mFirstVerticalViewView.getMeasuredHeight();
		offsetY += mChronometer.getMeasuredHeight();
		LeUI.layoutViewAtPos(mSecondVerticalView, offsetX, offsetY);

		offsetX += mSecondVerticalView.getMeasuredWidth();
		LeUI.layoutViewAtPos(mThirdVerticalView, offsetX, offsetY);

		offsetX += mThirdVerticalView.getMeasuredWidth();
		LeUI.layoutViewAtPos(mFourthVerticalView, offsetX, offsetY);

		offsetY += mThirdVerticalView.getMeasuredHeight() + mBeginBtnTop;
		offsetX = mPadding;
		LeLog.e("LY onLayout mBeginBtn width=" + mControlBtn.getMeasuredWidth());
		LeUI.layoutViewAtPos(mControlBtn, offsetX, offsetY);

	}
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		mSplitLine.setBounds(mChronometer.getLeft(), mChronometer.getBottom(), mChronometer.getRight(), mChronometer.getBottom() + 1);
		mSplitLine.draw(canvas);
		
		mSplitLine.setBounds(mSecondVerticalView.getRight(), mSecondVerticalView.getTop(), mSecondVerticalView.getRight(), mSecondVerticalView.getBottom());
		mSplitLine.draw(canvas);
		
		mSplitLine.setBounds(mThirdVerticalView.getRight(), mThirdVerticalView.getTop(), mThirdVerticalView.getRight(), mThirdVerticalView.getBottom());
		mSplitLine.draw(canvas);
	}

	private class BYControlButton extends LeButton {
		private static final int UI_ROUND_SIZE = 3;
		private String mText;

		private Paint mPaint;
		private RectF mBgRectF;

		private int mRoundSize;

		public BYControlButton(Context context) {
			super(context);
			mPaint = new Paint();
			mBgRectF = new RectF();

			mRoundSize = LeUI.getDensityDimen(mContext, UI_ROUND_SIZE);

			mText = "开始";
		}

		public void setText(String text) {
			mText = text;
		}

		@Override
		protected void onDraw(Canvas canvas) {
			mBgRectF.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
			if (isPressed()) {
				mPaint.setColor(getResources().getColor(
						R.color.data_collect_begin_button_pressed_bg));
				canvas.drawRoundRect(mBgRectF, mRoundSize, mRoundSize, mPaint);
			} else {
				mPaint.setColor(getResources().getColor(
						R.color.data_collect_begin_button_normal_bg));
				canvas.drawRoundRect(mBgRectF, mRoundSize, mRoundSize, mPaint);
			}
			mPaint.reset();

			mPaint.setColor(Color.WHITE);
			mPaint.setTextSize(getResources().getDimension(
					R.dimen.text_size_normal_large));
			canvas.drawText(mText, LeTextUtil.calcXWhenAlignCenter(
					getMeasuredWidth(), mPaint, mText), LeTextUtil
					.calcYWhenAlignCenter(getMeasuredHeight(), mPaint), mPaint);
		}

	}

	@Override
	public void onClick(View v) {
		if (BYBluetoothManager.getInstance().getConnectionState() > BYBluetoothManager.STATE_DISCONNECTED) {
			if (isRun) {
				mChronometer.stop();
				isRun = false;
				mControlBtn.setText("开始");
			} else {
				mChronometer.setBase(SystemClock.elapsedRealtime());
				mChronometer.start();
				isRun = true;
				mControlBtn.setText("停止");
			}
		} else {
			showDialog();
		}
		

	}

	private void showDialog() {
		//弹出是否连接传输数据的蓝牙设备，是的话进入连接蓝牙设备的界面，否的话对话框消失
		BYDialog dialog = new BYDialog(getContext());
		
	}

}
