package com.beiying.fitmanager.core.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;

import com.beiying.fitmanager.core.utils.LeUtils;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("NewApi")
public class LeGridView extends LeFrameViewGroup implements View.OnLongClickListener{
	
	public static final int COL_NUM = 3;
	
	protected static final int ANIMATION_DURATION = 150;
	
	private static final int UI_PADDING = 10;
	private static final int UI_GAP_Y = 4;
	private static final int UI_DRAG_THRESHOLD = 8;
	private static final int UI_AUTO_SCROLL_SCOPE = 10;
	private static final int UI_AUTO_SCROLL_STEP = 10;
	
	protected List<LeGridItem> mGridItems;
	
	protected int mColNum;
	
	protected int mPadding;
	protected int mItemWidth;
	protected int mItemHeight = 0;
	protected int mGapX;
	protected int mGapY;
	protected int mDragThreshold;
	protected int mAutoScrollScope;
	protected int mAutoScrollStep;
	
	protected LeGridItem mSelectItem;
	protected LeGridItem mDragItem;
	protected LeGridItem mCoverItem;

	protected boolean mIsInDrag;
	
	private float mDownX;
	private float mDownY;
	
	private float mRawDownX;
	private float mRawDownY;
	
	private float mDragSrcX;
	private float mDragSrcY;
	
	protected LeScrollView mScrollView;
	
	public LeGridView(Context context) {
		super(context);
		
		mColNum = COL_NUM;
		
		mGridItems = new ArrayList<LeGridItem>();
		
		initResources();
		
		setChildrenDrawingOrderEnabled(true);
	}
	
	private void initResources() {
		mPadding = LeUI.getDensityDimen(getContext(), UI_PADDING);
		mGapY = LeUI.getDensityDimen(getContext(), UI_GAP_Y);
		
		mDragThreshold = LeUI.getDensityDimen(getContext(), UI_DRAG_THRESHOLD);
		mAutoScrollScope = LeUI.getDensityDimen(getContext(), UI_AUTO_SCROLL_SCOPE);
		mAutoScrollStep = LeUI.getDensityDimen(getContext(), UI_AUTO_SCROLL_STEP);
	}
	
	protected void initViews() {
		organizeItems(false);
	}
	
	public boolean isInDrag() {
		return mIsInDrag;
	}
	
	public List<LeGridItem> getGridItems() {
		return mGridItems;
	}
	
	public void removeAllGridItem() {
		removeAllViews();
		mGridItems.clear();
	}
	
	public void addGridItem(LeGridItem gridItem) {
		mGridItems.add(gridItem);
		gridItem.setOnLongClickListener(this);
		addView(gridItem);
	}
	
	public void setScrollView(LeScrollView scrollView) {
		mScrollView = scrollView;
	}
	
	public float getRawDownX() {
		return mRawDownX;
	}
	
	public float getRawDownY() {
		return mRawDownY;
	}
	
	public LeGridItem getSelectItem() {
		return mSelectItem;
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (mScrollView == null) {
			return super.onInterceptTouchEvent(event);
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
//			LeLog.e("gyy:action down");
			mDownX = event.getX();
			mDownY = event.getY();
			mRawDownX = event.getRawX();
			mRawDownY = event.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
//			LeLog.e("gyy:action move");
			if (mIsInDrag) {
				float deltaX = event.getX() - mDownX;
				float deltaY = event.getY() - mDownY;
				LeUI.layoutViewAtPos(mDragItem, (int) (mDragSrcX + deltaX), (int) (mDragSrcY + deltaY));
				mCoverItem = getCoveredItem(event.getX(), event.getY());
				if (mCoverItem != null) {
					reLayoutGridItems();
				} else {
					//	TODO	需要优化 by chenchong 2015.06.30
					invalidate();
				}
				if (Math.abs(deltaX) > mDragThreshold || Math.abs(deltaY) > mDragThreshold) {
					dismissPopMenu();
				}
				autoScrollIfNecessary(event);
			}
			break;
		case MotionEvent.ACTION_UP:
//			LeLog.e("gyy: action up");
		case MotionEvent.ACTION_CANCEL:
//			LeLog.e("gyy: action up or cancel");
			onDragFinished();
			
			mIsInDrag = false;
			if (mDragItem != null) {
				mDragItem.setIsInDrag(false);
			}
			mDragItem = null;
			requestLayout();
			break;
		default:
			break;
		}
		return super.onInterceptTouchEvent(event);
	}
	
	protected void autoScrollIfNecessary(MotionEvent event){
		if (event.getY() - mScrollView.getScrollY() < mAutoScrollScope) {
			mScrollView.smoothScrollBy(0, -mAutoScrollStep);
		}
		if (event.getY() - mScrollView.getScrollY() > mScrollView.getMeasuredHeight() - mAutoScrollScope) {
			mScrollView.smoothScrollBy(0, mAutoScrollStep);
		}
	}
	
	@Override
	public boolean onLongClick(View v) {
		enterDragState(v);
		
		//取消弹出菜单	chenchong	2015.06.15
		//showPopMenu();
		
		return true;
	}
	
	protected void enterDragState(View v){
		mIsInDrag = true;
		
		mSelectItem = (LeGridItem) v;
		mDragItem = (LeGridItem) v;
		mDragItem.setIsInDrag(true);
		if (Build.VERSION.SDK_INT >= 11) {
			mDragSrcX = mDragItem.getX();
			mDragSrcY = mDragItem.getY();
		} else {
			mDragSrcX = mDragItem.getLeft();
			mDragSrcY = mDragItem.getTop();
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int width = View.MeasureSpec.getSize(widthMeasureSpec);
		mColNum = calcColNum(width);
		organizeItems(false);
		
		mItemWidth= (width - mPadding * (mColNum + 1)) / mColNum;
		if (mItemHeight == 0) {
			mItemHeight = mItemWidth;
		}
		int invisibleNum = 0;
		for (int i = 0; i < mGridItems.size(); i++) {
			if (mGridItems.get(i) != null && mGridItems.get(i).getVisibility() != VISIBLE) {
				invisibleNum ++;
			}
		}
		
		int rowNum = (mGridItems.size() - 1 - invisibleNum) / mColNum + 1;
		final int height = rowNum * (mItemHeight + mGapY) + mPadding * 2;
		
		setMeasuredDimension(width, Math.max(height, View.MeasureSpec.getSize(heightMeasureSpec)));
		
		for (LeGridItem gridItem : mGridItems) {
			LeUI.measureExactly(gridItem, mItemWidth, mItemHeight);
		}
		
		mGapX = (getMeasuredWidth() - mItemWidth * mColNum - mPadding * 2) / (mColNum - 1);
	}	

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int offsetX, offsetY;
		for (LeGridItem gridItem : mGridItems) {
			if (gridItem != mDragItem) {
				offsetX = getXByPosX(gridItem.getPosX());
				offsetY = getYByPosY(gridItem.getPosY()) + getGridOffsetY();
				LeUI.layoutViewAtPos(gridItem, offsetX, offsetY);
				
				if(gridItem.getPosX() == mColNum - 1){
					gridItem.setId(LeUtils.createId(gridItem));
					gridItem.setNextFocusRightId(LeUtils.createId(gridItem));
				} else {
					setNextFocusRightId(-1);
				}
			}
		}
	}

	@Override
	protected int getChildDrawingOrder(int childCount, int i) {
		if (mDragItem == null) {
			return i;
		}
		final int dragIndex = indexOfChild(mDragItem);
		
		if (dragIndex < 0) {
			return i;
		}
		if (i < dragIndex) {
			return i;
		} else if (i >= dragIndex && i < childCount - 1) {
			return i + 1;  
		} else {
			return dragIndex;
		}
	}
	
	protected int calcColNum(int width) {
		return COL_NUM;
	}

	protected void showPopMenu() {
		
	}
	
	protected void dismissPopMenu() {
		
	}
	
	protected void onDragFinished() {
		
	}

	private LeGridItem getCoveredItem(float posX, float posY) {
		for (LeGridItem gridItem : mGridItems) {
			if (gridItem != mDragItem) {
				if (posX > gridItem.getLeft() && posX < gridItem.getRight() && posY > gridItem.getTop() && posY < gridItem.getBottom()) {
					return gridItem;
				}
			}
		}
		return null;
	}
	
	private void reLayoutGridItems() {
		int coverIndex = mGridItems.indexOf(mCoverItem);
		mGridItems.remove(mDragItem);
		mGridItems.add(coverIndex, mDragItem);
		organizeItems(true);
		requestLayout();
	}
	
	protected void organizeItems(boolean hasAnimation) {
		int posX, posY;
		posX = 0;
		posY = 0;
		for (LeGridItem gridItem : mGridItems) {
			if (gridItem.getVisibility() != View.VISIBLE) {
				continue;
			}
			if (gridItem != mDragItem && hasAnimation) {
				int pivotY = 0;
				if (Build.VERSION.SDK_INT >= 11) {
					pivotY = (int) gridItem.getPivotY();
				}
				if (posX != gridItem.getPosX() || posY != pivotY) {
					int srcX = getXByPosX(gridItem.getPosX());
					int srcY = getYByPosY(gridItem.getPosY());
					int dstX = getXByPosX(posX);
					int dstY = getYByPosY(posY);
					TranslateAnimation animation = new TranslateAnimation(srcX - dstX, 0, srcY - dstY, 0);
					animation.setDuration(ANIMATION_DURATION);
					gridItem.startAnimation(animation);
				}
			}
			gridItem.setPosX(posX);
			gridItem.setPosY(posY);
			posX += 1;
			if (posX == mColNum) {
				posX = 0;
				posY += 1;
			}
		}
	}
	
	protected int getGridOffsetY(){
		return 0;
	}
	
	protected int getXByPosX(int posX) {
		return mPadding + (mItemWidth + mGapX) * posX;
	}
	
	protected int getYByPosY(int posY) {
		return mPadding + (mItemHeight + mGapY) * posY;
	}
}
