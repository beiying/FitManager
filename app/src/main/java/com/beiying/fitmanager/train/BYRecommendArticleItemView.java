package com.beiying.fitmanager.train;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.beiying.fitmanager.R;
import com.beiying.fitmanager.core.ui.LeUI;
import com.beiying.fitmanager.core.ui.LeView;
import com.beiying.fitmanager.core.utils.LeTextUtil;
import com.beiying.fitmanager.framework.model.BYArticleModel;

/**
 * Created by beiying on 17/8/5.
 */

public class BYRecommendArticleItemView extends LeView implements View.OnClickListener{
    private static int PADDING = 16;
    private static int ITEM_HEIGHT = 90;
    private static int TEXT_MARGIN = 20;

    private TextView mTitle;
    private TextView mTime;
    private TextView mSource;
    private View mDividerLine;

    private BYArticleModel mModel;
    private int mItemHeight;
    private int mTextMargin;
    private int mPadding;

    public BYRecommendArticleItemView(Context context) {
        this(context, null);
    }
    public BYRecommendArticleItemView(Context context, BYArticleModel model) {
        super(context);
        mModel = model;
        mItemHeight = LeUI.getDensityDimen(context, ITEM_HEIGHT);
        mTextMargin = LeUI.getDensityDimen(context, TEXT_MARGIN);
        mPadding = LeUI.getDensityDimen(context, PADDING);

        mTitle = new TextView(context);
        mTitle.setTextSize(18);
        mTitle.setTextColor(0xFF404040);
        mTitle.setSingleLine(true);
        addView(mTitle);

        mTime = new TextView(context);
        mTime.setTextSize(12);
        mTime.setTextColor(Color.WHITE);
        addView(mTime);

        mSource = new TextView(context);
        mSource.setTextSize(12);
        mSource.setTextColor(0xFFA4A4A4);
        addView(mSource);

        mDividerLine = new View(context);
        mDividerLine.setBackgroundResource(R.drawable.divide_line);
        addView(mDividerLine);

        refresh();

        setOnClickListener(this);
    }


    public void refresh() {
        if (mModel != null) {
            mTitle.setText(mModel.mTitle);
            mSource.setText(mModel.mSource);
        }
        invalidate();
    }

    public void setModel(BYArticleModel model) {
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
            LeUI.measureExactly(mTitle, width - mPadding * 2, LeTextUtil.getPaintHeight(mTitle.getPaint()));
            LeUI.measureExactly(mSource, LeTextUtil.getTextWidth(mSource.getPaint(), mSource.getText().toString()), LeTextUtil.getPaintHeight(mSource.getPaint()));
            LeUI.measureExactly(mDividerLine, width, 2);
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (mModel == null) {
            return;
        }
        int offsetX = 0;
        int offsetY = 0;
        LeUI.layoutViewAtPos(mDividerLine, offsetX, offsetY);

        offsetX += mPadding;
        offsetY += (getMeasuredHeight() - mTitle.getMeasuredHeight() - mTextMargin - mSource.getMeasuredHeight()) / 2;
        LeUI.layoutViewAtPos(mTitle, offsetX, offsetY);

        offsetY += (mTextMargin + mTitle.getMeasuredHeight());
        LeUI.layoutViewAtPos(mSource, offsetX, offsetY);
    }
}
