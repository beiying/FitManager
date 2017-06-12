package com.beiying.fitmanager.train;

import android.content.Context;

import com.beiying.fitmanager.core.ui.LeUI;
import com.beiying.fitmanager.core.ui.LeView;

/**
 * Created by beiying on 17/6/8.
 */

public class BYTrainBriefView extends LeView {
    private static final int PADDING = 8;

    private BYTrainingView mTrainings;

    private int mPadding;

    public BYTrainBriefView(Context context) {
        super(context);

        mPadding = LeUI.getDensityDimen(context, PADDING);

        mTrainings = new BYTrainingView(context);
        addView(mTrainings);

        setBackgroundColor(0xFFE6ECF0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        LeUI.measureExactly(mTrainings, width, height);
        setMeasuredDimension(width, height);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int offsetX = 0;
        int offsetY = 0;
        LeUI.layoutViewAtPos(mTrainings, offsetX, offsetY);
    }
}
