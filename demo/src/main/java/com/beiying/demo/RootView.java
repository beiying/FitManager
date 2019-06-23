package com.beiying.demo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowInsets;
import android.widget.FrameLayout;

import com.beiying.demo.view.CoordinatorLayoutView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by beiying on 18/6/4.
 */

public class RootView extends FrameLayout {
    private CoordinatorLayoutView mCoordinatorLayoutView;
    private RecyclerView mRecyclerView;
    private FeatureView mFeatureView;
    public RootView(@NonNull Context context) {
        super(context);

        initViews(context);
    }

    private void initViews(Context context) {
//        mCoordinatorLayoutView = new CoordinatorLayoutView(context);
//        addView(mCoordinatorLayoutView);

        mRecyclerView = new RecyclerView(context);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        RecyclerAdapter adapter = new RecyclerAdapter(context, getDatas());
        mRecyclerView.setAdapter(adapter);
//        mRecyclerView.setBackgroundColor(Color.BLUE);
        addView(mRecyclerView);
        setBackgroundColor(Color.WHITE);

        mFeatureView = new FeatureView(context);
//        mFeatureView.setVisibility(View.GONE);
        mFeatureView.setBackgroundColor(Color.GREEN);
        addView(mFeatureView);
    }

    private List<String> getDatas() {
        List<String> datas = new ArrayList();
        datas.add("RecylerView");
        datas.add("CoordinatorLayout");
        return datas;
    }

    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        return super.onApplyWindowInsets(insets);
    }

    @Override
    protected boolean fitSystemWindows(Rect insets) {
        return super.fitSystemWindows(insets);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        for (int i = 0;i < getChildCount();i++) {
            View child = getChildAt(i);
            UI.measureExactly(child, width, height);
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int offsetX = 0, offsetY = 0;

        UI.layoutViewAtPos(mRecyclerView, offsetX, offsetY);
        UI.layoutViewAtPos(mFeatureView, offsetX, offsetY);
    }
}
