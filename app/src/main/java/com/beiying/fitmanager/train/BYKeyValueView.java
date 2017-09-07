package com.beiying.fitmanager.train;

import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;

import com.beiying.fitmanager.core.ui.LeUI;
import com.beiying.fitmanager.core.ui.LeView;
import com.beiying.fitmanager.core.utils.LeTextUtil;

/**
 * Created by beiying on 17/8/7.
 */

public class BYKeyValueView extends LeView {
    private static final int HEIGHT = 80;
    private static final int TEXT_PADDING = 8;

    private int mHeight;
    private int mTextPadding;

    private TextView mKeyView;
    private TextView mValueView;

    public BYKeyValueView(Context context) {
        this(context, "", "");
    }

    public BYKeyValueView(Context context, String key, String value) {
        super(context);

        mHeight = LeUI.getDensityDimen(context, HEIGHT);
        mTextPadding = LeUI.getDensityDimen(context, TEXT_PADDING);

        mKeyView = new TextView(context);
        mKeyView.setTextSize(14);
        mKeyView.setTextColor(0xFF565656);
        mKeyView.setGravity(Gravity.CENTER);
        mKeyView.setText(key);
        addView(mKeyView);

        mValueView = new TextView(context);
        mValueView.setTextSize(30);
        mValueView.setTextColor(0xFF453C4C);
        mValueView.setGravity(Gravity.CENTER);
        mValueView.setText(value);
        addView(mValueView);
    }



    public void setKeyText(String key) {
        mKeyView.setText(key);
    }

    public void setValueText(String value) {
        mValueView.setText(value);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = mHeight;

        LeUI.measureExactly(mKeyView, width, LeTextUtil.getPaintHeight(mKeyView.getPaint()));
        LeUI.measureExactly(mValueView, width, LeTextUtil.getPaintHeight(mValueView.getPaint()));
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int offsetX = 0, offsetY = (mHeight - mKeyView.getMeasuredHeight() - mValueView.getMeasuredHeight() - mTextPadding) / 2;
        LeUI.layoutViewAtPos(mKeyView, offsetX, offsetY);

        offsetY += (mKeyView.getMeasuredHeight() + mTextPadding);
        LeUI.layoutViewAtPos(mValueView, offsetX, offsetY);

    }
}
