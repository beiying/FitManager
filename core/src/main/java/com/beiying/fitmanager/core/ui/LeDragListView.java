package com.beiying.fitmanager.core.ui;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

public abstract class LeDragListView extends LeListView {
	
	public static final int UI_DRAG_WIDTH = 50;
	
	protected LeDragView mDragView;
	
	protected int mSelectIndex = -1;
	protected int mCoverIndex = -1;
	protected boolean mIsInDrag;
	protected boolean mDisallowTouchEvent = false;
	
	protected Object mSelectModel;
	
	protected int mDragY;
	protected int mDragItemOffsetY;
	
	protected int mDragWidth;

	public LeDragListView(Context context, LeListViewModel<?> model) {
		super(context, model);
		
		mDragWidth = LeUI.getDensityDimen(getContext(), UI_DRAG_WIDTH);
	}
	
	public void setDragView(LeDragView dragView) {
		mDragView = dragView;
	}
	
	public void setDragWidth(int dragWidth) {
		mDragWidth = dragWidth;
	}
	
	public Object getSelectModel() {
		return mSelectModel;
	}
	
	public int getSelectItemIndex(int x, int y) {
		if (x < getMeasuredWidth() - mDragWidth) {
			return -1;
		}
		return (y + getScrollY()) / getItemHeight();
	}
	
	public int getCoverItemIndex() {
		int index = (mDragView.getDragItem().getTop() + getScrollY()) / getItemHeight();
		int offset = (mDragView.getDragItem().getTop() + getScrollY()) % getItemHeight();
		if (offset > getItemHeight() / 2) {
			index++;
		}
		return index;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!mDisallowTouchEvent) {
			View dragItem = mDragView.getDragItem();
		
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_MOVE:
					int x = (int) event.getX();
					int y = (int) event.getY();
				
					if (y < getItemHeight()) {
						scrollBy(0, -10);
					}
				
					if (y > getMeasuredHeight() - getItemHeight()) {
						scrollBy(0, 10);
					}
				
					mDragY = y;
					if (dragItem.getVisibility() == View.GONE) {
						mDragItemOffsetY = (y + getScrollY()) % getItemHeight();
						mSelectIndex = getSelectItemIndex(x, y);
						if (mSelectIndex >= mModel.getSize()) {
							break;
						}
						mSelectModel = mModel.get(mSelectIndex);
						if (mSelectIndex >= 0) {
							mIsInDrag = true;
							dragItem.setVisibility(View.VISIBLE);
							dragItem = getListItem(mModel.indexOf(mSelectModel), dragItem);
							invalidateAll();
						}
					}
					
					mDragView.layoutDragItem(mDragY - mDragItemOffsetY);
				
					if (mSelectIndex >= 0) {
						int coverIndex = getCoverItemIndex();
						exchangeCoverItem(coverIndex);
					}
				
					if (dragItem.getVisibility() == View.VISIBLE) {
						return true;
					}
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
					onDragFinished(mSelectModel);
					mIsInDrag = false;
					dragItem.setVisibility(View.GONE);
					mSelectModel = null;
					mSelectIndex = -1;
					mCoverIndex = -1;
					invalidateAll();
					break;
				default:
					break;
			}
			invalidate();
		}
		return super.onTouchEvent(event);
	}
	
	protected void setDisallowTouchEvent(boolean disallow) {
		mDisallowTouchEvent = disallow;
	}
	
	protected void onDragFinished(Object selectModel) {
		
	}
	
	protected void exchangeCoverItem(int coverIndex) {
		Object coverObject = mModel.get(coverIndex);
		if (coverObject != null && !coverObject.equals(mSelectModel)) {
			exchangeItem(coverIndex, mSelectIndex);
			
			mCoverIndex = mSelectIndex;
			mSelectIndex = coverIndex;
		}
	}
	
	private void exchangeItem(int index1, int index2) {
		if (index1 == index2) {
			return;
		}
		Object temp = mModel.get(index1);
		mModel.update(index1, mModel.get(index2));
		mModel.update(index2, temp);
	}
}
