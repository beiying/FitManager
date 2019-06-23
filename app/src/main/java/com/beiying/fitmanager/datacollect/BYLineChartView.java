package com.beiying.fitmanager.datacollect;

import android.content.Context;
import android.graphics.Color;

import com.beiying.fitmanager.R;
import com.beiying.fitmanager.core.LeLog;
import com.beiying.fitmanager.core.charts.BYLineChart;
import com.beiying.fitmanager.core.charts.data.BYEntry;
import com.beiying.fitmanager.core.charts.data.BYLineData;
import com.beiying.fitmanager.core.charts.data.BYLineDataSet;
import com.beiying.fitmanager.core.ui.LeUI;
import com.beiying.fitmanager.core.ui.LeView;

import java.util.ArrayList;

public class BYLineChartView extends LeView {
	private static final int UI_HEIGHT = 150;
	private BYLineChart mLineChart;
	
	private Context mContext;

	public BYLineChartView(Context context) {
		super(context);
		this.mContext = context;
		setBackgroundColor(getResources().getColor(R.color.data_collect_line_char_bg));
		
		initView();
	}

	private void initView() {
		mLineChart = new BYLineChart(mContext);
		
		addView(mLineChart);
		
		setData(45,100);
	}
	
	private void setData(int count, int range) {
		ArrayList<String> xVals = new ArrayList<String>();
		ArrayList<BYEntry> yVals = new ArrayList<BYEntry>();
		for (int i = 0;i < count;i++) {
			xVals.add(i + "");
			float mult = (range + 1);
			float val = (float) ((Math.random() * mult)) + 3;
			
			yVals.add(new BYEntry(val, i));
			LeLog.e("LY setData BYEntry ="+yVals.get(i).toString());
		}
		
		BYLineDataSet dataSet1 = new BYLineDataSet(yVals, "DataSet1");
		dataSet1.enableDashedLine(10f, 5f, 0f);
		dataSet1.setColor(Color.BLACK);
		dataSet1.setCircleColor(Color.BLACK);
		dataSet1.setLineWidth(1f);
		dataSet1.setCircleSize(4f);
		dataSet1.setFillAlpha(65);
		dataSet1.setFillColor(Color.BLACK);
		
		ArrayList<BYLineDataSet> dataSets = new ArrayList<BYLineDataSet>();
		dataSets.add(dataSet1);
		
		BYLineData lineData = new BYLineData(xVals, dataSets);
//		mLineChart.setData(lineData);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		
		if (height == 0) {
			height = LeUI.getDensityDimen(getContext(), UI_HEIGHT);
		}
		LeUI.measureExactly(mLineChart, width, height);
		
		setMeasuredDimension(width, height);
	}
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		LeUI.layoutViewAtPos(mLineChart, 0, 0);
	}

}
