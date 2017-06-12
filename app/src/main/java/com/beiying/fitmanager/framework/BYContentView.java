package com.beiying.fitmanager.framework;

import android.content.Context;
import android.view.View;

import com.beiying.fitmanager.R;
import com.beiying.fitmanager.core.LeLog;
import com.beiying.fitmanager.core.ui.LeScrollView;
import com.beiying.fitmanager.core.ui.LeUI;
import com.beiying.fitmanager.core.ui.LeView;
import com.beiying.fitmanager.train.BYTrainBriefView;

import java.util.List;

public class BYContentView extends LeView {

//	private BYDataCollectContentView mDataCollectView;
//	private BYHistoryView mHistoryView;
	private View mView;
	private LeScrollView mScrollView;
	
	private List<View> mChildList;
	
	public BYContentView(Context context) {
		super(context);
		
		initResource();
		initView(context);
		
//		setWillNotDraw(false);
	}
	
	private void initResource() {
		setBackgroundColor(getResources().getColor(R.color.data_collect_content_view_bg));
	}
	
	private void initView(Context context) {
		
		
		mScrollView = new LeScrollView(context);
		addView(mScrollView);
		
//		mView = new BYUserProfileView(context);
//		mView = new BYDataCollectContentView(context);
		mView = new BYTrainBriefView(context);
		mScrollView.addView(mView);
		
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		LeUI.measureExactly(mScrollView, width, height);
		
		LeLog.e("LY ContentView width="+width+";height="+height);
		setMeasuredDimension(width, height);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		LeLog.e("LY BYContentView onLayout left="+left+";top="+top+";right="+right+";bottom="+bottom);
		LeUI.layoutViewAtPos(mScrollView, 0, 0);
	}
	
	public void showContentView(View view) {
		mScrollView.removeAllViews();
		mView = view;
		mScrollView.addView(view);
		requestLayout();
	}

}
