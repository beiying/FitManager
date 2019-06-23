package com.beiying.demo.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;

/**
 * Created by beiying on 18/6/4.
 */

public class CoordinatorLayoutView extends CoordinatorLayout {
//    private CoordinatorLayout mLayout;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private RecyclerView mListView;

    public CoordinatorLayoutView(@NonNull Context context) {
        super(context);

        initView(context);
    }

    private void initView(Context context) {
//        mLayout = new CoordinatorLayout(context);
//        mLayout.setBackgroundColor(Color.RED);
//        addView(mLayout);

        mAppBarLayout = new AppBarLayout(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(mAppBarLayout, params);
        mAppBarLayout.setBackgroundColor(Color.BLUE);
        mToolbar = new Toolbar(context);
        AppBarLayout.LayoutParams toolbar_params = new AppBarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        toolbar_params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL);
        mAppBarLayout.addView(mToolbar, params);
        mToolbar.setBackgroundColor(Color.BLACK);

        mListView = new RecyclerView(context);
        ViewGroup.LayoutParams recyclerview_params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mListView.setBackgroundColor(Color.RED);
        addView(mListView, recyclerview_params);
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int width = MeasureSpec.getSize(widthMeasureSpec);
//        int height = MeasureSpec.getSize(heightMeasureSpec);
//
//        UI.measureExactly(mAppBarLayout, width, );
//
//        setMeasuredDimension(width, height);
//    }
//
//    @Override
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        int offsetX = 0, offsetY = 0;
//        UI.layoutViewAtPos(mAppBarLayout, offsetX, offsetY);
//    }
}
