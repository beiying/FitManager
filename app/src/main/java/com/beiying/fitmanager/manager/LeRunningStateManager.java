package com.beiying.fitmanager.manager;

import android.content.Context;

import com.beiying.fitmanager.core.LeLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**

 * file: LeRunningStateManager.java

 * package: com.lenovo.browser

 * @author zyb

 */
public class LeRunningStateManager {
	
	public static final String PATH = LeFileManager.getDirFiles() + File.separator + "my.out";
	
	public static final String HOME_PRE = "home://";
	
	public static void updateState() {
		
	}
	
	public static void saveState() {
//		LeWindowManager windowManager = BYControlCenter.getInstance().getWindowManager();
//		if (windowManager != null) {
//			LeMultWindowsState multWindowsState = new LeMultWindowsState();
//			int currentIndex = windowManager.getCurrentIndex();
//			multWindowsState.setCurrentIndex(currentIndex);
//			multWindowsState.setSaveTime(System.currentTimeMillis());
//			List<LeWindow> list = windowManager.getWindowList();
//			if (list != null) {
//				int n = list.size();
//				for (int i = 0; i < n; i++) {
//					LeWindow window = list.get(i);
//					LeWindowState state = new LeWindowState();
//					state.mIndex = i;
//					if (window instanceof LeExploreWindow) {
//						state.mUrl = ((LeExploreWindow) window).getExploreWrapper().getCurrentUrl();
//						state.mTitle = ((LeExploreWindow) window).getExploreWrapper().getCurrentTitle();
//						state.mIsNewFlag = ((LeExploreWindow) window).getExploreWrapper().getModel().getNewFlag();
//					} else if (window instanceof LeHomeWindow) {
//						state.mUrl = HOME_PRE;
//					}
//					multWindowsState.addState(state);
//				}
//			}
//
//			String rssPath = LeRssManager.getShowingPath();
//			if (rssPath != null) {
//				multWindowsState.setCurrentFeaturePath(rssPath);
//			}
//
//			try {
//				ObjectOutputStream oos;
//				LeLog.e("zyb save " + multWindowsState.toString());
//				oos = new ObjectOutputStream(new FileOutputStream(PATH));
//				oos.writeObject(multWindowsState);
//				oos.flush();  //缓冲流
//				oos.close(); //关闭流
//			} catch (FileNotFoundException e) {
//				LeLog.e(e);
//			} catch (IOException e) {
//				LeLog.e(e);
//			} catch (Exception e) {
//				LeLog.e(e);
//			}
//		}
	}

	public static void showRestoreMultWinStatePrompt(final LeMultWindowsState state, final Context context) {
//		new Handler(Looper.getMainLooper()).postDelayed(new LeSafeRunnable() {
//			@Override
//			public void runSafely() {
//				final LeRestorePrompt toast = new LeRestorePrompt(context);
//				toast.setListener(new LeRestorePrompt.LeRestorePromptListener() {
//
//					@Override
//					public void onReStore() {
//						reStoreMultWinState(state);
//					}
//
//					@Override
//					public void onClosed() {
//						clearMultWinState();
//					}
//				});
//				new Handler(Looper.getMainLooper()).post(new LeSafeRunnable() {
//					@Override
//					public void runSafely() {
//						LeControlCenter.getInstance().showFloatView(toast, toast.createFloatCallback());
//					}
//				});
//			}
//		}, 200);
	}

	public static void reStoreMultWinState(LeMultWindowsState state) {
//		List<LeWindowState> stateList = state.getWindowStates();
//		if (stateList == null) {
//			return;
//		}
//		String feature = state.getFeature();
//		int len = stateList.size();
//		LeWindowManager manager = LeControlCenter.getInstance().getWindowManager();
//		int currentIndex = state.getCurrentIndex();
//		int originWinNum = manager.getWindowNumber();
//		for (int i = 0; i < len; i++) {
//			LeRunningStateManager.LeWindowState s = stateList.get(i);
//			if (s.mUrl != null) {
//				if (s.mUrl.startsWith("home")) {
//					if (i < originWinNum) {
//						if (manager.getWrapper(i) instanceof LeWindowWrapper.LeHomeWrapper) {
//							//do nothing.
//						} else {
//							LeLog.e("zyb : tryRestoreMultWInState error, it is not init the home page!");
//						}
//					} else {
//						manager.openNewHomeWindow(false);
//					}
//				} else {
//					if (i < originWinNum) {
//						LeExploreManager exploreManager = manager.getWrapper(i).fetchNewForegroundExplorer(false);
//						exploreManager.setUnLoadedTitle(s.mTitle);
//						exploreManager.getModel().setStartUrl(s.mUrl);
//						exploreManager.getModel().setHasLoaded(false);
//						exploreManager.getModel().setNewFlag(s.mIsNewFlag);
//					} else {
//						manager.openUnloadedExploreWindow(s.mUrl, s.mTitle, true, s.mIsNewFlag);
//					}
//				}
//			}
//		}
//
//		int index = manager.getCurrentIndex();
//		if (index == currentIndex) {
////			LeLog.e("zyb try resume index == currentIndex ---");
//			manager.getWrapper(index).resume();
//		} else {
////			LeLog.e("zyb go go go swtich!");
//			manager.switchWindow(currentIndex, null, false);
//		}
//
//		if (feature != null && feature.startsWith("rss")) {
//			LeRssManager.showFromLauncher();
//		}
//		LeRunningStateManager.clearState();
	}

	private static void clearMultWinState() {
		LeRunningStateManager.clearState();
	}



	public static LeMultWindowsState reStoreState() {
		ObjectInputStream oin = null;// 局部变量必须要初始化
		LeMultWindowsState multWindowsState = null;
		try {
			File file = new File(PATH);
			if (file.exists()) {
				oin = new ObjectInputStream(new FileInputStream(PATH));
				multWindowsState = (LeMultWindowsState) oin.readObject();// 由Object对象向下转型为MyTest对象
				LeLog.e("zyb reStore :" + multWindowsState.toString());
				oin.close();
			}
		} catch (FileNotFoundException e) {
			LeLog.e(e);
		} catch (IOException e) {
			LeLog.e(e);
		} catch (ClassNotFoundException e) {
			LeLog.e(e);
		} catch (Exception e) {
			LeLog.e(e);
		}
		return multWindowsState;
	}


	/**
	 * 恢复后清除所有的状态.
	 */
	public static void clearState() {
		try {
			LeLog.e("zyb clearState!");
			File file = new File(PATH);
			if (file.exists()) {
				file.delete();
			}
		} catch (Exception e) {
			LeLog.e(e);
		}
	}

	public static class LeMultWindowsState implements Serializable {

		private static final long serialVersionUID = 1075549187883016717L;

		public static final long SHOW_DIALOG_TIME = 15 * 1000 * 60;//ms

		private int mCurrentIndex = 0;

		private String mFeaturePath = "";

		private List<LeWindowState> mWindowStates;

		private long mSaveTime = 0;

		public LeMultWindowsState() {
			mWindowStates = new ArrayList<LeWindowState>();
		}
		
		public void setCurrentIndex(int index) {
			mCurrentIndex = index;
		}
		
		public void setCurrentFeaturePath(String path) {
			mFeaturePath = path;
		}
		
		public int getCurrentIndex() {
			return mCurrentIndex;
		}
		
		public String getFeature() {
			return mFeaturePath;
		}
		
		public List<LeWindowState> getWindowStates() {
			return mWindowStates;
		}
		
		public void addState(LeWindowState state) {
			mWindowStates.add(state);
		}
		
		public void setSaveTime(long savetime) {
			mSaveTime = savetime;
		}
		
		public long getSaveTime() {
			return mSaveTime;
		}
		
		public boolean shouldRestore() {
			if (mWindowStates== null || mWindowStates.size() <= 0) {
				return false;
			}
			int len = mWindowStates.size();
			for (int i = 0; i < len; i++) {
				if (mWindowStates.get(i).mUrl != null && !mWindowStates.get(i).mUrl.startsWith(HOME_PRE)) {
					return true;
				}
			}
			
			return false;
		}
				
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("zyb index: ").append(mCurrentIndex)
			  .append("\n");
			sb.append("zyb feture: ").append(mFeaturePath)
			  .append("\n");
			for (int i = 0; i < mWindowStates.size(); i++) {
				sb.append("zyb ").append(mWindowStates.get(i).mUrl).append("\n");
			}
			return sb.toString();
		}
	}
	
	public static class LeWindowState implements Serializable {
		private static final long serialVersionUID = 7848015985714952489L;
		public String mUrl;
		public String mTitle;
		public int mIndex;
		public boolean mIsNewFlag;
	}
}
