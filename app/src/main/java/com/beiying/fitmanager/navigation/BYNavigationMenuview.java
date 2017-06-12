package com.beiying.fitmanager.navigation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;

import com.beiying.fitmanager.R;
import com.beiying.fitmanager.core.ui.LeUI;
import com.beiying.fitmanager.core.ui.LeViewGroup;
import com.beiying.fitmanager.core.utils.LeUtils;
import com.beiying.fitmanager.framework.BYSplitLineDrawable;

public class BYNavigationMenuview extends LeViewGroup implements OnClickListener {
	public static final int ID_COLLECT = 0;
	public static final int ID_HISTORY = 1;
	public static final int ID_COMMUNITY = 2;
	public static final int ID_ANALYSIS = 3;
	public static final int ID_PRESCRIPTION = 4;
	public static final int ID_SETTING = 5;
	
	private BYNavigationMenuItemView mCollectPage;
	private BYNavigationMenuItemView mHistoryPage;
	private BYNavigationMenuItemView mCommunityPage;
	private BYNavigationMenuItemView mAnalysisPage;
	private BYNavigationMenuItemView mSportPrescriptionPage;
	private BYNavigationMenuItemView mSettingPage;
	
	
	private BYSplitLineDrawable mLineDrawable;
	
	public BYNavigationMenuview(Context context) {
		super(context);
		setBackgroundColor(Color.WHITE);
		
		mCollectPage = new BYNavigationMenuItemView(context);
		mCollectPage.setNormalIcon(R.drawable.nav_collect);
		mCollectPage.setSelectedIcon(R.drawable.nav_collect_selected);
		mCollectPage.setText(R.string.navdrawer_item_train);
		mCollectPage.setOnClickListener(this);
		mCollectPage.setId(ID_COLLECT);
		addView(mCollectPage);
		
		mHistoryPage = new BYNavigationMenuItemView(context);
		mHistoryPage.setNormalIcon(R.drawable.nav_history);
		mHistoryPage.setSelectedIcon(R.drawable.nav_history_selected);
		mHistoryPage.setText(R.string.navdrawer_item_history);
		mHistoryPage.setOnClickListener(this);
		mHistoryPage.setId(ID_HISTORY);
		addView(mHistoryPage);
		
		mCommunityPage = new BYNavigationMenuItemView(context);
		mCommunityPage.setNormalIcon(R.drawable.nav_community);
		mCommunityPage.setSelectedIcon(R.drawable.nav_community_selected);
		mCommunityPage.setText(R.string.navdrawer_item_competition);
		mCommunityPage.setOnClickListener(this);
		mCommunityPage.setId(ID_COMMUNITY);
		addView(mCommunityPage);
		
		mAnalysisPage = new BYNavigationMenuItemView(context);
		mAnalysisPage.setNormalIcon(R.drawable.nav_analysis);
		mAnalysisPage.setSelectedIcon(R.drawable.nav_analysis_selected);
		mAnalysisPage.setText(R.string.navdrawer_item_analysis);
		mAnalysisPage.setOnClickListener(this);
		mAnalysisPage.setId(ID_ANALYSIS);
		addView(mAnalysisPage);
		
		mSportPrescriptionPage = new BYNavigationMenuItemView(context);
		mSportPrescriptionPage.setNormalIcon(R.drawable.nav_prescription);
		mSportPrescriptionPage.setSelectedIcon(R.drawable.nav_prescription_selected);
		mSportPrescriptionPage.setText(R.string.navdrawer_item_prescription);
		mSportPrescriptionPage.setOnClickListener(this);
		mSportPrescriptionPage.setId(ID_PRESCRIPTION);
		addView(mSportPrescriptionPage);
		
		mSettingPage= new BYNavigationMenuItemView(context);
		mSettingPage.setNormalIcon(R.drawable.nav_setting);
		mSettingPage.setSelectedIcon(R.drawable.nav_setting_selected);
		mSettingPage.setText(R.string.navdrawer_item_setting);
		mSettingPage.setOnClickListener(this);
		mSettingPage.setId(ID_SETTING);
		addView(mSettingPage);
		
		mLineDrawable = new BYSplitLineDrawable(context);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		
		for(int i = 0;i < getChildCount();i++) {
			View view = getChildAt(i);
			LeUI.measureExactly(view, width, height);
		}
		
		setMeasuredDimension(width, height);
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int offsetX = 0;
		int offsetY = 0;
		
		for(int i = 0;i < getChildCount();i++) {
			View view = getChildAt(i);
			LeUI.layoutViewAtPos(view, offsetX, offsetY);
			offsetY += view.getMeasuredHeight();
		}
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		
		int offsetY = 0;
		for(int i = 0;i<getChildCount();i++) {
			offsetY += getChildAt(i).getMeasuredHeight();
			mLineDrawable.setBounds(0, offsetY, getMeasuredHeight(), offsetY + 1);
			mLineDrawable.draw(canvas);
		}
	}

	@Override
	public void onClick(View v) {
		BYNavigationManager.getInstance().onNavigationMenuItemClick((BYNavigationMenuItemView)v,v.getId());
		for(int i = 0;i < getChildCount();i++) {
			getChildAt(i).invalidate();
		}
	}
	

}
