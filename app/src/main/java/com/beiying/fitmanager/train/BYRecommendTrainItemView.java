package com.beiying.fitmanager.train;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.beiying.fitmanager.core.ui.LeUI;
import com.beiying.fitmanager.core.ui.LeView;
import com.beiying.fitmanager.core.utils.LeTextUtil;
import com.beiying.fitmanager.framework.model.BYTrainModel;

/**
 * Created by beiying on 17/6/9.
 */

public class BYRecommendTrainItemView extends LeView implements View.OnClickListener {
    private static int ITEM_HEIGHT = 120;
    private static int TEXT_MARGIN = 20;

    private LeView mBackground;
    private LeView mMaskView;
    private TextView mName;
    private TextView mTime;

    private BYTrainModel mModel;
    private int mItemHeight;
    private int mTextMargin;

    public BYRecommendTrainItemView(Context context) {
        this(context, null);
    }
    public BYRecommendTrainItemView(Context context, BYTrainModel model) {
        super(context);
        mModel = model;
        mItemHeight = LeUI.getDensityDimen(context, ITEM_HEIGHT);
        mTextMargin = LeUI.getDensityDimen(context, TEXT_MARGIN);

        mBackground = new LeView(context);
//        mBackground.setBackgroundColor(Color.RED);
        addView(mBackground);

        mMaskView = new LeView(context);
        mMaskView.setBackgroundColor(0x77000000);
        addView(mMaskView);

        mName = new TextView(context);
        mName.setTextSize(20);
        mName.setTextColor(Color.WHITE);
        addView(mName);

        mTime = new TextView(context);
        mTime.setTextSize(15);
        mTime.setTextColor(Color.WHITE);
        addView(mTime);


        refresh();

        setOnClickListener(this);
    }

    public void setBackgroundImage(int res) {
        mBackground.setBackgroundResource(res);
    }

    public void refresh() {
        if (mModel != null) {
            mName.setText(mModel.mName);
            if (mModel.mTime < 60) {
                mTime.setText(mModel.mTime + "秒");
            } else if (mModel.mTime < 3600) {
                mTime.setText(mModel.mTime / 60 + "分钟");
            } else {
                mTime.setText(mModel.mTime / 3600 + "小时");
            }
        }
        invalidate();
    }

    public void setModel(BYTrainModel model) {
        mModel = model;
        refresh();
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = mItemHeight;

        if (mModel != null) {
            LeUI.measureExactly(mBackground, width - mTextMargin * 2, mItemHeight);
            LeUI.measureExactly(mMaskView, width - mTextMargin * 2, mItemHeight);
            LeUI.measureExactly(mName, LeTextUtil.getTextWidth(mName.getPaint(), mName.getText().toString()), LeTextUtil.getPaintHeight(mName.getPaint()));
            LeUI.measureExactly(mTime, LeTextUtil.getTextWidth(mTime.getPaint(), mTime.getText().toString()), LeTextUtil.getPaintHeight(mTime.getPaint()));
        }

        setMeasuredDimension(width - mTextMargin * 2, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (mModel == null) {
            return;
        }
        int offsetX = 0;
        int offsetY = 0;
        LeUI.layoutViewAtPos(mBackground, offsetX, offsetY);
        LeUI.layoutViewAtPos(mMaskView, offsetX, offsetY);

        offsetX += mTextMargin;
        offsetY += (getMeasuredHeight() - mName.getMeasuredHeight() - mTextMargin - mTime.getMeasuredHeight()) / 2;
        LeUI.layoutViewAtPos(mName, offsetX, offsetY);

        offsetY += (mTextMargin + mName.getMeasuredHeight());
        LeUI.layoutViewAtPos(mTime, offsetX, offsetY);
    }
}
