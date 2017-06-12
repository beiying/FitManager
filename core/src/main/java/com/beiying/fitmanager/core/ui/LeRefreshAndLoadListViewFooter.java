package com.beiying.fitmanager.core.ui;

import android.content.Context;
import android.support.annotation.IntRange;
import android.widget.FrameLayout;

public abstract class LeRefreshAndLoadListViewFooter extends FrameLayout {

    public static final int STATE_NORMAL = 0;
    public static final int STATE_LOAD_FAILED = 1;
    public static final int STATE_NO_MORE_DATA = 2;

    public LeRefreshAndLoadListViewFooter(Context context) {
        super(context);
    }

    public abstract void setState(@IntRange(from=0, to=2) int state);

    public abstract void show();

    public abstract void hide();
}
