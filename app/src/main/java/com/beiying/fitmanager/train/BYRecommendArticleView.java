package com.beiying.fitmanager.train;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import com.beiying.fitmanager.R;
import com.beiying.fitmanager.core.ui.LeUI;
import com.beiying.fitmanager.core.ui.LeView;
import com.beiying.fitmanager.core.utils.LeTextUtil;
import com.beiying.fitmanager.framework.model.BYArticleModel;

/**
 * Created by beiying on 17/8/5.
 */

public class BYRecommendArticleView extends LeView{
    private static final int TITLE_PADDING_LEFT = 16;
    private static final int ITEM_MARGIN = 4;
    private TextView mTitle;
    private BYRecommendArticleItemView mItemView1;
    private BYRecommendArticleItemView mItemView2;
    private BYRecommendArticleItemView mItemView3;

    private int mTitlePaddingLeft;
    private int mItemMargin;

    public BYRecommendArticleView(Context context) {
        super(context);

        mTitlePaddingLeft = LeUI.getDensityDimen(context, TITLE_PADDING_LEFT);
        mItemMargin = LeUI.getDensityDimen(context, ITEM_MARGIN);

        mTitle = new TextView(context);
        mTitle.setText(R.string.train_recommend_article_title);
        mTitle.setTextColor(0xFF101010);
        mTitle.setTextSize(15);
        addView(mTitle);

        BYArticleModel model1 = new BYArticleModel();
        model1.mTitle = "下肢增强式训练:提升你的下肢爆发力";
        model1.mContent = "";
        model1.mSource = "健身吧";
        mItemView1 = new BYRecommendArticleItemView(context, model1);
        addView(mItemView1);

        BYArticleModel model2 = new BYArticleModel();
        model2.mTitle = "颈后下拉、推举是真的危险吗？";
        model2.mContent = "";
        model2.mSource = "虎扑健身";
        mItemView2 = new BYRecommendArticleItemView(context, model2);
        addView(mItemView2);

        BYArticleModel model3 = new BYArticleModel();
        model3.mTitle = "放松胸小肌：改善圆肩驼背";
        model3.mContent = "";
        model3.mSource = "全球健身指南";
        mItemView3 = new BYRecommendArticleItemView(context, model3);
        addView(mItemView3);

        setBackgroundColor(Color.WHITE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = 0;
        int width = MeasureSpec.getSize(widthMeasureSpec);

        LeUI.measureExactly(mTitle, width - mTitlePaddingLeft, LeTextUtil.getPaintHeight(mTitle.getPaint()));
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

        offsetX = 0;
        offsetY += (mTitle.getMeasuredHeight()+ mTitlePaddingLeft);
        LeUI.layoutViewAtPos(mItemView1, offsetX, offsetY);

        offsetY += (mItemView1.getMeasuredHeight() + mItemMargin);
        LeUI.layoutViewAtPos(mItemView2, offsetX, offsetY);

        offsetY += (mItemView2.getMeasuredHeight() + mItemMargin);
        LeUI.layoutViewAtPos(mItemView3, offsetX, offsetY);
    }
}
