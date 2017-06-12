package com.beiying.fitmanager.train;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.beiying.fitmanager.R;
import com.beiying.fitmanager.core.ui.LeUI;
import com.beiying.fitmanager.core.ui.LeView;
import com.beiying.fitmanager.core.utils.LeTextUtil;
import com.beiying.fitmanager.framework.model.BYTrainModel;

/**
 * Created by beiying on 17/6/9.
 */

public class BYTrainItemView extends LeView implements View.OnClickListener {
    private static int ITEM_HEIGHT = 90;
    private static int TEXT_MARGIN = 20;
    private static int DESCRIPTION_MARGIN_TOP = 4;

    private ImageView mDivideLine;
    private TextView mName;
    private TextView mTime;
    private TextView mDescription;

    private BYTrainModel mModel;
    private int mItemHeight;
    private int mTextMargin;
    private int mDescriptionMariginTop;
    public BYTrainItemView(Context context) {
        super(context);
        mItemHeight = LeUI.getDensityDimen(context, ITEM_HEIGHT);
        mTextMargin = LeUI.getDensityDimen(context, TEXT_MARGIN);
        mDescriptionMariginTop = LeUI.getDensityDimen(context, DESCRIPTION_MARGIN_TOP);

        mDivideLine = new ImageView(context);
        mDivideLine.setBackgroundResource(R.drawable.divide_line);
        addView(mDivideLine);

        mName = new TextView(context);
        mName.setTextSize(20);
        mName.setTextColor(0xFF404040);
        addView(mName);

        mTime = new TextView(context);
        mTime.setTextSize(15);
        mTime.setTextColor(0xFF404040);
        addView(mTime);

        mDescription = new TextView(context);
        mDescription.setTextSize(13);
        mDescription.setTextColor(0xFFA4A4A4);
        mDescription.setSingleLine();
        mDescription.setEllipsize(TextUtils.TruncateAt.END);
        addView(mDescription);


        refresh();

        setOnClickListener(this);
    }

    public void refresh() {
        if (mModel != null) {
            mName.setText(mModel.mName);
            mDescription.setText(mModel.mDescription);
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
            LeUI.measureExactly(mDivideLine, width, 2);
            LeUI.measureExactly(mName, LeTextUtil.getTextWidth(mName.getPaint(), mName.getText().toString()), LeTextUtil.getPaintHeight(mName.getPaint()));
            LeUI.measureExactly(mTime, LeTextUtil.getTextWidth(mTime.getPaint(), mTime.getText().toString()), LeTextUtil.getPaintHeight(mTime.getPaint()));
            LeUI.measureExactly(mDescription, width - mTextMargin * 2, LeTextUtil.getPaintHeight(mDescription.getPaint()));
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
        LeUI.layoutViewAtPos(mDivideLine, offsetX, offsetY);

        offsetX += mTextMargin;
        offsetY += mTextMargin;
        LeUI.layoutViewAtPos(mName, offsetX, offsetY);

        offsetX = (getMeasuredWidth() - mTime.getMeasuredWidth() - mTextMargin);
        LeUI.layoutViewAtPos(mTime, offsetX, offsetY);

        offsetX = mTextMargin;
        offsetY += (mName.getMeasuredHeight() + mDescriptionMariginTop);
        LeUI.layoutViewAtPos(mDescription, offsetX, offsetY);
    }
}
