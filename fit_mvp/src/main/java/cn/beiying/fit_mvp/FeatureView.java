package cn.beiying.fit_mvp;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.OverScroller;

import com.beiying.fitmanager.core.ui.LeUI;

/**
 * Created by beiying on 18/6/6.
 */

public class FeatureView extends FrameLayout {
    private static final float FLING_VELOCITY = 2000;
    private VelocityTracker mVelocityTracker;
    private GestureDetector mGestureDetector;
    private OverScroller mScroller;
    private int mTouchSlop;
    private int mDownX;
    private int mLastX;

    private FeatureViewContainer mContainer;
    public FeatureView(@NonNull Context context) {
        super(context);
        mVelocityTracker = VelocityTracker.obtain();
        mScroller = new OverScroller(context);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mContainer = new FeatureViewContainer(context);
        mContainer.setBackgroundColor(Color.GRAY);
        addView(mContainer);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        LeUI.measureExactly(mContainer, width, height);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        LeUI.layoutViewAtPos(mContainer, 0, 0);
    }

    @Override
    public boolean onCapturedPointerEvent(MotionEvent event) {
        return super.onCapturedPointerEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int currentX = (int) event.getRawX();
        Log.e("liuyu", "dispatchTouchEvent:" + action + ";currentX=" + currentX);
//        mVelocityTracker.addMovement(event);
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int currentX = (int) event.getRawX();
        Log.e("liuyu", "onInterceptTouchEvent:" + action + ";currentX=" + currentX);
        if (action == MotionEvent.ACTION_DOWN) {

        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int currentX = (int) event.getRawX();
        mVelocityTracker.addMovement(event);
        Log.e("liuyu", "onTouchEvent:" + action + ";currentX=" + currentX);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastX = currentX;
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = currentX - mLastX;
                Log.e("liuyu", "dx=" + dx + ";lastX=" + mLastX);
                scrollBy(-dx, 0);
                mLastX = currentX;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(1000);
                float xVelocity = mVelocityTracker.getXVelocity();
                Log.e("liuyu", "onTouchEvent:" + action + ";xVelocity=" + xVelocity);
                if (mVelocityTracker.getXVelocity() > FLING_VELOCITY) {
                    scrollForward(0, 0);
                } else {

                }
                mVelocityTracker.clear();
                break;
        }
        return true;
    }
    // 回滚
    private void scrollBack(int dx, int dy) {
        int startX = getScrollX();
        int startY = getScrollY();
        mScroller.startScroll(startX, startY, dx, dy);
    }

    // 继续前滚
    private void scrollForward(int dx, int dy) {
        int startX = getScrollX();
        int startY = getScrollY();
        mScroller.startScroll(startX, startY, dx, dy);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    public void computeScroll() {
//        if (mScroller.computeScrollOffset()) {
//            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
//        }
        super.computeScroll();
    }

    class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return super.onDown(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    class FeatureViewContainer extends FrameLayout {

        public FeatureViewContainer(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
