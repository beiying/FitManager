package com.beiying.fitmanager.navigation;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import com.beiying.fitmanager.core.LeLog;
import com.beiying.fitmanager.core.ui.LeUI;
import com.beiying.fitmanager.core.ui.LeView;
import com.beiying.fitmanager.framework.model.BYUserModel;

public class BYNavigationView extends LeView implements OnClickListener {
	private BYNavigationUserView mUserProfileView;
	private BYNavigationMenuview mNavigationMenuview;
	public BYNavigationView(Context context) {
		super(context);
		
		initResource();
		
		initView(context);
	}

	private void initResource() {
	}

	private void initView(Context context) {
		BYUserModel userModel = new BYUserModel();
		userModel.mUserName = "beiying";
		userModel.mHeight = 185;
		userModel.mWeight = 75;
		mUserProfileView = new BYNavigationUserView(context, userModel);
		mUserProfileView.setClickable(true);
		mUserProfileView.setOnClickListener(this);
		addView(mUserProfileView);
		
		mNavigationMenuview = new BYNavigationMenuview(context);
		addView(mNavigationMenuview);
		
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		
		LeLog.e("BY BYNavigationView onMeasure width="+width+";height="+height);
		LeUI.measureExactly(mUserProfileView, width, height);
		LeUI.measureExactly(mNavigationMenuview, width, height - mUserProfileView.getMeasuredHeight());
		setMeasuredDimension(width, height);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {

		LeUI.layoutViewAtPos(mUserProfileView, 0, 0);
		LeUI.layoutViewAtPos(mNavigationMenuview, 0, mUserProfileView.getMeasuredHeight());
	}

	@Override
	public void onClick(View v) {
		BYNavigationManager.getInstance().onUserProfileClick();
	}
	
	
	

}
