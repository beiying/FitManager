package com.beiying.fitmanager.core.ui;

import android.content.Context;
import android.view.KeyEvent;

public class LeToolbarButton extends LeIconButton {
	
	private int mPos = -1;

	public LeToolbarButton(Context context) {
		super(context);
	}
	
	public int getPos() {
		return mPos;
	}

	public void setPos(int pos) {
		mPos = pos;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		return super.dispatchKeyEvent(event)|| executeKeyEvent(event);
	}

	private boolean executeKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
			return true;
		}
		return false;
	}
	
	
}
