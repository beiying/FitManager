package com.beiying.fitmanager.core.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import com.beiying.fitmanager.core.BYSafeRunnable;

import java.util.ArrayList;

public class LeGallery extends LeFrameViewGroup {
	private static enum TouchState {
		REST,
		SCROLLING
	};

	private static final int MAX_SCROLL_DURATION = 1000;
	private static final float DEFAULT_ROLL_PADDING = 0.4f;
	private int SNAP_VELOCITY = 300;


	private int mCurScreen;
	private int mTouchSlop;
	protected float mLastMotionX;
	protected float mLastMotionY;
	protected float mDownMotionX;
	protected float mDownMotionY;
	private int mTotalWidth;
	private int mScreenWidth;
	private int mLeftEdge;
	private int mRightEdge;
	private int[] mChildWidths;
	private float mRollPadding;
	private Rect mTempRect = new Rect();

	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;
	private TouchState mTouchState = TouchState.REST;

	private LeGalleryIndicator mIndicator;

	private ArrayList<LeGalleryListener> mListenerList;

	private boolean isXLocked = false;
	private boolean isLockETH = false;
	
	private float mFirstX = -1;
	private float mStartDirection = 0;		//大于0向右，小于0向左
	protected boolean mFrozen = false;
	
	private int mLastScrollX;
	
	private boolean mTriggered;
	
	public LeGallery(Context context) {
		this(context, null);
	}

	public LeGallery(Context context, Interpolator interpolator) {
		super(context);
		init(interpolator);
	}

	private void init(Interpolator interpolator) {
		mScroller = new Scroller(getContext(), interpolator);
		mRollPadding = DEFAULT_ROLL_PADDING;
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
		mVelocityTracker = VelocityTracker.obtain();
		this.setWillNotDraw(true);

		this.reset();

		if (android.os.Build.MODEL.toLowerCase().startsWith("mi-one")) {
			SNAP_VELOCITY = 200;
		}
		mListenerList = new ArrayList<LeGalleryListener>();
	}
	
	public boolean isScrolling() {
		return !mScroller.isFinished();
	}
	
	public void freeze() {
		mFrozen = true;
	}
	
	public void unfreeze() {
		mFrozen = false;
	}

	public void setDefaultScreen(int defScreen) {
		mCurScreen = defScreen;
		
		for (int i = 0; i < mListenerList.size(); i++) {
			mListenerList.get(i).onGalleryScreenChanged(getChildAt(mCurScreen), mCurScreen);
		}
	}

	public void lockX() {
		isXLocked = true;
		lockETH();
	}

	private void lockETH() {
		isLockETH = true;
	}

	public void unlockX() {
		isXLocked = false;
		isLockETH = false;
	}
	
	public boolean isXLocked() {
		return isXLocked;
	}

	public int getCurrX() {
		return this.getScrollX();
	}

	public int getCurScreen() {
		return mCurScreen;
	}

	public Scroller getScroller() {
		return mScroller;
	}

	public void setGalleryIndicator(LeGalleryIndicator indicator) {
		mIndicator = indicator;
		mIndicator.setGallery(this);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		mScreenWidth = View.MeasureSpec.getSize(widthMeasureSpec);

		int count = getChildCount();
		mChildWidths = new int[count];
		mTotalWidth = 0;
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
			final int childWidth = getChildAt(i).getMeasuredWidth();
			mChildWidths[i] = childWidth;
			mTotalWidth += mChildWidths[i];
		}

		mLeftEdge = (int) (-mScreenWidth * mRollPadding);
		mRightEdge = (int) (mTotalWidth + mRollPadding * mScreenWidth);

		this.setMeasuredDimension(mTotalWidth, View.MeasureSpec.getSize(heightMeasureSpec));

		scrollTo(mCurScreen * mScreenWidth, 0);
	}

	/**
	 * NOTE! call me just after constructor
	 */
	public void forbitDriftOver() {
		mRollPadding = 0;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int childLeft = 0;
		final int childCount = getChildCount();

		for (int i = 0; i < childCount; i++) {
			View childView = getChildAt(i);
			if (childView.getVisibility() == View.GONE)
				continue;

			final int childWidth = childView.getMeasuredWidth();
			childView.layout(childLeft, 0, childLeft + childWidth, childView.getMeasuredHeight());
			childLeft += childWidth;
		}
	}
	
	public void setToScreen(int whichScreen) {
		whichScreen = checkBounds(whichScreen);

		int dstLeft = (getChildAt(whichScreen).getLeft());
		if (getScrollX() == dstLeft) {
			return;
		}
		mCurScreen = whichScreen;
		invalidate(); // Redraw the layout

		for (int i = 0; i < mListenerList.size(); i++) {
			mListenerList.get(i).onGalleryScreenChanged(getChildAt(mCurScreen), mCurScreen);
			mListenerList.get(i).onGalleryScreenChangeComplete(getChildAt(mCurScreen), mCurScreen);
		}
		scrollTo(dstLeft, 0);
	}

	public void snapToScreen(int whichScreen) {
		snapToScreenWithDuration(whichScreen, -1);
	}
	
	public void snapToScreenWithVelocity(int whichScreen, float velocity) {
		whichScreen = checkBounds(whichScreen);
		int delta = computeScrollDistance(whichScreen);
		int duration = (int) Math.abs(delta * 1000 / velocity) * 2;
		duration = Math.min(duration, MAX_SCROLL_DURATION);
		snapToScreenWithDuration(whichScreen, duration);
	}

	public void snapToScreenWithDuration(int whichScreen, int duration) {
		whichScreen = checkBounds(whichScreen);
		
		if (getScrollX() == (getChildAt(whichScreen).getLeft())) {
			return;
		}

		int delta = computeScrollDistance(whichScreen);
		int during = duration == -1 ? Math.abs(delta) : duration;
		during = controlScrollDuration(during);
		mLastScrollX = getScrollX();
		mScroller.startScroll(getScrollX(), 0, delta, 0, during);

		mCurScreen = whichScreen;
		invalidate(); // Redraw the layout

		for (int i = 0; i < mListenerList.size(); i++) {
			mListenerList.get(i).onGalleryScreenChanged(getChildAt(mCurScreen), mCurScreen);
		}

		postDelayed(new BYSafeRunnable() {

			@Override
			public void runSafely() {
				for (int i = 0; i < mListenerList.size(); i++) {
					mListenerList.get(i).onGalleryScreenChangeComplete(getChildAt(mCurScreen), mCurScreen);
				}
			}
		}, during + 10);
	}
	
	private int checkBounds(int whichScreen) {
		return (whichScreen < 0 ? 0 : whichScreen > (getChildCount() - 1) ? (getChildCount() - 1)
				: whichScreen);
	}
	
	private int computeScrollDistance(int whichScreen) {
		int delta = getChildAt(whichScreen).getLeft() - getScrollX();
		final int rightShift = getChildAt(whichScreen).getLeft() + mScreenWidth - mTotalWidth;
		if (rightShift > 0) {
			delta -= rightShift;
		}
		return delta;
	}
	
	protected int controlScrollDuration(int duration) {
		return duration / 2;
	}
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		for (int i = 0; i < mListenerList.size(); i++) {
			mListenerList.get(i).onGalleryScrolled(l, t, oldl, oldt);
		}
		super.onScrollChanged(l, t, oldl, oldt);
	}
	
	@Override
	public void computeScroll() {
		if (!mScroller.computeScrollOffset())
			return;
		trigger(mScroller.getCurrX()-mLastScrollX);
		mLastScrollX = mScroller.getCurrX();
		
		scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
		this.invalidate();
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// Let the focused view and/or our descendants get the key first
		return super.dispatchKeyEvent(event) || executeKeyEvent(event);
	}

	public boolean executeKeyEvent(KeyEvent event) {
		mTempRect.setEmpty();
		
		if (!canScroll()) {
			if (isFocused() && event.getKeyCode() != KeyEvent.KEYCODE_BACK) {
				View currentFocused = findFocus();
				if (currentFocused == this)
					currentFocused = null;
				View nextFocused = FocusFinder.getInstance().findNextFocus(this, currentFocused,
						View.FOCUS_RIGHT);
				return nextFocused != null && nextFocused != this
						&& nextFocused.requestFocus(View.FOCUS_RIGHT);
			}
			return false;
		}

		boolean handled = false;
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (event.getKeyCode()) {
				case KeyEvent.KEYCODE_DPAD_LEFT:
					handled = arrowScroll(View.FOCUS_LEFT);
					break;
				case KeyEvent.KEYCODE_DPAD_RIGHT:
					handled = arrowScroll(View.FOCUS_RIGHT);
					break;
			}
		}

		return handled;
	}
	
	private boolean canScroll() {
		return mTotalWidth > mScreenWidth;
	}
	
	public boolean arrowScroll(int direction) {

		View currentFocused = findFocus();
		if (currentFocused == this) {
			currentFocused = null;
		}

		View nextFocused = FocusFinder.getInstance().findNextFocus(this, currentFocused, direction);
		if (nextFocused == null) {
			return false;
		}
		offsetDescendantRectToMyCoords(nextFocused, mTempRect);
		
		if (mTempRect.left >= mCurScreen * mScreenWidth && mTempRect.right < (mCurScreen + 1) * mScreenWidth) {
			return false;
		}
		if (direction == View.FOCUS_RIGHT) {
			int nextIndex = mCurScreen + 1;
			snapToScreen(nextIndex);
			nextFocused.requestFocus(direction);
		} else if (direction == View.FOCUS_LEFT) {
			int nextIndex = mCurScreen - 1;
			snapToScreen(nextIndex);
			nextFocused.requestFocus(direction);
		}
		return true;
	}
	
	/**
	 * 获取开始滑动方向
	 * @return
	 */
	public float getStartDirection() {
		return mStartDirection;
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (mFrozen) {
			return super.dispatchTouchEvent(ev);
		}
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			reset();
		}
		float x = ev.getX();
		if (mFirstX == -1) {
			mFirstX = x;
		} else if (mStartDirection == 0) {
			mStartDirection = x - mFirstX;
		}
		mVelocityTracker.addMovement(ev);
		if (ev.getAction() == MotionEvent.ACTION_CANCEL || ev.getAction() == MotionEvent.ACTION_UP)
			if (isXLocked)
				this.unlockX();
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (mFrozen) {
			return super.onInterceptTouchEvent(event);
		}
		final int action = event.getAction();
		if ((action == MotionEvent.ACTION_MOVE) && (mTouchState != TouchState.REST) && !isLockETH && !isXLocked) {
			return true;
		}

		final float x = event.getX();

		switch (action) {

			case MotionEvent.ACTION_MOVE:
				final int xDiff = (int) Math.abs(mLastMotionX - x);
				if (xDiff > mTouchSlop) {
					mTouchState = TouchState.SCROLLING;
					mLastMotionX = x;
					if (xDiff < Math.abs(event.getY() - mDownMotionY)) {
						this.lockX();
					}
				}
				break;
			case MotionEvent.ACTION_DOWN:
				mLastMotionX = x;
				mDownMotionY = event.getY();
				mTouchState = mScroller.isFinished() ? TouchState.REST : TouchState.SCROLLING;
				break;

			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				mTouchState = TouchState.REST;
				isLockETH = isXLocked = false;
				break;
			default:
				break;
		}

		if (isLockETH)
			return false;

		return mTouchState != TouchState.REST;
	}
	
	protected void forceFinishScroller() {
		if (!mScroller.isFinished()) {
			mScroller.forceFinished(true);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mFrozen) {
			return super.onTouchEvent(event);
		}
		final int action = event.getAction();
		final float x = event.getX();

		switch (action) {
			case MotionEvent.ACTION_DOWN:
				forceFinishScroller();
				
				mDownMotionX = mLastMotionX = x;
				mLastMotionY = event.getY();
				break;

			case MotionEvent.ACTION_MOVE:
				if (this.isXLocked)
					break;

				int deltaX = (int) (mLastMotionX - x);
				mLastMotionX = x;
				mLastMotionY = event.getY();
				
				trigger(deltaX);
				int dst = getScrollX() + deltaX;
				if (dst < 0) {
					if (mLeftEdge == 0) {
						deltaX = -getScrollX();
					} else {
						deltaX = (int) (deltaX * (mLeftEdge - getScrollX()) / (mLeftEdge * 1.0f));
					}
				} else if (dst > (mTotalWidth - mScreenWidth)) {
					if (mRightEdge == 0) {
						deltaX = mScreenWidth - getScrollX();
					} else {
						int maxExceed = mRightEdge - mTotalWidth;
						int realExceed = getScrollX() - (mTotalWidth - mScreenWidth);
						if (realExceed < 0) {
							deltaX = (-realExceed);
						} else if (maxExceed == 0){
							deltaX = (-realExceed);
						} else {
							deltaX = Math.round(deltaX * (1f - (float) realExceed / (float)maxExceed));
						}
//						deltaX = (int) (deltaX * (mRightEdge - getScrollX()) / (mRightEdge * 1.0f));
					}
				}
				scrollBy(deltaX, 0);
				break;

			case MotionEvent.ACTION_UP:
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(1000);
				float velocityX = velocityTracker.getXVelocity();
				if (velocityX > SNAP_VELOCITY && mCurScreen > 0) { // left
					touchEndSnap(mCurScreen - 1, velocityX);
				} else if (velocityX < -SNAP_VELOCITY && mCurScreen < getChildCount() - 1) {
					touchEndSnap(mCurScreen + 1, velocityX);
				} else {
					snapAccordCurrX();
				}

				reset();
				break;

			case MotionEvent.ACTION_CANCEL:
				snapAccordCurrX();

				reset();
				break;
				
			default:
				break;
		}

		return true;
	}
	
	protected boolean trigger(final int deltaX) {
		if (mTriggered) {
			return false;
		}
		for (int i = 0; i < mListenerList.size(); i++) {
			mListenerList.get(i).onXChange(deltaX);
		}
		mTriggered = true;
		return true;
	}
	
	protected void touchEndSnap(int screen, float velocityX) {
		snapToScreenWithVelocity(screen, velocityX);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (mIndicator != null) {
			mIndicator.invalidate();
		}
	}

	private void reset() {
		this.unlockX();
		mVelocityTracker.clear();
		mTouchState = TouchState.REST;
		
		mFirstX = -1;
		mStartDirection = 0;
		
		mTriggered = false;
	}

	private void snapAccordCurrX() {
		int destScreen = 0;
		int currentWidth = 0;

		for (destScreen = 0; destScreen < getChildCount(); destScreen++) {
			int childWidth = getChildAt(destScreen).getMeasuredWidth();
			if (currentWidth + childWidth > getScrollX()) {
				break;
			}
			currentWidth += childWidth;
		}
		if (destScreen + 1 < getChildCount()
				&& getScrollX() - currentWidth > getChildAt(destScreen).getMeasuredWidth() / 2) {
			destScreen += 1;
		}
		snapToScreen(destScreen);
	}

	public void addGalleryChangeListener(LeGalleryListener listener) {
		mListenerList.add(listener);
	}
	
	public static class LeGalleryAdapter implements LeGalleryListener {

		@Override
		public void onGalleryScreenChanged(View view, int screen) {
			
		}

		@Override
		public void onGalleryScreenChangeComplete(View view, int screen) {
			
		}

		@Override
		public void onGalleryScrolled(int l, int t, int oldl, int oldt) {
			
		}

		@Override
		public void onXChange(int delta) {
			
		}
		
	}

	public interface LeGalleryListener {
		void onGalleryScreenChanged(View view, int screen);
		void onGalleryScreenChangeComplete(View view, int screen);
		void onGalleryScrolled(int l, int t, int oldl, int oldt);
		void onXChange(int delta);
	}
}
