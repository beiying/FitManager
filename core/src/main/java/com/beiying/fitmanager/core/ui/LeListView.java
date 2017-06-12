package com.beiying.fitmanager.core.ui;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.beiying.fitmanager.core.LeLog;

import java.util.ArrayList;

public abstract class LeListView extends LeScrollView implements LeListViewModel.LeListViewModelListener {

	public static final int DEFAULT_ITEM_HEIGHT = 100;

	protected LeListViewModel<?> mModel;
	
	protected float mRawDownX;
	protected float mRawDownY;
	protected float mDownX;
	protected float mDownY;

	protected LeListViewContent mContent;
	
	protected LinearLayout mHeadFootContainer;

	protected int mItemHeight;

	protected int mTotalItemNum;

	private int mScrollTopOffset;

	private boolean mHasPrepared = false;

	private View mHeadView;
	private View mFootView;
	
	private int mLastTopItemCount;
	
	private int mMinimumContentHeight = 0;

	public LeListView(Context context, LeListViewModel<?> model) {
		super(context);

		if (model == null) {
			return;
		}
		mModel = model;
		mModel.setModelListener(this);

		mHeadFootContainer = new LinearLayout(context);
		mHeadFootContainer.setOrientation(LinearLayout.VERTICAL);
		addView(mHeadFootContainer);
		
		mContent = new LeListViewContent(context);
		mHeadFootContainer.addView(mContent);

		mItemHeight = DEFAULT_ITEM_HEIGHT;
		mTotalItemNum = 5;

		addFirstItem();
	}
	
	public void release() {
		if (mModel != null) {
			mModel.setModelListener(null);
			mModel = null;
		}
	}
	
	public LeListViewModel<?> getModel() {
		return mModel;
	}
	
	public void setModel(LeListViewModel<?> model) {
		mHasPrepared = false;
		
		mModel = model;
		mModel.setModelListener(this);
		mModel.refresh();

		mContent.removeAllViews();
		addFirstItem();
		
		mHeadFootContainer.removeAllViews();
		if (mHeadView != null) {
			mHeadFootContainer.addView(mHeadView);
		}
		mHeadFootContainer.addView(mContent);
		if (mFootView != null) {
			mHeadFootContainer.addView(mFootView);
		}
	}
	
	public void reloadModel() {
		setModel(mModel);
	}

	private void addFirstItem() {
		if (mModel.getSize() > 0) {
			mContent.addView(getListItem(0, null), 0);
			mContent.getIndexList().add(0);
		}
	}
	
	public int getItemHeight() {
		return mItemHeight;
	}
	
	public View getHeader() {
		return mHeadView;
	}
	
	public View getFooter() {
		return mFootView;
	}

	public void addHeader(View headView) {
		if (mHeadView != null) {
			removeHeader();
		}
		mHeadView = headView;
		mHeadFootContainer.addView(mHeadView, 0);
	}
	
	public void addFooter(View footView) {
		if (mFootView != null) {
			removeFooter();
		}
		mFootView = footView;
		mHeadFootContainer.addView(mFootView);
	}
	
	public void removeHeader() {
		if (mHeadView != null) {
			mHeadFootContainer.removeView(mHeadView);
			mHeadView = null;
		}
	}
	
	public void removeFooter() {
		if (mFootView != null) {
			mHeadFootContainer.removeView(mFootView);
			mFootView = null;
		}
	}
	
	public void setMinimumContentHeight(int minimumHeight) {
		mMinimumContentHeight = minimumHeight;
	}
	
	public void invalidateAll() {
		mContent.requestLayout();
		mContent.invalidate();
		for (int i = 0; i < mContent.getChildCount(); i++) {
			mContent.getChildAt(i).invalidate();
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mRawDownX = ev.getRawX();
			mRawDownY = ev.getRawY();
			mDownX = ev.getX();
			mDownY = ev.getY();
			break;
		default:
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		LeLog.e("liuyu", "listview measure count =" + mContent.getChildCount());
		prepareListContentView();
		mContent.measure(widthMeasureSpec, heightMeasureSpec);
	}

	private void prepareListContentView() {
		if (mModel.getSize() == 0) {
			return;
		}

		if (mHasPrepared) {
			return;
		}

		mHasPrepared = true;

		for (int i = 0; i < mContent.getChildCount(); i++) {
			final View childView = mContent.getChildAt(i);
			mItemHeight = childView.getMeasuredHeight();
			break;
		}


		calcTotalItemNum();
		for (int i = 0; i < Math.min(mTotalItemNum, mModel.getSize()); i++) {
			mContent.addView(getListItem(i, null));
			mContent.getIndexList().add(i);
		}
	}

	protected void calcTotalItemNum() {
		final DisplayMetrics dm = getResources().getDisplayMetrics();
		mTotalItemNum = Math.max(dm.widthPixels, dm.heightPixels) / mItemHeight + 3;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);

		mScrollTopOffset = t;
		int headHeight = 0;
		if (mHeadView != null) {
			headHeight = mHeadView.getMeasuredHeight();
		}
		mScrollTopOffset -= headHeight;
		if (mScrollTopOffset > mContent.getMeasuredHeight() - getMeasuredHeight()) {
			mScrollTopOffset = mContent.getMeasuredHeight() - getMeasuredHeight();
		}

		if (mScrollTopOffset < 0) {
			mScrollTopOffset = 0;
		}
		
		int count = ((mScrollTopOffset - headHeight) / mItemHeight);
		if (mLastTopItemCount != count) {
			mLastTopItemCount = count;
			mContent.layoutChilds(mContent.getLeft(), mContent.getTop(), mContent.getRight(), mContent.getBottom());
		}
	}

	public void onAdd(int index) {
		if (mModel.getSize() < mTotalItemNum) {
			if (mContent.getIndexList().size() == 0) {
				addFirstItem();
			}
			int indexTemp = index;
			indexTemp++;
			if (mHasPrepared) {
				mContent.addView(getListItem(index, null), indexTemp);
				mContent.getIndexList().add(index + 1, index);
			}
		}
		mContent.requestLayout();
		mContent.invalidate();
	}

	public void onRemove(int index) {
		if (mModel.getSize() == 0) {
			onClear();
			return;
		}
		if (mModel.getSize() + 1 < mTotalItemNum) {
			int indexTemp = index + 1;
			if (mHasPrepared) {
				mContent.removeViewAt(indexTemp);
				mContent.getIndexList().remove(index + 1);
			}
		}
		mContent.requestLayout();
	}
	@Override
	public void onClear() {
		if (mHasPrepared) {
			mContent.removeAllViews();
			mContent.getIndexList().clear();
		}
	}

	public void onUpdate(int index) {
		mContent.requestLayout();
	}

	public void onRefresh() {
		mContent.requestLayout();
	}
	
	public View getListItemByIndex(int index) {
		for (Integer integer : mContent.getIndexList()) {
			if (integer == index) {
				return mContent.getChildAt(integer);
			}
		}
		return null;
	}
	
	protected abstract View getListItem(int index, View convertView);

	public class LeListViewContent extends ViewGroup {

		private int mTopItemIndex;

		private ArrayList<Integer> mIndexList; // 记录每个childview的所对应的index

		public LeListViewContent(Context context) {
			super(context);

			mIndexList = new ArrayList<Integer>();
		}

		public ArrayList<Integer> getIndexList() {
			return mIndexList;
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			final int width = MeasureSpec.getSize(widthMeasureSpec);
			int height = 0;

			for (int i = 0; i < getChildCount(); i++) {
				final View childView = getChildAt(i);
				childView.measure(widthMeasureSpec,
						MeasureSpec.makeMeasureSpec(DEFAULT_ITEM_HEIGHT, MeasureSpec.EXACTLY));
			}
			if (getChildCount() > 0) {
				final View childView = getChildAt(0);
				mItemHeight = childView.getMeasuredHeight();
				LeLog.e("liuyu", "listviewcontent measure mItemHeight =" + mItemHeight);
				height = mItemHeight * mModel.getSize();
			}

			if (mModel.getSize() == 0) {
				height = Math.max(height, mMinimumContentHeight);
			}
			LeLog.e("liuyu", "listviewcontent measure height =" + height);
			setMeasuredDimension(width, height);
		}

		@Override
		protected void onLayout(boolean changed, int l, int t, int r, int b) {
			layoutChilds(l, t, r, b);
		}
		
		public void layoutChilds(int l, int t, int r, int b) {
			if (getChildCount() == 0) {
				return;
			}
			
			final DisplayMetrics dm = getResources().getDisplayMetrics();
			int topPassCount;
			int scrollTopOffset = Math.min(mScrollTopOffset, getMeasuredHeight() - dm.heightPixels);
			scrollTopOffset = Math.max(scrollTopOffset, 0);
			topPassCount = scrollTopOffset / mItemHeight;
			int offsetY = (topPassCount - 1) * mItemHeight; 

			mTopItemIndex = topPassCount % getChildCount();

			for (int i = mTopItemIndex; i < getChildCount(); i++) {
				final View childView = getChildAt(i);
				final int top = offsetY;
				final int bottom = top + childView.getMeasuredHeight();
				if (bottom <= getMeasuredHeight()) {
					childView.layout(0, top, childView.getMeasuredWidth(), bottom);
					offsetY += childView.getMeasuredHeight();
				} else {
					childView.layout(0, 0, 0, 0);
				}

			}

			for (int i = 0; i < mTopItemIndex; i++) {
				final View childView = getChildAt(i);
				final int top = offsetY;
				final int bottom = top + childView.getMeasuredHeight();
				if (bottom <= getMeasuredHeight()) {
					childView.layout(0, top, childView.getMeasuredWidth(), bottom);
					offsetY += childView.getMeasuredHeight();
				} else {
					childView.layout(0, 0, 0, 0);
				}
			}

			adjustChild();
		}

		private void adjustChild() {
			int childViewIndex = 0;
			for (int i = 0; i < getChildCount(); i++) {
				int top = getChildAt(i).getTop();
				int index = top / mItemHeight;
				if (index >= 0 && index < mModel.getSize()) {
					getListItem(index, getChildAt(i));
					mIndexList.set(childViewIndex, index);
					childViewIndex++;
				}
			}
		}

	}

}