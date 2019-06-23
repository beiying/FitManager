package com.beiying.fitmanager.train;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import com.beiying.fitmanager.R;
import com.beiying.fitmanager.core.ui.LeUI;
import com.beiying.fitmanager.core.ui.LeView;

import java.util.ArrayList;

/**
 * Created by beiying on 17/9/7.
 */

public class BYAllTrainView extends LeView {
    public static final String ALL_TRAIN = "全部训练";
    public static final String REDUCE_FAT_TRAIN = "减脂";
    public static final String SHAPE_TRAIN = "塑形";
    public static final String MUSCLE_BUILD_TRAIN = "增肌";
    public static final String PHYSICAL_TRAIN = "体能";
    public static final String STRENGTH_TRAIN = "力量";
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolBar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private ArrayList<ViewPagerModel> mViewModels;

    public BYAllTrainView(Context context) {
        super(context);

        initView(context);
    }

    private void initView(Context context) {
        mAppBarLayout = new AppBarLayout(context);
        addView(mAppBarLayout);

        mToolBar = new Toolbar(context);
        mToolBar.setBackgroundColor(getResources().getColor(R.color.common_theme_color));
        mToolBar.setTitle(ALL_TRAIN);
        mAppBarLayout.addView(mToolBar);

        mTabLayout = new TabLayout(context);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mTabLayout.setBackgroundColor(getResources().getColor(R.color.common_theme_color));
        mTabLayout.setTabTextColors(0x7f000000, Color.WHITE);

        mAppBarLayout.addView(mTabLayout);

        mViewPager = new ViewPager(context);
        mViewModels = new ArrayList<>();
        ViewPagerModel viewModel1 = new ViewPagerModel();
        viewModel1.mTitle = ALL_TRAIN;
        viewModel1.mView = new View(context);
        viewModel1.mView.setBackgroundColor(Color.GRAY);
        mViewModels.add(viewModel1);

        ViewPagerModel viewModel2 = new ViewPagerModel();
        viewModel2.mTitle = REDUCE_FAT_TRAIN;
        viewModel2.mView = new View(context);
        viewModel2.mView.setBackgroundColor(Color.GREEN);
        mViewModels.add(viewModel2);

        ViewPagerModel viewModel3 = new ViewPagerModel();
        viewModel3.mTitle = SHAPE_TRAIN;
        viewModel3.mView = new View(context);
        viewModel3.mView.setBackgroundColor(Color.BLACK);
        mViewModels.add(viewModel3);

        ViewPagerModel viewModel4 = new ViewPagerModel();
        viewModel4.mTitle = MUSCLE_BUILD_TRAIN;
        viewModel4.mView = new View(context);
        viewModel4.mView.setBackgroundColor(Color.GRAY);
        mViewModels.add(viewModel4);

        ViewPagerModel viewModel5 = new ViewPagerModel();
        viewModel5.mTitle = PHYSICAL_TRAIN;
        viewModel5.mView = new View(context);
        viewModel5.mView.setBackgroundColor(Color.GRAY);
        mViewModels.add(viewModel5);

        ViewPagerModel viewModel6 = new ViewPagerModel();
        viewModel6.mTitle = STRENGTH_TRAIN;
        viewModel6.mView = new View(context);
        viewModel6.mView.setBackgroundColor(Color.YELLOW);
        mViewModels.add(viewModel6);

        mViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return mViewModels.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mViewModels.get(position).mTitle;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = mViewModels.get(position).mView;
                container.addView(view);
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(mViewModels.get(position).mView);
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });
        mTabLayout.setupWithViewPager(mViewPager);
        mAppBarLayout.addView(mViewPager);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        LeUI.measureExactly(mAppBarLayout, width, height);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int offsetX = 0, offsetY = 0;
        LeUI.layoutViewAtPos(mAppBarLayout, offsetX, offsetY);
    }

    class BYAppBarLayout extends AppBarLayout {

        public BYAppBarLayout(Context context) {
            super(context);
        }
    }

    class ViewPagerModel {
        public String mTitle;
        public View mView;
    }
}
