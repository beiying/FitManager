package com.beiying.fitmanager.core.ui;

import android.view.animation.AnimationUtils;

public class LeProcessor {
	private float mStartProcess;
	private float mFinalProcess;
	private float mDeltaProcess;

	private float mCurrProcess;
	private long mStartTime;
	private int mDuration;
	private float mDurationReciprocal;
	private boolean mIsFinished;
	
	private LeProcessListener mListener;
	
	public static float checkProcess(float process) {
		float result = process;
		result = Math.min(process, 1.0f);
		result = Math.max(result, 0.0f);
		return result;
	}

	public LeProcessor() {
		mIsFinished = true;
	}

	public void startProcess(float startProcess, float finalProcess, int duration) {
		mIsFinished = false;
		mDuration = duration;
		mStartTime = AnimationUtils.currentAnimationTimeMillis();
		mStartProcess = startProcess;
		mFinalProcess = finalProcess;
		mDeltaProcess = finalProcess - startProcess;
		mDurationReciprocal = 1.0f / (float) mDuration;
	}

	public boolean computeProcessOffset() {
		if (mIsFinished) {
			return false;
		}

		int timePassed = (int) (AnimationUtils.currentAnimationTimeMillis() - mStartTime);

		if (timePassed < mDuration - 10) {
			float x = timePassed * mDurationReciprocal;
			mCurrProcess = mStartProcess + x * mDeltaProcess;
		} else {
			mCurrProcess = mFinalProcess;
			mIsFinished = true;
			fireProcessEnd();
		}
		return true;
	}

	public final boolean isFinished() {
		return mIsFinished;
	}

	public final void forceFinished(boolean finished) {
		mIsFinished = finished;
		fireProcessEnd();
	}

	public final int getDuration() {
		return mDuration;
	}

	public final float getCurrProcess() {
		return mCurrProcess;
	}
	
	public final void setCurrProcess(float process) {
		mCurrProcess = process;
	}
	
	public void setProcessListener(LeProcessListener listener) {
		mListener = listener;
	}

	private void fireProcessEnd() {
		if (mListener != null) {
			mListener.onProcessEnd();
		}
	}

	public interface LeProcessListener {
		void onProcessEnd();
	}
}