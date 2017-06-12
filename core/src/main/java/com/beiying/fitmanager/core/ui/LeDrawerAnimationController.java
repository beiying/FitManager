package com.beiying.fitmanager.core.ui;


import com.beiying.fitmanager.core.LeLog;

public abstract class LeDrawerAnimationController {
	/** 下拉进程或收起进程达到此阈值时，切换到自动动画 */
	public static final float DRAG_PROCESS_THRESHOLD = 0.2f;

	/** 当前动画的动作，无动作，拖动，自动向下或自动向上 */
	public static final int ACTION_NONE = 0;
	public static final int ACTION_DRAG = 1;
	public static final int ACTION_MOVING_DOWN = 2;
	public static final int ACTION_MOVING_UP = 3;

	/** 当前动画的状态，收起或展开 */
	public static final int STATE_FOLD = 4;
	public static final int STATE_EXPAND = 5;
	
	private static final boolean LOG = false;

	/** 当前动画的动作 */
	private int mActionType;
	/** 当前动画的状态，区别于mIsExpand，mIsExpand用于排版布局，mStateType用于动画控制 */
	private int mStateType;

	/**
	 * 动画共有8种情况： 1. 收起状态 2. 展开状态 3. 收起状态用户拖动 4. 展开状态用户拖动 5. 收起状态自动向上 6. 展开状态自动向上
	 * 7. 收起状态自动向下 8. 展开状态自动向下
	 */
	
	public LeDrawerAnimationController() {
		mActionType = ACTION_NONE;
		mStateType = STATE_FOLD;
	}

	public int getActionType() {
		return mActionType;
	}

	public void setActionType(int actionType) {
		this.mActionType = actionType;
	}

	public int getStateType() {
		return mStateType;
	}

	public void setStateType(int stateType) {
		this.mStateType = stateType;
	}
	
	public void startShowAnimation() {
		if (LOG) {
			dumpLog("startShowAnimation()");
		}
		/** 情况1、3、4时，可以执行该方法 */
		final boolean flag = (mStateType == STATE_FOLD && mActionType == ACTION_NONE)
				|| mActionType == ACTION_DRAG;
		if (!flag) {
			return;
		}
		
		mActionType = ACTION_MOVING_DOWN;

		startShowAnimationInner(0);
	}

	public void startDismissAnimation() {
		if (LOG) {
			dumpLog("startDismissAnimation()");
		}
		/** 情况2、3、4时，可以执行该方法 */
		final boolean flag = (mStateType == STATE_EXPAND && mActionType == ACTION_NONE)
				|| mActionType == ACTION_DRAG;
		if (!flag) {
			return;
		}
		
		
		mActionType = ACTION_MOVING_UP;

		startDismissAnimationInner(1);
	}

	public void startShowAnimation(float process) {
		if (LOG) {
			dumpLog("startShowAnimation(float process)");
		}
		/** 情况1、3、4时，可以执行该方法 */
		final boolean flag = (mStateType == STATE_FOLD && mActionType == ACTION_NONE)
				|| mActionType == ACTION_DRAG;
		if (!flag) {
			return;
		}
		
		mActionType = ACTION_MOVING_DOWN;

		startShowAnimationInner(checkProcess(process));
	}

	public void startDismissAnimation(float process) {
		if (LOG) {
			dumpLog("startDismissAnimation(float process)");
		}
		/** 情况2、3、4时，可以执行该方法 */
		final boolean flag = (mStateType == STATE_EXPAND && mActionType == ACTION_NONE)
				|| mActionType == ACTION_DRAG;
		if (!flag) {
			return;
		}
		
		mActionType = ACTION_MOVING_UP;

		startDismissAnimationInner(checkProcess(process));
	}

	public void simulateShowAnimation(float process) {
		if (LOG) {
			dumpLog("simulateShowAnimation(float process)");
		}
		/** 情况1、3时，可以执行该方法 */
		final boolean flag = mStateType == STATE_FOLD
				&& (mActionType == ACTION_NONE || mActionType == ACTION_DRAG);
		if (!flag) {
			return;
		}
		
		mActionType = ACTION_DRAG;

		simulateShowAnimationInner(checkProcess(process));
	}

	public void simulateDismissAnimation(float process) {
		if (LOG) {
			dumpLog("simulateDismissAnimation(float process)");
		}
		/** 情况2、4时，可以执行该方法 */
		final boolean flag = mStateType == STATE_EXPAND
				&& (mActionType == ACTION_NONE || mActionType == ACTION_DRAG);
		if (!flag) {
			return;
		}
		
		
		mActionType = ACTION_DRAG;

		simulateDismissAnimationInner(checkProcess(process));

	}

	public void showWithoutAnimation() {
		if (LOG) {
			dumpLog("showWithoutAnimation()");
		}
		/** 情况1,3,4时，可以执行该方法 */
		final boolean flag = (mStateType == STATE_FOLD && mActionType == ACTION_NONE)
				|| mActionType == ACTION_DRAG;
		if (!flag) {
			return;
		}
		
		mStateType = STATE_EXPAND;
		mActionType = ACTION_NONE;

		showWithoutAnimationInner();
	}

	public void dismissWithoutAnimation() {
		if (LOG) {
			dumpLog("dismissWithoutAnimation()");
		}
		/** 情况2,3,4时，可以执行该方法 */
		final boolean flag = (mStateType == STATE_EXPAND && mActionType == ACTION_NONE) 
				|| mActionType == ACTION_DRAG;
		if (!flag) {
			return;
		}
		
		mStateType = STATE_FOLD;
		mActionType = ACTION_NONE;

		dismissWithoutAnimationInner();
	}

	public void onShowAnimationFinished() {
		if (LOG) {
			dumpLog("onShowAnimationFinished()");
		}
		
		mStateType = STATE_EXPAND;
		mActionType = ACTION_NONE;

		onShowAnimationFinishedInner();
	}

	public void onDismissAnimationFinished() {
		if (LOG) {
			dumpLog("onDismissAnimationFinished()");
		}
		
		mStateType = STATE_FOLD;
		mActionType = ACTION_NONE;

		onDismissAnimationFinishedInner();
	}
	
	private void dumpLog(String tag) {
		LeLog.i("gyy: " + tag + " :mStateType:" + getString(mStateType) + "|mActionType:" + getString(mActionType));
	}
	
	private String getString(int stateOrAction) {
		switch (stateOrAction) {
		case ACTION_NONE:
			return "ACTION_NONE";
		case ACTION_DRAG:
			return "ACTION_DRAG";
		case ACTION_MOVING_DOWN:
			return "ACTION_MOVING_DOWN";
		case ACTION_MOVING_UP:
			return "ACTION_MOVING_UP";
		case STATE_FOLD:
			return "STATE_FOLD";
		case STATE_EXPAND:
			return "STATE_EXPAND";
		default:
			break;
		}
		return "";
	}
	
	private float checkProcess(float process) {
		float result = process;
		result = Math.min(process, 1.0f);
		result = Math.max(result, 0.0f);
		return result;
	}

	/** 以下方法子类通过重载，用于控制动画进程 */
	protected abstract void startShowAnimationInner(float process);

	protected abstract void startDismissAnimationInner(float process);

	protected abstract void simulateShowAnimationInner(float process);

	protected abstract void simulateDismissAnimationInner(float process);

	protected abstract void showWithoutAnimationInner();

	protected abstract void dismissWithoutAnimationInner();

	protected abstract void onShowAnimationFinishedInner();

	protected abstract void onDismissAnimationFinishedInner();
}
