package com.beiying.fitmanager.core.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.View;

import java.util.ArrayList;

public class LePopMenu extends LePopContent implements View.OnClickListener {
	private static final int UI_DEFAULT_BACKGROUND = 0xffffffff;
	private static final int UI_DEFAULT_SHADOW = 0x33000000;
	private static final int DEFAULT_MAX_COL = 1;
	
	protected boolean mIsMarginCollapsing = true;

	protected int mColNum;
	protected int mRowNum;

	private int mMaxCol;
	
	private Paint mPaint;
	private Rect mRect;
	
	protected int mWidth;
	protected int mHeight;
	
	private LePopMenuClickListener mListener;
	
	public LePopMenu(Context context) {
		super(context);

		mMaxCol = DEFAULT_MAX_COL;
		mColNum = DEFAULT_MAX_COL;

		setClickable(true);
		setWillNotDraw(false);
		
		mPaint = new Paint();
		mRect = new Rect();
	}
	
	public int getContentWidth() {
		if (mWidth == 0) {
			measureInner();
		}
		return mWidth;
	}
	
	public int getContentHeight() {
		if (mHeight == 0) {
			measureInner();
		}
		return mHeight;
	}
	
	public void setIsMarginCollapsing(boolean isMarginCollapsing) {
		mIsMarginCollapsing = isMarginCollapsing;
	}

	public void setMaxColumn(int maxCol) {
		mMaxCol = maxCol;
	}
	
	public void setColNum(int colNum) {
		mColNum = colNum;
	}
	
	public LePopMenuItem getPopMenuItemById(int id) {
		for (int i = 0; i < getChildCount(); i++) {
			LePopMenuItem menuItem = (LePopMenuItem) getChildAt(i);
			if (menuItem.getId() == id) {
				return menuItem;
			}
		}
		return null;
	}

	public void addPopMenuItem(LePopMenuItem menuItem) {
		addView(menuItem);
		menuItem.setOnClickListener(this);
		calculateRowAndCol();
		requestLayout();
	}

	public void setPopMenuClickListener(LePopMenuClickListener listener) {
		mListener = listener;
	}

	public ArrayList<LePopMenuItem> getPopMenuItems() {
		ArrayList<LePopMenuItem> list = new ArrayList<LePopMenuItem>();
		for (int i = 0; i < getChildCount(); i++) {
			list.add((LePopMenuItem) getChildAt(i));
		}
		return list;
	}

	protected void calculateRowAndCol() {
		mRowNum = (getChildCount() - 1) / mMaxCol + 1;
		mColNum = Math.min(mMaxCol, getChildCount());
	}

	public void recyclePopMenu() {
		for (int i = 0; i < getChildCount(); i++) {
			((LePopMenuItem) getChildAt(i)).recyclePopMenuItem();
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		measureInner();
	}
	
	protected void measureInner() {
		if (getChildCount() == 0) {
			setMeasuredDimension(getPaddingLeft() + getPaddingRight(), getPaddingTop() + getPaddingBottom());
			return;
		}

		int maxWidth = 0;
		for (int i = 0; i < getChildCount(); i++) {
			LePopMenuItem subItem = (LePopMenuItem) getChildAt(i);
			int itemWidth = subItem.getTextWidth();
			if (itemWidth > maxWidth) {
				maxWidth = itemWidth;
			}
		}
		
		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).measure(maxWidth, 0);
		}

		final LePopMenuItem item = (LePopMenuItem) getChildAt(0);

		mWidth = getPaddingLeft() + getPaddingRight() + mColNum
				* (maxWidth + item.getMarginLeft() + item.getMarginRight());
		mHeight = getPaddingTop() + getPaddingBottom() + mRowNum
				* (item.getMeasuredHeight() + item.getMarginTop() + item.getMarginBottom());

		if (mIsMarginCollapsing) {
			mWidth -= Math.min(item.getMarginLeft(), item.getMarginRight()) * (mColNum - 1);
			mHeight -= Math.max(item.getMarginTop(), item.getMarginBottom()) * (mRowNum - 1);
		}
		setMeasuredDimension(mWidth, mHeight);
	}
	
	@Override
	public void onClick(View v) {
		if (mListener != null) {
			mListener.onPopMenuItemClick(getId(), v.getId());
		}
		if (mCommonCallback != null) {
			mCommonCallback.onDismiss();
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		for (int i = 0; i < getChildCount(); i++) {
			final int row = i / mColNum;
			final int col = i % mColNum;
			final LePopMenuItem item = (LePopMenuItem) getChildAt(i);

			int offsetX = getPaddingLeft();
			int offsetY = getPaddingTop();
			offsetX += (item.getMeasuredWidth() + item.getMarginLeft() + item.getMarginRight()) * col;
			offsetY += (item.getMeasuredHeight() + item.getMarginTop() + item.getMarginBottom()) * row;
			offsetX += item.getMarginLeft();
			offsetY += item.getMarginTop();

			if (mIsMarginCollapsing) {
				offsetX -= Math.min(item.getMarginLeft(), item.getMarginRight()) * col;
				offsetY -= Math.max(item.getMarginTop(), item.getMarginBottom()) * row;
			}

			item.layout(offsetX, offsetY, offsetX + item.getMeasuredWidth(),
					offsetY + item.getMeasuredHeight());
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		mRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth(3);
		mPaint.setColor(UI_DEFAULT_SHADOW);
		
		canvas.drawColor(UI_DEFAULT_BACKGROUND);
		canvas.drawRect(mRect, mPaint);
	}

	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		return super.dispatchKeyEvent(event)|| executeKeyEvent(event);
	}
	
	public boolean executeKeyEvent(KeyEvent event) {
		if (event.getAction() != KeyEvent.ACTION_DOWN) {
			return false;
		}

		View viewToFoucus = null;
		boolean returnValue = true;
		View currentFocused = findFocus();
		if (currentFocused == this) {
			ArrayList<LePopMenuItem> list = getPopMenuItems();
			if(list.size() > 0){
				switch (event.getKeyCode()) {
					case KeyEvent.KEYCODE_DPAD_UP:
						returnValue = true;
						list.get(list.size()-1).setFocusableInTouchMode(true);
						list.get(list.size()-1).requestFocus();
						break;

					case KeyEvent.KEYCODE_DPAD_LEFT:
					case KeyEvent.KEYCODE_DPAD_RIGHT:
					case KeyEvent.KEYCODE_DPAD_DOWN:
						returnValue = true;
						list.get(0).setFocusableInTouchMode(true);
						list.get(0).requestFocus();
						break;
					
					default:
						returnValue = false;
						break;
				}
				return returnValue;
			}
			return false;
		}
		switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_DPAD_UP:
				viewToFoucus = FocusFinder.getInstance().findNextFocus(this, currentFocused, FOCUS_UP);
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				viewToFoucus = FocusFinder.getInstance().findNextFocus(this, currentFocused, FOCUS_DOWN);
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				viewToFoucus = FocusFinder.getInstance().findNextFocus(this, currentFocused, FOCUS_RIGHT);
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				viewToFoucus = FocusFinder.getInstance().findNextFocus(this, currentFocused, FOCUS_LEFT);
				break;
			default:
				returnValue = false;
				break;
		}

		if (viewToFoucus != null) {
			viewToFoucus.requestFocus();
		} else {
			currentFocused.requestFocus();
		}
		return returnValue;
	}


	public interface LePopMenuClickListener {
		void onPopMenuItemClick(int popMenuId, int popMenuItemId);
	}

}
