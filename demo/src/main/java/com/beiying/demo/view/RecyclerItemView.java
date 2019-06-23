package com.beiying.demo.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.widget.Button;
import android.widget.FrameLayout;

import com.beiying.demo.UI;

/**
 * Created by beiying on 18/6/5.
 */

public class RecyclerItemView extends FrameLayout {
    private static final int ITEM_HEIGHT = 50;
    private Button mTextButton;
    private int mItemHeight;

    public RecyclerItemView(@NonNull Context context) {
        super(context);

        mItemHeight = UI.getDensityDimen(context, ITEM_HEIGHT);

        mTextButton = new Button(context);
        mTextButton.setTextSize(18);
        mTextButton.setAllCaps(false);
        mTextButton.setGravity(Gravity.CENTER);
        addView(mTextButton);

    }

    public void setText(String text) {
        mTextButton.setText(text);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        UI.measureExactly(mTextButton, width, mItemHeight);
        setMeasuredDimension(width, mItemHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int offsetX = 0, offsetY = 0;

        UI.layoutViewAtPos(mTextButton, offsetX, offsetY);
    }
}
