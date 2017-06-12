package com.beiying.fitmanager.history;

import android.content.Context;

import com.beiying.fitmanager.core.ui.LeListViewModel;
import com.beiying.fitmanager.core.ui.LeUI;
import com.beiying.fitmanager.core.ui.LeViewGroup;
import com.beiying.fitmanager.framework.model.BYBikeTrainModel;

public class BYHistoryView extends LeViewGroup{
	
	private LeListViewModel<BYBikeTrainModel> mListViewModel;

	private BYHistoryTotalView mHistoryTotalView;
	private BYHistoryListView mHistoryListView;
	
	public BYHistoryView(Context context) {
		super(context);
		
		initView(context);
	}
	
	private void initView(Context context) {
		mHistoryTotalView = new BYHistoryTotalView(context);
		addView(mHistoryTotalView);
		
		mListViewModel = new LeListViewModel<>();
		
		mHistoryListView = new BYHistoryListView(context, mListViewModel);
		BYBikeTrainModel model = new BYBikeTrainModel();
		model.mCalorie = 128;
		model.mDuration = 3600;
		model.mTime = System.currentTimeMillis();
		model.mDistance = 20;
		model.mHeartRate = 90;
		model.mSpeed = 4;
		model.mStrength = 516;
		mListViewModel.add(model);
		mListViewModel.add(model);
		mListViewModel.add(model);
		mListViewModel.add(model);
		mListViewModel.add(model);
		mListViewModel.add(model);
		mListViewModel.add(model);
		mListViewModel.add(model);
		mListViewModel.add(model);
		mListViewModel.add(model);
		
		addView(mHistoryListView);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int height = MeasureSpec.getSize(heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);

		LeUI.measureExactly(mHistoryTotalView, width, height);
		LeUI.measureExactly(mHistoryListView, width, height - mHistoryTotalView.getMeasuredHeight());
		
		setMeasuredDimension(width, height);
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		LeUI.layoutViewAtPos(mHistoryTotalView, 0, 0);
		LeUI.layoutViewAtPos(mHistoryListView, 0, mHistoryTotalView.getMeasuredHeight());
	}
	

}
