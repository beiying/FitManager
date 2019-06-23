package com.beiying.fitmanager.train;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.beiying.fitmanager.BYBasicContainer;
import com.beiying.fitmanager.PermissionManager;
import com.beiying.fitmanager.core.LeLog;
import com.beiying.fitmanager.core.ui.LeUI;
import com.beiying.fitmanager.core.ui.LeView;
import com.beiying.fitmanager.core.utils.LeTextUtil;
import com.beiying.plugincore.PluginManager;
import com.beiying.plugincore.ProxyActivity;

import java.security.Permission;

/**
 * Created by beiying on 17/8/7.
 */

public class BYTotalTrainTimeView extends LeView {
    private static final int HEIGHT = 200;

    private int mHeight;

    private BYKeyValueView mTotalTimeView;
    private BYKeyValueView mWeekTimeView;
    private BYKeyValueView mRankView;
    private TextView mDayTimeView;
    public BYTotalTrainTimeView(Context context) {
        super(context);

        mHeight = LeUI.getDensityDimen(context, HEIGHT);

        initView(context);
    }

    private void initView(final Context context) {
        mTotalTimeView = new BYKeyValueView(context, "总运动(分钟)", "0");
        mTotalTimeView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionManager.getInstance().processPermission(PermissionManager.REQUEST_READ_EXTERNAL_STORAGE, new PermissionManager.LePermissionProcessor() {
                    @Override
                    public void doOnGrantedPermission() {
                        PluginManager.getInstance().loadPath(
                                Environment.getExternalStorageDirectory().getAbsolutePath() + "/demo-mock-debug.apk");
                        jumpActivity();
                    }
                });
            }
        });
        addView(mTotalTimeView);

        mWeekTimeView = new BYKeyValueView(context, "本周运动(分钟)", "0");
        addView(mWeekTimeView);

        mRankView = new BYKeyValueView(context, "本周好友运动排名", "-");
        addView(mRankView);

        mDayTimeView = new TextView(context);
        mDayTimeView.setText("共-天");
        mDayTimeView.setTextSize(14);
        mDayTimeView.setTextColor(0xFF7E7E7E);
        mDayTimeView.setGravity(Gravity.CENTER);
        addView(mDayTimeView);

        setBackgroundColor(Color.WHITE);
    }

    public void jumpActivity() {
        Intent intent = new Intent().setClass(getContext(), ProxyActivity.class);
        String apkMainActivity = PluginManager.getInstance().getPackageInfo().activities[1].name;
        LeLog.e("liuyu jump activity:" + apkMainActivity);
        intent.putExtra("className", apkMainActivity);
        getContext().startActivity(intent);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = mHeight;

        LeUI.measureExactly(mTotalTimeView, width, height);
        LeUI.measureExactly(mWeekTimeView, width / 2, height);
        LeUI.measureExactly(mRankView, width / 2, height);
        LeUI.measureExactly(mDayTimeView, width, LeTextUtil.getPaintHeight(mDayTimeView.getPaint()));

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int offsetX = 0,offsetY = 0;
        LeUI.layoutViewAtPos(mTotalTimeView, offsetX, offsetY);

        offsetY += mTotalTimeView.getMeasuredHeight();
        LeUI.layoutViewAtPos(mDayTimeView, offsetX, offsetY);

        offsetY += mDayTimeView.getMeasuredHeight();
        LeUI.layoutViewAtPos(mWeekTimeView, offsetX, offsetY);

        offsetX += mWeekTimeView.getMeasuredWidth();
        LeUI.layoutViewAtPos(mRankView, offsetX, offsetY);
    }
}
