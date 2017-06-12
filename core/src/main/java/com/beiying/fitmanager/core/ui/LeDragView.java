package com.beiying.fitmanager.core.ui;

import android.content.Context;
import android.view.View;

public abstract class LeDragView extends LeViewGroup {

	private LeDragListView mListView;
	private View mDragItem;

	public LeDragView(Context context, LeListViewModel<?> model) {
		super(context);

		mListView = createDragListView(model);
		mListView.setDragView(this);
		addView(mListView);

		mDragItem = new LeView(context);
		mDragItem.setVisibility(View.GONE);
		addView(mDragItem);
	}
	
	public void release() {
		mListView.release();
		mListView = null;
		mDragItem = null;
	}
	
	public void setDragWidth(int dragWidth) {
		mListView.setDragWidth(dragWidth);
	}

	public void layoutDragItem(int offsetY) {
		int offsetX;
		offsetX = 0;
		mDragItem.layout(offsetX, offsetY, offsetX + mDragItem.getMeasuredWidth(),
				offsetY + mDragItem.getMeasuredHeight());
	}

	public View getDragItem() {
		return mDragItem;
	}

	public LeDragListView getDragListView() {
		return mListView;
	}

	protected abstract LeDragListView createDragListView(LeListViewModel<?> model);

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		mListView.measure(widthMeasureSpec, heightMeasureSpec);
		mDragItem.measure(widthMeasureSpec, heightMeasureSpec);

		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		mListView.layout(0, 0, mListView.getMeasuredWidth(), mListView.getMeasuredHeight());
	}

}
