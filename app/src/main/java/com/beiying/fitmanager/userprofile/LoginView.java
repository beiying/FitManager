package com.beiying.fitmanager.userprofile;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;

import com.beiying.fitmanager.R;
import com.beiying.fitmanager.core.LeSafeRunnable;
import com.beiying.fitmanager.core.ui.LeLinearLayout;
import com.beiying.fitmanager.core.ui.LeTitlebar;
import com.beiying.fitmanager.core.ui.LeUI;
import com.beiying.fitmanager.core.ui.LeView;
import com.beiying.fitmanager.core.utils.LeTextUtil;
import com.beiying.fitmanager.framework.ui.LeDimen;

/**
 * Created by beiying on 17/5/4.
 */

public class LoginView extends LeView {
    private static final int LOGIN_TITLE_CONTENT_GAP = 24;
    private static final int LOGIN_ITEM_HEIGHT = 54;
    private static final int LOGIN_ITEM_PADDING_LEFT = 15;
    private static final int LOGIN_ITEM_ICON_SIZE = 24;
    private static final int LOGIN_ICON_TEXT_GAP = 25;

    private LeTitlebar mTitlebar;
    private LoginContentView mContentView;
    private int mTitleContentGap;

    public LoginView(Context context) {
        super(context);

        mTitleContentGap = LeUI.getDensityDimen(context, LOGIN_TITLE_CONTENT_GAP);

        mTitlebar = new LeTitlebar(context, R.drawable.toolbar_back, "登录");
        mTitlebar.setBackAction(new LeSafeRunnable() {
            @Override
            public void runSafely() {
//                BYControlCenter.getInstance().backFullScreen();
            }
        });
        addView(mTitlebar);

        mContentView = new LoginContentView(context);
        addView(mContentView);

        setBackgroundColor(Color.parseColor("#ffededed"));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);

        LeUI.measureExactly(mTitlebar, width, 0);
        LeUI.measureExactly(mContentView, width, 0);

        setMeasuredDimension(width, height);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int offsetX = 0;
        int offsetY = 0;
        LeUI.layoutViewAtPos(mTitlebar, offsetX, offsetY);

        offsetY += (mTitlebar.getMeasuredHeight() + mTitleContentGap);
        LeUI.layoutViewAtPos(mContentView, offsetX, offsetY);
    }

    private class LoginContentView extends LeLinearLayout {
        private LoginItemView mWbLogin;
        private LoginItemView mWxLogin;
        private LoginItemView mQQLogin;

        public LoginContentView(Context context) {
            super(context);
            mWbLogin = new LoginItemView(context, getResources().getDrawable(R.drawable.share_item_sina), "新浪微博");
            mWbLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ThirdPartyManager.getInstance().loginByWB();
                }
            });
            mWxLogin = new LoginItemView(context, getResources().getDrawable(R.drawable.share_item_wechat), "微信");
            mWxLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ThirdPartyManager.getInstance().loginByWx();
                }
            });
            mQQLogin = new LoginItemView(context, getResources().getDrawable(R.drawable.share_item_mobileqq), "QQ");
            mQQLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ThirdPartyManager.getInstance().loginByQQ();
                }
            });

            addView(mWbLogin);
            addView(mWxLogin);
            addView(mQQLogin);

            setOrientation(LinearLayout.VERTICAL);
            setBackgroundColor(Color.WHITE);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);

            LeUI.measureExactly(mWbLogin, width, 0);
            LeUI.measureExactly(mWxLogin, width, 0);
            LeUI.measureExactly(mQQLogin, width, 0);
            setMeasuredDimension(width, mWxLogin.getMeasuredHeight() * 3);
        }
    }

    private class LoginItemView extends LeView {

        private Drawable mIcon;
        private Drawable mLine;
        private Drawable mBgDrawable;
        private int mItemHeight;
        private int mItemPaddingLeft;
        private int mIconSize;
        private int mIconTextGap;
        private Paint mPaint;
        private String mText;

        public  LoginItemView(Context context, int resId, int textId) {
            super(context);
            initView(context, getResources().getDrawable(resId), getResources().getString(textId));
        }

        public LoginItemView(Context context, Drawable icon, String text) {
            super(context);
            initView(context, icon, text);
        }

        private void initView(Context context, Drawable icon, String text) {
            mIcon = icon;
            mText = text;
            mItemHeight = LeUI.getDensityDimen(context, LOGIN_ITEM_HEIGHT);
            mItemPaddingLeft = LeUI.getDensityDimen(context, LOGIN_ITEM_PADDING_LEFT);
            mIconSize = LeUI.getDensityDimen(context, LOGIN_ITEM_ICON_SIZE);
            mIconTextGap = LeUI.getDensityDimen(context, LOGIN_ICON_TEXT_GAP);

            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setTextSize(LeDimen.getTextSize());

            mLine = getResources().getDrawable(R.drawable.divide_line);
            mBgDrawable = new ColorDrawable(Color.parseColor("#0d191919"));
            mBgDrawable.setCallback(this);
            setWillNotDraw(false);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = mItemHeight;
            this.setMeasuredDimension(width, height);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int offsetX = 0;
            int offsetY = 0;
            if (isPressed()) {
                mBgDrawable.setBounds(offsetX, offsetY, offsetX + getMeasuredWidth(), offsetY + getMeasuredHeight());
                mBgDrawable.draw(canvas);
            }
            if (mIcon != null) {
                offsetX = mItemPaddingLeft;
                offsetY = mItemPaddingLeft;
                mIcon.setBounds(offsetX, offsetY, offsetX + mIconSize, offsetY + mIconSize);
                mIcon.draw(canvas);
            }
            if (mPaint != null) {
                offsetX = mItemPaddingLeft + mIconSize + mIconTextGap;
                offsetY = LeTextUtil.calcYWhenAlignCenter(getMeasuredHeight(), mPaint);
                canvas.drawText(mText , offsetX, offsetY, mPaint);
            }

            if (mLine != null) {
                offsetX = 0;
                offsetY = getMeasuredHeight();
                mLine.setBounds(offsetX, offsetY - mLine.getIntrinsicHeight(), offsetX + getMeasuredWidth(), offsetY);
                mLine.draw(canvas);
            }
        }
    }
}
