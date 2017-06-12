package com.beiying.fitmanager.framework;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.beiying.fitmanager.core.ui.LeScrollView;

public class BYInsetsScrollView extends LeScrollView {
	
	private static final int sInsetsForeground = 2000;
	private Rect mInsetsRect;
	public BYInsetsScrollView(Context context) {
		super(context);
		setWillNotDraw(true);
	}
	@Override
	protected boolean fitSystemWindows(Rect insets) {
		// TODO Auto-generated method stub
		return super.fitSystemWindows(insets);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
//		getContext().getdra
//		int width = getWidth();
//		int heght = getHeight();
//		
//		if (mInsetsRect != null &&  sInsetsForeground >= 0) {
//			int save = canvas.save();
//			canvas.translate(getScrollX(), getScrollY());
//			
//		}
		
	}
	

}
