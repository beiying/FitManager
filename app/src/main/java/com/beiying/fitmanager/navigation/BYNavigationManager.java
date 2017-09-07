package com.beiying.fitmanager.navigation;

import com.beiying.fitmanager.BYBasicContainer;
import com.beiying.fitmanager.R;
import com.beiying.fitmanager.framework.BYControlCenter;
import com.beiying.fitmanager.history.BYHistoryView;
import com.beiying.fitmanager.train.BYTrainBriefView;

public class BYNavigationManager extends BYBasicContainer{

	private static BYNavigationManager sInstance;
	private static int mSelectedId = 0;
	private BYNavigationManager() {
		
	}
	
	public static BYNavigationManager getInstance() {
		if (sInstance == null) {
			sInstance = new BYNavigationManager();
		}
		return sInstance;
		
	}
	
	public void onNavigationMenuItemClick(BYNavigationMenuItemView view, int id) {
		mSelectedId = id;
		BYControlCenter.getInstance().hideNavigationView();
		switch (id) {
		case BYNavigationMenuview.ID_TRAIN:
//			BYDataCollectContentView  dataCollectContentView = new BYDataCollectContentView(view.getContext());
			BYTrainBriefView trainBriefView = new BYTrainBriefView(view.getContext());
			BYControlCenter.getInstance().showInMainView(trainBriefView);
			break;
		case BYNavigationMenuview.ID_HISTORY:
			BYHistoryView historyView = new BYHistoryView(view.getContext());
			BYControlCenter.getInstance().showInMainView(historyView);
			break;
		case BYNavigationMenuview.ID_COMMUNITY:
			view.setSelectedIcon(R.drawable.nav_community_selected);
			break;
		case BYNavigationMenuview.ID_ANALYSIS:
			view.setSelectedIcon(R.drawable.nav_analysis_selected);
			break;
		case BYNavigationMenuview.ID_PRESCRIPTION:
			view.setSelectedIcon(R.drawable.nav_prescription_selected);
			break;
		case BYNavigationMenuview.ID_SETTING:
			view.setSelectedIcon(R.drawable.nav_setting_selected);
			break;
		}
	}
	
	public int getSelectedId() {
		return mSelectedId;
	}

	public void onUserProfileClick() {
		BYControlCenter.getInstance().hideNavigationView();
	}
}
