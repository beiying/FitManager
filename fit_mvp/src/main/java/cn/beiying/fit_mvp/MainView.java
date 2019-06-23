package cn.beiying.fit_mvp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.beiying.fitmanager.core.ui.LeUI;

/**
 * Created by beiying on 19/3/9.
 */

public class MainView extends FrameLayout implements BaseView {
    private MainPresenter mPresenter;
    private ScrollView mScrollView;
    private LinearLayout mContentView;
    private TextView mDownFileBtn;

    public MainView(@NonNull Context context) {
        super(context);

        mPresenter = createPresenter();

        mScrollView = new ScrollView(context);
        addView(mScrollView);

        mContentView = new LinearLayout(context);
        mScrollView.addView(mContentView);

        mDownFileBtn = new TextView(context);
        mDownFileBtn.setBackgroundDrawable(getBgDrawable());
        mDownFileBtn.setText("文件下载");
        mContentView.addView(mDownFileBtn, getBtnLayoutParams());

    }

    private Drawable getBgDrawable() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(LeUI.getDensityDimen(getContext(), 8));
        drawable.setColor(Color.BLUE);
        return drawable;
    }

    private LinearLayout.LayoutParams getBtnLayoutParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        return params;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        LeUI.measureExactly(mScrollView, width, height);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        LeUI.layoutViewAtPos(mScrollView, 0,0);
    }

    @Override
    public MainPresenter createPresenter() {
        return new MainPresenter(this);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showError(String msg) {

    }

    @Override
    public void onErrorCode(BaseModel model) {

    }
}
