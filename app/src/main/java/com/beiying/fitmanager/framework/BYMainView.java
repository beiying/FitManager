package com.beiying.fitmanager.framework;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.beiying.fitmanager.R;
import com.beiying.fitmanager.core.LeLog;
import com.beiying.fitmanager.core.ui.LeUI;
import com.beiying.fitmanager.core.ui.LeViewGroup;
/**
 * 用于显示与导航栏菜单对应的主界面内容
 * @author yu
 *
 */
public class BYMainView extends LeViewGroup{

	private static final int UI_TOOL_BAR_HEIGHT = 52;
	private Toolbar mActionToolbar;
	private BYContentView mContentView;
	
	private int mToolbarHeight;
	public BYMainView(Context context) {
		super(context);
		
		mToolbarHeight = LeUI.getDensityDimen(context, UI_TOOL_BAR_HEIGHT);
		
		mActionToolbar = new Toolbar(context);
		mActionToolbar.setBackgroundColor(getResources().getColor(R.color.common_theme_color));
		addView(mActionToolbar);
		
		mContentView = new BYContentView(context);
		addView(mContentView);
		
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		
		if (mActionToolbar != null) {
			LeUI.measureExactly(mActionToolbar, width, mToolbarHeight);
		}
		LeLog.e("LY BYMainView width="+width+";screen width="+getResources().getDisplayMetrics().widthPixels);
		LeUI.measureExactly(mContentView, width, height - mToolbarHeight);
		setMeasuredDimension(width, height);
	}
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int offsetX, offsetY;
		offsetX = 0;
		offsetY = 0;
		for (int i = 0; i < getChildCount(); i++) {
			LeUI.layoutViewAtPos(getChildAt(i), offsetX, offsetY);
			offsetY += getChildAt(i).getMeasuredHeight();
		}
	}
	
	public Toolbar getActionToolbar() {
		return mActionToolbar;
	}

	
	public void showContentView(View view) {
		if (view != null) {
			mContentView.showContentView(view);
		}
	}
}
