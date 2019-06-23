package com.beiying.fitmanager.core.ui;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;

import com.beiying.fitmanager.core.BYSafeRunnable;
import com.beiying.fitmanager.core.LeLog;


public class LeAdjustResizeView extends LeView {
	
	private int mNormalTop = -1;
    
	public LeAdjustResizeView(Context context) {
		this(context, false);
	}
	
    public LeAdjustResizeView(Context context, final boolean drawStatusbar) {
		super(context);
		
		final BYSafeRunnable guessKeyboardVisibilityChanged = new BYSafeRunnable() {

	        @Override
	        public void runSafely() {
	        	Rect visibleRect = new Rect();
				LeAdjustResizeView.this.getWindowVisibleDisplayFrame(visibleRect);
				if (drawStatusbar) {
					visibleRect.top = 0;
				}
				final int heightDiff = LeAdjustResizeView.this.getHeight()
						- (visibleRect.bottom - visibleRect.top);
				/*LeLog.i("CW", "root top:" + LeRootView.this.getTop());
				LeLog.i("CW", "visible top:" + visibleRect.top);*/
				
				onKeyboardVisibilityChanged(heightDiff);
	        }
	    };
	    
	    OnGlobalLayoutListener globalLayoutListener = new OnGlobalLayoutListener() {

	        @Override
	        public void onGlobalLayout() {
	        	if (mNormalTop == -1) {
	        		mNormalTop = getRootParentTop();
	        	}
	            removeCallbacks(guessKeyboardVisibilityChanged);
	            int delay = 0;
	            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
	            	delay = 300;
	            }
	            postDelayed(guessKeyboardVisibilityChanged, delay);
	        }
	    };
		
		ViewTreeObserver viewTreeObserver = getViewTreeObserver();
		if (viewTreeObserver != null && viewTreeObserver.isAlive()) {
			try {
				viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener);
			} catch (Exception e) {
				LeLog.e(e);
			}
		}
	}
    
    final protected int getRootParentTop() {
		if (getParent() != null) {
			return ((View) getParent()).getTop();
		}
		return 0;
	}
    
    final protected int getNormalTop() {
    	return mNormalTop;
    }
    
	protected void onKeyboardVisibilityChanged(final int keyboardHeight) {
		int rootParentTop = getRootParentTop();
		final boolean isVisible = keyboardHeight > 0;
		if (rootParentTop == 0 || rootParentTop == mNormalTop || !isVisible) {
			int paddingBottom = keyboardHeight;
			for (int i = 0; i < getChildCount(); i++) {
				View child = getChildAt(i);
				if (child != null && child.getVisibility() == View.VISIBLE) {
					shouldSetChildPaddingBottom(child, paddingBottom);
				}
			}
		}
	}

	final protected boolean shouldSetChildPaddingBottom(View target, int paddingBottom) {
    	if (target == null) {
    		return false;
    	}
		FrameLayout.LayoutParams lParams = (LayoutParams) target
				.getLayoutParams();
		if (lParams.bottomMargin != paddingBottom) {
			lParams.bottomMargin = paddingBottom;
	
			target.requestLayout();
			
			//LeLog.i("CW", "set bottomMargin:" + paddingBottom);
            return true;
		}
        return false;
	}
	
}
