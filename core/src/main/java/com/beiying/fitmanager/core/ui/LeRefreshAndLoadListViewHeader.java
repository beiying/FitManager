package com.beiying.fitmanager.core.ui;

import android.content.Context;
import android.support.annotation.IntRange;
import android.widget.FrameLayout;

public abstract class LeRefreshAndLoadListViewHeader extends FrameLayout {

    public static final int STATE_NORMAL = 0;
    public static final int STATE_READY = 1;
    public static final int STATE_REFRESHING = 2;

    public LeRefreshAndLoadListViewHeader(Context context) {
        super(context);
    }

    public abstract void setState(@IntRange(from=0, to=2) int state);

    public abstract void setVisibleHeight(int height);

    public abstract int getVisibleHeight();

    public abstract int getIntrinsicHeight();
}
