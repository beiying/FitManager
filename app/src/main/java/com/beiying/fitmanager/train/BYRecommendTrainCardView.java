package com.beiying.fitmanager.train;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import com.beiying.fitmanager.R;
import com.beiying.fitmanager.core.ui.LeUI;
import com.beiying.fitmanager.core.ui.LeView;
import com.beiying.fitmanager.core.utils.LeTextUtil;
import com.beiying.fitmanager.framework.model.BYTrainModel;

/**
 * Created by beiying on 17/6/12.
 */

public class BYRecommendTrainCardView extends LeView {
    private static final int TITLE_PADDING_LEFT = 16;
    private static final int ITEM_MARGIN = 4;
    private TextView mTitle;
    private BYRecommendTrainItemView mItemView1;
    private BYRecommendTrainItemView mItemView2;
    private BYRecommendTrainItemView mItemView3;

    private int mTitlePaddingLeft;
    private int mItemMargin;

    public BYRecommendTrainCardView(Context context) {
        super(context);

        mTitlePaddingLeft = LeUI.getDensityDimen(context, TITLE_PADDING_LEFT);
        mItemMargin = LeUI.getDensityDimen(context, ITEM_MARGIN);

        mTitle = new TextView(context);
        mTitle.setText(R.string.train_recommend_train_title);
        mTitle.setTextColor(0xFF101010);
        mTitle.setTextSize(15);
        addView(mTitle);

        BYTrainModel model1 = new BYTrainModel();
        model1.mName = "椭圆机";
        model1.mIcon = "";
        model1.mTime = 1080;
        mItemView1 = new BYRecommendTrainItemView(context, model1);
        mItemView1.setBackgroundImage(R.drawable.train_elliptical_machine_bg);
        addView(mItemView1);

        BYTrainModel model2 = new BYTrainModel();
        model2.mName = "户外跑步";
        model2.mIcon = "";
        model2.mTime = 1500;
        mItemView2 = new BYRecommendTrainItemView(context, model2);
        mItemView2.setBackgroundResource(R.drawable.train_run_bg);
        addView(mItemView2);

        BYTrainModel model3 = new BYTrainModel();
        model3.mName = "胸肌训练器";
        model3.mIcon = "";
        model3.mTime = 1200;
        mItemView3 = new BYRecommendTrainItemView(context, model3);
        mItemView3.setBackgroundResource(R.drawable.train_pectoralis_bg);
        addView(mItemView3);

        setBackgroundColor(Color.WHITE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = 0;
        int width = MeasureSpec.getSize(widthMeasureSpec);

        LeUI.measureExactly(mTitle, LeTextUtil.getTextWidth(mTitle.getPaint(),mTitle.getText().toString()), LeTextUtil.getPaintHeight(mTitle.getPaint()));
        LeUI.measureExactly(mItemView1, width, height);
        LeUI.measureExactly(mItemView2, width, height);
        LeUI.measureExactly(mItemView3, width, height);

        height = (mTitle.getMeasuredHeight() + mTitlePaddingLeft * 2 + mItemView1.getMeasuredHeight() * 3 + mItemMargin * 3);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int offsetX = mTitlePaddingLeft;
        int offsetY = mTitlePaddingLeft;
        LeUI.layoutViewAtPos(mTitle, offsetX, offsetY);

        offsetY += (mTitle.getMeasuredHeight()+ mTitlePaddingLeft);
        LeUI.layoutViewAtPos(mItemView1, offsetX, offsetY);

        offsetY += (mItemView1.getMeasuredHeight() + mItemMargin);
        LeUI.layoutViewAtPos(mItemView2, offsetX, offsetY);

        offsetY += (mItemView2.getMeasuredHeight() + mItemMargin);
        LeUI.layoutViewAtPos(mItemView3, offsetX, offsetY);
    }
}
