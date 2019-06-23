package com.beiying.fitmanager.train;

import android.content.Context;

import com.beiying.fitmanager.core.ui.LeUI;
import com.beiying.fitmanager.core.ui.LeView;

/**
 * Created by beiying on 17/6/8.
 */

public class BYTrainBriefView extends LeView {
    private static final int PADDING = 8;

    private BYTotalTrainTimeView mTotalTimeView;
    private BYTrainingCardView mTrainings;
    private BYRecommendTrainCardView mRecommendTrainView;
    private BYRecommendArticleCardView mRecommendArticleView;

    private int mPadding;

    public BYTrainBriefView(Context context) {
        super(context);

        mPadding = LeUI.getDensityDimen(context, PADDING);

        mTotalTimeView = new BYTotalTrainTimeView(context);
        addView(mTotalTimeView);

        mTrainings = new BYTrainingCardView(context);
        addView(mTrainings);

        mRecommendTrainView = new BYRecommendTrainCardView(context);
        addView(mRecommendTrainView);

        mRecommendArticleView = new BYRecommendArticleCardView(context);
        addView(mRecommendArticleView);

        setBackgroundColor(0xFFE6ECF0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        LeUI.measureExactly(mTotalTimeView, width, height);
        LeUI.measureExactly(mTrainings, width, height);
        LeUI.measureExactly(mRecommendTrainView, width, height);
        LeUI.measureExactly(mRecommendArticleView, width, height);

        height = (mTotalTimeView.getMeasuredHeight() + mTrainings.getMeasuredHeight() + mRecommendTrainView.getMeasuredHeight() + mRecommendArticleView.getMeasuredHeight() + mPadding * 4);
        setMeasuredDimension(width, height);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int offsetX = 0, offsetY = 0;
        LeUI.layoutViewAtPos(mTotalTimeView, offsetX, offsetY);

        offsetY += (mTotalTimeView.getMeasuredHeight() + mPadding);
        LeUI.layoutViewAtPos(mTrainings, offsetX, offsetY);

        offsetY += (mTrainings.getMeasuredHeight() + mPadding);
        LeUI.layoutViewAtPos(mRecommendTrainView, offsetX, offsetY);

        offsetY += (mRecommendTrainView.getMeasuredHeight() + mPadding);
        LeUI.layoutViewAtPos(mRecommendArticleView, offsetX, offsetY);
    }
}
