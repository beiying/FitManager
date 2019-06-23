package com.beiying.fitmanager.module;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.beiying.fitmanager.core.ui.LeUI;
import com.beiying.fitmanager.core.ui.LeView;

public class BYModuleRootView extends LeView {
    private FrameLayout mTopView;
    public BYModuleRootView(Context context) {
        super(context);

        mTopView = new FrameLayout(context);
        addView(mTopView);
    }

    public ViewGroup getTopView() {
        return mTopView;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        LeUI.measureExactly(mTopView, width, height / 2);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        LeUI.layoutViewAtPos(mTopView, 0,0);
    }
}
