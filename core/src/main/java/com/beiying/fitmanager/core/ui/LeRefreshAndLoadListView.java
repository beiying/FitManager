/**
 *  模仿XListView（https://github.com/Maxwin-z/XListView-Android）实现
 *  支持下拉刷新，预加载，以及滑到底部加载更多
 *
 *  自定义下拉刷新效果需继承自LeRefreshAndLoadListViewHeader
 *  自定义加载更多效果需继承自LeRefreshAndLoadListViewFooter
 *  下拉刷新效果和加载更多的效果基于ListView的HeaderView和FooterView实现
 *
 *  TODO    若没添加header或footer，但开启了下拉加载或滑到底加载，可能崩溃
 */

package com.beiying.fitmanager.core.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.Scroller;


public class LeRefreshAndLoadListView extends ListView implements AbsListView.OnScrollListener{

    private final static float HEADER_OFFSET_RADIO = 1.8f;
    private final static int HEADER_SCROLL_DURATION = 400;
    private final static float DEFAULT_PRELOAD_START_RATIO = .8f;
    private static final long REFRESH_MIN_DURATION = 500L;

    private static final int AUTO_SCROLL_STATE_RESET_HEIGHT = 0;
    private static final int AUTO_SCROLL_STATE_AUTO_REFRESH = 1;

    private LeRefreshAndLoadListViewHeader mHeaderView;
    private int mHeaderViewIntrinsicHeight;

    private LeRefreshAndLoadListViewFooter mFooterView;

    private boolean mEnableRefresh = true;
    private boolean mTempDisallowRefresh;// 暂时内部禁止下拉刷新，配合后台刷新refreshInBackground()
    private boolean mIsRefreshing;
    private long mRefreshStartTime;
    private float mDownY;
    private int mActivePointerId;
    private Scroller mScroller;
    private int mAutoScrollState;

    /**
     * mEnableLoadMore由调用者决定是否开启加载更多功能，为总开关
     * mIsFooterViewVisible决定该功能本身是否可用
     *  当ListView数据不足以撑满一屏幕时，FooterView不显示，此时加载更多功能不可用，直到数据足以撑满
     *  一屏
     */
    private boolean mEnableLoadMore = true;
    private boolean mIsFooterViewVisible;
    private boolean mIsLoading;

    private boolean mEnablePreload = true;
    private float mPreloadStartRatio;

    private OnScrollListener mOnScrollListener;

    protected RefreshLoadListener mRefreshLoadListener;

    private Bitmap mLoadingBitmap;
    private int mLoadingViewOffset;

    public LeRefreshAndLoadListView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mScroller = new Scroller(context, new DecelerateInterpolator());
        super.setOnScrollListener(this);
        mPreloadStartRatio = DEFAULT_PRELOAD_START_RATIO;
    }

    public void setRefreshHeaderView(LeRefreshAndLoadListViewHeader header) {
        super.addHeaderView(header);
        mHeaderView = header;
        mHeaderViewIntrinsicHeight = mHeaderView.getIntrinsicHeight();
    }

    public void setLoadMoreFooterView(LeRefreshAndLoadListViewFooter footer) {
        super.addFooterView(footer);
        mFooterView = footer;
        mIsFooterViewVisible = false;
        mFooterView.hide();
    }

    public void stopRefresh() {
        if (mIsRefreshing) {
            if (mTempDisallowRefresh) { // 目前为后台刷新，不用更新Header状态
                mTempDisallowRefresh = false;
                mIsRefreshing = false;
                return;
            }

            long duration = System.currentTimeMillis() - mRefreshStartTime;
            if (duration >= REFRESH_MIN_DURATION) {
                mIsRefreshing = false;
                resetHeaderHeight();
            } else {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIsRefreshing = false;
                        resetHeaderHeight();
                    }
                }, REFRESH_MIN_DURATION - duration);
            }
        }
    }

    public void stopLoad(boolean success, boolean hasMoreData) {
        if (mIsLoading) {
            if (success) {
                if (hasMoreData) {
                    mFooterView.setState(LeRefreshAndLoadListViewFooter.STATE_NORMAL);
                    mIsLoading = false;
                } else {
                    mFooterView.setState(LeRefreshAndLoadListViewFooter.STATE_NO_MORE_DATA);
                    //  通过将mIsLoading保持为true，使加载更多功能失效
                }
            } else {
                mFooterView.setState(LeRefreshAndLoadListViewFooter.STATE_LOAD_FAILED);
            }
        }
    }

    public void resetLoadingState() {
        mIsLoading = false;
        mFooterView.setState(LeRefreshAndLoadListViewFooter.STATE_NORMAL);
    }

    public void setRefreshLoadListener(RefreshLoadListener l) {
        mRefreshLoadListener = l;
    }

    public void setOnScrollListener(OnScrollListener l) {
        mOnScrollListener = l;
    }

    public void enableRefresh(boolean enable) {
        mEnableRefresh = enable;
    }

    public void enableLoadMore(boolean enable) {
        if (mEnableLoadMore && !enable) {
            mIsFooterViewVisible = false;
            mFooterView.hide();
        }
        mEnableLoadMore = enable;
    }

    public void setPreloadStartRatio(float ratio) {
        mPreloadStartRatio = ratio;
    }

    public void enablePreLoad(boolean enable) {
        mEnablePreload = enable;
    }

    public void autoRefresh() {
        if (!mIsRefreshing) {
            mIsRefreshing = true;
            mAutoScrollState = AUTO_SCROLL_STATE_AUTO_REFRESH;
            mHeaderView.setState(LeRefreshAndLoadListViewHeader.STATE_REFRESHING);
            mScroller.startScroll(0, 0, 0, mHeaderViewIntrinsicHeight, HEADER_SCROLL_DURATION);
            invalidate();
        }
    }

    public void refreshInBackground() {
        if (!mIsRefreshing && mRefreshLoadListener != null) {
            mIsRefreshing = true;
            mTempDisallowRefresh = true;
            mRefreshLoadListener.onRefresh();
        }
    }

    public void setLoadingBitmap(Bitmap bitmap) {
        mLoadingBitmap = bitmap;
        invalidate();
    }

    /***********************************************************************************
     * 处理下拉刷新的逻辑
     **********************************************************************************/

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!mTempDisallowRefresh) { // 后台刷新，不处理用户手势
            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_MOVE:
                    int index = ev.findPointerIndex(mActivePointerId);
                    float y = ev.getY(index);
                    float deltaY = y - mDownY;
                    mDownY = y;
                    if (mEnableRefresh && getFirstVisiblePosition() == 0
                            && (mHeaderView.getVisibleHeight() > 0 || deltaY > 0)) {
                        updateHeaderHeight(deltaY / HEADER_OFFSET_RADIO);
                    }
                    break;
                case MotionEvent.ACTION_DOWN:
                    mActivePointerId = ev.getPointerId(0);
                    mDownY = ev.getY();
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    index = ev.getActionIndex();
                    mActivePointerId = ev.getPointerId(index);
                    mDownY = ev.getY(index);
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    index = ev.getActionIndex();
                    if (ev.getPointerId(index) == mActivePointerId) {
                        int newActivePointerIndex = index == 0 ? 1 : 0;
                        mActivePointerId = ev.getPointerId(newActivePointerIndex);
                        mDownY = ev.getY(newActivePointerIndex);
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    if (getFirstVisiblePosition() == 0) {
                        // 刷新过程中不调用resetHeaderHeight
                        // 避免autoRefresh过程中点击事件改变mAutoScrollState造成的无法停止的刷新状态
                        if (!mIsRefreshing) {
                            // 手动下拉触发刷新
                            if (mEnableRefresh && mHeaderView.getVisibleHeight() > mHeaderViewIntrinsicHeight) {
                                mIsRefreshing = true;
                                mRefreshStartTime = System.currentTimeMillis();
                                mHeaderView.setState(LeRefreshAndLoadListViewHeader.STATE_REFRESHING);
                                if (mRefreshLoadListener != null) {
                                    mRefreshLoadListener.onRefresh();
                                }
                            }
                            resetHeaderHeight();
                        }
                    }
                    break;
            }
        }
        return super.onTouchEvent(ev);
    }

    private void updateHeaderHeight(float delta) {
        int newHeight = Math.max(mHeaderView.getVisibleHeight() + (int)delta, 0);
        mHeaderView.setVisibleHeight(newHeight);
        if (showLoadingView()) {
            mLoadingViewOffset += (int)delta;
            invalidate();
        }
        if (mEnableRefresh && !mIsRefreshing) {
            if (mHeaderView.getVisibleHeight() < mHeaderViewIntrinsicHeight) {
                mHeaderView.setState(LeRefreshAndLoadListViewHeader.STATE_NORMAL);
            } else {
                mHeaderView.setState(LeRefreshAndLoadListViewHeader.STATE_READY);
            }
        }
        setSelection(0);
    }

    private void resetHeaderHeight() {
        int height = mHeaderView.getVisibleHeight();
        if (height == 0) {
            return;
        }
        int targetHeight = 0;
        if (mIsRefreshing) {
            targetHeight = mHeaderViewIntrinsicHeight;
        }
        mAutoScrollState = AUTO_SCROLL_STATE_RESET_HEIGHT;
        mScroller.startScroll(0, height, 0, targetHeight - height, HEADER_SCROLL_DURATION);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            switch (mAutoScrollState) {
                case AUTO_SCROLL_STATE_RESET_HEIGHT:
                    int y = mScroller.getCurrY();
                    int deltaY = y - mHeaderView.getVisibleHeight();
                    mHeaderView.setVisibleHeight(y);
                    if (showLoadingView()) {
                        mLoadingViewOffset += deltaY;
                    }
                    if (y == 0) {
                        mHeaderView.setState(LeRefreshAndLoadListViewHeader.STATE_NORMAL);
                        mScroller.forceFinished(true);
                        break;
                    }
                    invalidate();
                    break;
                case AUTO_SCROLL_STATE_AUTO_REFRESH:
                    int currY = mScroller.getCurrY();
                    deltaY = currY - mHeaderView.getVisibleHeight();
                    mHeaderView.setVisibleHeight(currY);
                    if (showLoadingView()) {
                        mLoadingViewOffset += deltaY;
                    }
                    if (currY == mHeaderViewIntrinsicHeight && mRefreshLoadListener != null) {
                        mScroller.forceFinished(true);
                        mRefreshStartTime = System.currentTimeMillis();
                        mRefreshLoadListener.onRefresh();
                        break;
                    }
                    invalidate();
                    break;
            }
        }
    }

    /***********************************************************************************
     * 处理加载更多的逻辑
     **********************************************************************************/

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        /**
         *  FooterView出现需满足以下一条：
         *      1. firstVisibleItem.getTop() < getTop()  ：第一条已滑出，说明内容已足够撑满屏幕
         *      2. firstVisibleItem+visibleItemCount < totalItemCount  : 所有元素一屏装不下
         *      3. firstVisibleItem+visibleItemCount == totalItemCount && totalItem > header和footer总数
         *		    &&  最后一个元素的bottom在listview的bottom之下
         */
        if (mEnableLoadMore) {
            if (firstVisibleItem >= 1 ||
                    firstVisibleItem + visibleItemCount < totalItemCount ||
                    (firstVisibleItem + visibleItemCount == totalItemCount &&
                            totalItemCount > 2 && getChildAt(getChildCount()-1).getBottom() >= getBottom())) {
                if (mFooterView != null && !mIsFooterViewVisible) {
                    mFooterView.show();
                    mIsFooterViewVisible = true;
                }
            } else {
                if (mFooterView != null && mIsFooterViewVisible) {
                    mFooterView.hide();
                    mIsFooterViewVisible = false;
                }
            }

            //  预加载
            if (mEnablePreload && !mIsLoading && mIsFooterViewVisible && mRefreshLoadListener != null
                    && (firstVisibleItem)/(float)totalItemCount >= mPreloadStartRatio) {
                mIsLoading = true;
                if (mFooterView != null) {
                    mFooterView.setState(LeRefreshAndLoadListViewFooter.STATE_NORMAL);
                }
                mRefreshLoadListener.onLoadMore();
            }

            //  滑到底部加载
            if (!mIsLoading && mIsFooterViewVisible && mRefreshLoadListener != null
                    && firstVisibleItem + visibleItemCount >= totalItemCount) {
                mIsLoading = true;
                if (mFooterView != null) {
                    mFooterView.setState(LeRefreshAndLoadListViewFooter.STATE_NORMAL);
                }
                mRefreshLoadListener.onLoadMore();
            }
        }

        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    private boolean showLoadingView() {
        Adapter adapter = getAdapter();
        return (adapter == null || adapter.isEmpty()) && (mLoadingBitmap != null && !mLoadingBitmap.isRecycled()) ;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (showLoadingView()) {
            canvas.save();
            canvas.translate(0, mLoadingViewOffset);
//            Rect srcRect = new Rect(0, 0, getWidth(), getHeight() - mLoadingViewOffset);
            canvas.drawBitmap(mLoadingBitmap, 0, 0, null);
            canvas.restore();
        }
    }

    public interface RefreshLoadListener {
        void onRefresh();

        void onLoadMore();
    }
}
