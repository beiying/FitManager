package com.beiying.module.video;

import android.content.Context;
import android.graphics.Color;

import com.beiying.fitmanager.core.ui.LeView;

public class MVideoRootView extends LeView {
    public MVideoRootView(Context context) {
        super(context);
        setBackgroundColor(Color.RED);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
