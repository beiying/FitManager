package com.beiying.fitmanager.framework;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.v4.view.GravityCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.beiying.fitmanager.R;
import com.beiying.fitmanager.core.ui.LeUI;
import com.beiying.fitmanager.core.ui.LeView;
import com.beiying.fitmanager.framework.featureview.BYFeatureCallback;
import com.beiying.fitmanager.framework.featureview.BYFeatureView;
import com.beiying.fitmanager.navigation.BYNavigationView;
/**
 * 将页面分为部分：
 * 	1、MainView：一般用于各个模块的View
 * 	2、NavigationView：导航栏
 * 	3、FeatureView：当重新加载MainView可能耗时时，切换View时候可以保留MainView的内容，新的View放在FeatureView中显示（背景透明的对话框可以显示该页面）
 * 	4、FloatView：用于显示可以在页面可以随意拖拽或任意显示的未占满屏幕的View
 * 	5、PopView：用于显示悬浮的菜单
 * 	6、DialogLayout：用于显示对话框
 * 
 * @author yu
 *
 */
public class BYFrameworkView extends DrawerLayout {
	private BYMainView mMainContentView;
	private BYFeatureView mFeatureView;
	private BYFloatView mFloatView;
	private BYNavigationView mNavigationView;
	private LeView mDialogLayout;
	
	
	private int mNavigationWidth;
	private Context mContext;

	public BYFrameworkView(Context context) {
		super(context);
		mContext = context;
		setFitsSystemWindows(true);
		setStatusBarBackgroundColor(getResources().getColor(
				R.color.common_theme_minor_color));
		mNavigationWidth = (int) getResources().getDimension(R.dimen.navdrawer_width);
//		setupSelf();
		
		mMainContentView = new BYMainView(context);
		mMainContentView.setVisibility(View.VISIBLE);
		addView(mMainContentView);
		
		mFeatureView = new BYFeatureView(context);
		mFeatureView.setVisibility(View.GONE);
		mFeatureView.initViewSet(mMainContentView);
		addView(mFeatureView);
		
		mFloatView = new BYFloatView(context);
		addView(mFloatView);
		
		mDialogLayout = new LeView(context);
		mDialogLayout.setVisibility(View.GONE);
		addView(mDialogLayout);
		
		mNavigationView = new BYNavigationView(context);
		LayoutParams addToParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);//这里的必须使用DrawerLayout的LayoutParams
		addToParams.gravity = Gravity.START;
		mNavigationView.setLayoutParams(addToParams);
		addView(mNavigationView);
		
		BYControlCenter.getInstance().init(this);
	}


	public void setupSelf() {
		mMainContentView.getActionToolbar().setNavigationIcon(R.drawable.ic_navigation_drawer);
		
		mMainContentView.getActionToolbar().setNavigationOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openDrawer(GravityCompat.START);
				mRightDragger.smoothSlideViewTo(mNavigationView, 0, mNavigationView.getTop());
			}
		});
		
		setStatusBarBackgroundColor(getResources().getColor(
				R.color.common_theme_minor_color));
		setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		setDrawerListener(new DrawerListener() {

			@Override
			public void onDrawerStateChanged(int newState) {
				onNavDrawerStateChanged(isNavDrawerOpen(),
						newState != DrawerLayout.STATE_IDLE);
			}

			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				onNavDrawerSlide(slideOffset);
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				onNavDrawerStateChanged(true, false);
			}

			@Override
			public void onDrawerClosed(View drawerView) {
//				mNavigationView.setVisibility(View.GONE);
				onNavDrawerStateChanged(false, false);
			}
		});
	}
	
	
	protected void onNavDrawerStateChanged(boolean b, boolean c) {
		
	}


	protected void onNavDrawerSlide(float slideOffset) {
		// TODO Auto-generated method stub
		
	}
	
	protected boolean isNavDrawerOpen() {
		return isDrawerOpen(GravityCompat.START);
	}



	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		
		LeUI.measureExactly(mMainContentView, width, height);
		if (mNavigationView != null) {
			LeUI.measureExactly(mNavigationView, width - mNavigationWidth, height);
		}
		
		setMeasuredDimension(width, height);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	public BYMainView getMainContentView() {
		return mMainContentView;
	}
	
	public void showNavigationView() {
		openDrawer(GravityCompat.START);
	}
	
	public void hideNavigationView () {
		closeDrawer(GravityCompat.START);
	}

	public void showFeatureView(View view, BYFeatureCallback callback) {
		mFeatureView.showFetureView(view, callback, true, ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED, null);
	}



	public boolean exitFeatureView(boolean animate) {
		return mFeatureView.exitFeatureView(animate);
	}

	public boolean backFeatureView(boolean animate, boolean slideBack) {
		return mFeatureView.backFeatureView(animate, slideBack);
	}

	public boolean backToFirstFullScreen(int index) {
		return mFeatureView.backToFirstFullScreen(index);
	}

}
