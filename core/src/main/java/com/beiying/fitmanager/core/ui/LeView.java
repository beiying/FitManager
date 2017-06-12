/** 
 * Filename:    LeView.java
 * Description:  
 * Copyright:   Lenovo PCL Copyright(c)2013 
 * @author:     chenwei07 
 * @version:    1.0
 * Create at:   2013-6-26 下午1:38:48
 * 
 * Modification History: 
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------ 
 * 2013-6-26     chenwei07    1.0         1.0 Version 
 */
package com.beiying.fitmanager.core.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class LeView extends FrameLayout implements LeThemable {

	public LeView(Context context) {
		super(context);
	}
	
	public LeView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void setPressed(boolean pressed) {
		super.setPressed(pressed);
		invalidate();
	}

	@Override
	public void onThemeChanged() {
		
	}

}
