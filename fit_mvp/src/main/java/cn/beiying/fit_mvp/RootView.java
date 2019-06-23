package cn.beiying.fit_mvp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowInsets;
import android.widget.FrameLayout;

import com.beiying.fitmanager.core.ui.LeUI;

/**
 * Created by beiying on 18/6/4.
 */

public class RootView extends FrameLayout implements BaseView<RootPresenter>{
    private RootPresenter mPresenter;
    private MainView mMainView;
    private FeatureView mFeatureView;
    public RootView(@NonNull Context context) {
        super(context);

        mPresenter = createPresenter();
        initViews(context);
    }

    private void initViews(Context context) {
        mMainView = new MainView(context);
        addView(mMainView);

        mFeatureView = new FeatureView(context);
        addView(mFeatureView);

        setBackgroundColor(Color.WHITE);

    }

    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        return super.onApplyWindowInsets(insets);
    }

    @Override
    protected boolean fitSystemWindows(Rect insets) {
        return super.fitSystemWindows(insets);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        for (int i = 0;i < getChildCount();i++) {
            View child = getChildAt(i);
            LeUI.measureExactly(child, width, height);
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int offsetX = 0, offsetY = 0;
        for (int i = 0;i < getChildCount();i++) {
            View child = getChildAt(i);
            LeUI.measureExactly(child, offsetX, offsetY);
        }
    }

    @Override
    public RootPresenter createPresenter() {
        return new RootPresenter(this);
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
