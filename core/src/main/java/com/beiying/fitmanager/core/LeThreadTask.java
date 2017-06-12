package com.beiying.fitmanager.core;

public abstract class LeThreadTask extends LeSafeRunnable implements Comparable<LeThreadTask> {

	public static final int PRIORITY_HIGH = 10;
	public static final int PRIORITY_DEFAULT = 0;
	public static final int PRIORITY_LOW = -10;
	
	private int mPriority;
	
	public LeThreadTask() {
		this(PRIORITY_DEFAULT);
	}
	
	/**
	 * 
	 * @param priority see {@link #PRIORITY_HIGH}, {@link #PRIORITY_DEFAULT}, {@link #PRIORITY_LOW}
	 */
	public LeThreadTask(int priority) {
		this(priority, true);
	}
	
	public LeThreadTask(boolean checkSafe) {
		this(PRIORITY_DEFAULT, checkSafe);
	}
	
	public LeThreadTask(int priority, boolean checkSafe) {
		super(null, checkSafe);
		mPriority = priority;
	}
	
	@Override
	public int compareTo(LeThreadTask another) {
		return mPriority - another.mPriority;
	}
}
