package com.beiying.fitmanager.manager;

import android.os.Looper;
import android.util.Log;

import com.beiying.fitmanager.BYBasicContainer;
import com.beiying.fitmanager.core.ContextContainer;
import com.beiying.fitmanager.core.LeLog;
import com.beiying.fitmanager.core.LeSafeBox;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * LeBasicManager是热启动重用模型的核心，实现了实例的生命周期管理，提供重用onReuse和释放onRelease的接口。
 * 
 * 模块Manager可直接继承LeBasicManager，并在getInstance方法中sInstance不为null调用reuse。
 * 在onReuse方法中实现重用逻辑，重用逻辑必须重新构建与Context相当的实例。
 * 在onRelease中实现释放逻辑。如果需要重用，释放逻辑应当保持sInstance，不能置为null；如果不需要重用，必须在onRelease中将sInstance置为null。
 * 
 * 需要注意的是，重用是以空间来换取时间，并不是所有的模块都需要重用，对于不需要重用的模块，务必在onRelease中将实例置空。
 * 
 * 继承LeBasicManager的单例getInstance务必使用以下写法：
 *
 * public static ModuleManager getInstance() {
 * 		if (sInstance != null && sInstance.reuse()) {	//这里优先处理复杂的重用逻辑
 * 			return sInstance;
 * 		}
 * 		if (sInstance == null) {
 * 			synchronized (ModuleManager.class) {
 * 				if (sInstance == null) {
 * 					sInstance = new ModuleManager();
 * 				}
 * 			}
 * 		}
 * 		return sInstance;
 * }
 *
 */
public class LeBasicManager extends BYBasicContainer {

	private static final boolean DEBUG = false;

	private static final String LOG_TAG = LeBasicManager.class.getSimpleName();

	private boolean mCreated;
	private boolean mReused;
	protected boolean mReleased;
	protected boolean mInstanceExisted;
	
	private boolean mEagerRelease;
	
	private static List<LeBasicManager> sMainEagerList = new ArrayList<LeBasicManager>();
	private static List<LeBasicManager> sMainLazyList = new ArrayList<LeBasicManager>();

	private static List<LeBasicManager> sOtherEagerList = new ArrayList<LeBasicManager>();
	private static List<LeBasicManager> sOtherLazyList = new ArrayList<LeBasicManager>();

	protected LeBasicManager() {
		this(false);
	}

	/**
	 * 
	 * @param eagerRelease
	 *            true表示需要在finish Activty之前释放；false表示在Activity onDestroy中释放
	 * 
	 */
	protected LeBasicManager(boolean eagerRelease) {
		mEagerRelease = eagerRelease;
		
		if (DEBUG) {
			if (!LeSafeBox.isContextAlive(ContextContainer.sContext)) {
                String str = "Creating " + getClass().getSimpleName() + " when context is dead!!!";
                try {
                    throw new RuntimeException(str);
                } catch (Exception e) {
                    LeLog.e(LOG_TAG, Log.getStackTraceString(e));
                }

			}
		}

		record(true);
		
		mCreated = true;
		mReused = false;
		mReleased = false;
	}

	/**
	 * 记录Manager实例时，如果是来自主线程，为了防止性能损耗，不使用同步锁，直接加入Main List中；如果来自其他线程，使用同步锁，加入到Other List中
	 * 
	 * 如果出现多线程访问异常，说明在某个Manager的onRelease中执行了某个Manager的getInstance，这是严重错误，需要进行排查
	 * 。 切忌在此捕获异常
	 */
	private void record(final boolean create) {
		final LeBasicManager me = this;
		boolean inMainThread = false;
		if (Looper.myLooper() == Looper.getMainLooper()) {
			if (mEagerRelease) {
				sMainEagerList.add(me);
			} else {
				sMainLazyList.add(me);
			}
			inMainThread = true;
		} else {
			if (mEagerRelease) {
				synchronized (sOtherEagerList) {
					sOtherEagerList.add(me);
				}
			} else {
				synchronized (sOtherLazyList) {
					sOtherLazyList.add(me);
				}
			}
		}
		mInstanceExisted = true;
		
		if (DEBUG) {
			String str = create ? "create" : "resume";
			String threadStr = inMainThread ? " in MainThread" : " in OtherThread";
			String eagerOrLazy = mEagerRelease ? " Eager" : " Lazy";
			LeLog.i(LOG_TAG, "try " + str + ": " + me.getClass().getSimpleName() + threadStr + eagerOrLazy);
		}
	}
	
	/**
	 * 在Manager.getInstance方法中sInstance不为null调用
	 */
	final protected boolean reuse() {
		if (mCreated) {
			return true;
		}
		if (!mReused) {
			synchronized (this) {
				if (!mInstanceExisted) {
					return false;
				}
				if (!mReused) {
					
					if (!mReleased) {
						if (doRelease(true)) {
							return false;
						}
					}
					
					doReuse();
				}
			}
		}
		return true;
	}
	
	private void doReuse() {
		record(false);

		onReuse();
		
		mCreated = false;
		mReused = true;
		mReleased = false;
	}
	
	final private void release(boolean singleRelease) {
		if (mReleased) {
			return ;
		}
		
		doRelease(singleRelease);
	}
	
	private boolean doRelease(boolean singleRelease) {
		boolean ret = onRelease();
		if (singleRelease && ret) {
			if (mEagerRelease) {
				synchronized (sMainEagerList) {
					sMainEagerList.remove(this);
				}
				synchronized (sOtherEagerList) {
					sOtherEagerList.remove(this);
				}
			} else {
				synchronized (sMainLazyList) {
					sMainLazyList.remove(this);
				}
				synchronized (sOtherLazyList) {
					sOtherLazyList.remove(this);
				}
			}
		}
		synchronized (this) {
			mCreated = false;
			mReused = false;
			mReleased = true;
			
			if (ret) {
				mInstanceExisted = false;
			}
			
			if (DEBUG) {
				LeLog.i(LOG_TAG, getClass().getSimpleName());
			}
		}
		return ret;
	}
	
	/**
	 * 重用逻辑在此实现
	 * 
	 * 可参考LeThemeManager
	 */
	protected void onReuse() {

	}

	/**
	 * 释放逻辑在此实现
	 * 
	 * 如果模块需要热启动重用，那么instance不要置为null，返回false；否则将instance置为null，返回true。
     *
     * 绝对不能在onRelease创建BasicManager的实例！！！！！！！这是不科学的做法。
     *
     * 如果错误地在onRelease中创建BasicManager的实例，lazyRecycle中会输出错误提示。
	 */
	protected boolean onRelease() {
		return true;
	}
	
	public static void eagerRecycle() {
		LeLog.i(LOG_TAG, "eager recycle");
		if (sMainEagerList != null) {
			for (LeBasicManager manager : sMainEagerList) {
				if (manager != null) {
					manager.release(false);
				}
			}
			sMainEagerList.clear();
		}
		if (sOtherEagerList != null) {
			for (LeBasicManager manager : sOtherEagerList) {
				if (manager != null) {
					manager.release(false);
				}
			}
			sOtherEagerList.clear();
		}
	}

	public static void lazyRecycle() {
		LeLog.i(LOG_TAG, "lazy recycle");
        try {
            synchronized (sMainLazyList) {
                for (LeBasicManager manager : sMainLazyList) {
                    if (manager != null) {
                        manager.release(false);
                    }
                }
                sMainLazyList.clear();
            }
            synchronized (sOtherLazyList) {
                for (LeBasicManager manager : sOtherLazyList) {
                    if (manager != null) {
                        manager.release(false);
                    }
                }
                sOtherLazyList.clear();
            }
        } catch (ConcurrentModificationException e) {
            LeLog.e(LOG_TAG, "Someone create BasicManager instance in onRelease. " +
                    "Check if you invoke getInstance of super class of BasicManager in onRelease");
        }
	}
	
	/**
	 * Demo
	 */
	private static class LeReusableManager extends LeBasicManager {
		
		private static LeReusableManager sInstance;
		
		private LeReusableManager() {
			
		}

		public static LeReusableManager getInstance() {
			if (sInstance != null && sInstance.reuse()) {
				return sInstance;
			}
			if (sInstance == null) {
				synchronized (LeReusableManager.class) {
					if (sInstance == null) {
						sInstance = new LeReusableManager();
					}
				}
			}
			return sInstance;
		}
		
		@Override
		protected boolean onRelease() {
			return false;
		}
		
		@Override
		protected void onReuse() {
			
		}
	}
	
	private static class LeUnreusableManager extends LeBasicManager {
		
		private static LeUnreusableManager sInstance;
		
		private LeUnreusableManager() {
			
		}
		
		public static LeUnreusableManager getInstance() {
			if (sInstance != null && sInstance.reuse()) {
				return sInstance;
			}
			if (sInstance == null) {
				synchronized (LeUnreusableManager.class) {
					if (sInstance == null) {
						sInstance = new LeUnreusableManager();
					}
				}
			}
			return sInstance;
		}
		
		@Override
		protected boolean onRelease() {
			sInstance = null;
			return true;
		}
		
		@Override
		protected void onReuse() {
			
		}
	}

}
