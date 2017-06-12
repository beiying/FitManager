package com.beiying.fitmanager.manager;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.beiying.fitmanager.core.LeLog;

import java.util.ArrayList;
import java.util.List;

public final class LeEventCenter extends LeBasicManager {
	/**
	 * event id 1000以内为框架的事件 1000以上为各模块事件，每个模块有100个事件空间
	 */
	/** ==================框架===================== */
	public static final int EVENT_ON_PAUSE = 99;
	
	public static final int EVENT_ROOT_ROTATE = 101;
	public static final int EVENT_IME_SHOW = 102;
	public static final int EVENT_IME_HIDE = 103;
	
	public static final int EVENT_DOWLOAD_COMPLETED = 104;
	public static final int EVENT_INSTALLING = 105;
	public static final int EVENT_INSTALLED = 106;
	public static final int EVENT_PACKAGE_REMOVED = 107;
	public static final int EVENT_DOWNLOAD_DELETED = 108;
	public static final int EVENT_APPS_UPDATE = 109;
	public static final int EVENT_INSTALL_FAIL = 110;
	public static final int EVENT_INVOKE_SYSTEM_INSTALL = 111;
	
	public static final int EVENT_ACTIVITY_ROTATE = 112;
	
	//public static final int EVENT_LAYOUT_COMPLETED = 113;
	
	public static final int EVENT_WINDOW_CLOSED = 114;

	public static final int EVENT_NET_CHANGED = 115;
	
	public static final int EVENT_CLICK_HOME_IN_EXPLORE = 116;
	
	public static final int EVENT_TOOLBAR_BUTTONS_LAYOUT = 117;
	
	public static final int EVENT_THEME_CHANGED = 200;

	public static final int EVENT_ADDRESS_ICON_CHANGED = 210;

	public static final int EVENT_SILENT_DOWNLOAD_ON_GOING = 220;

	//	网页护眼色颜色发生变化
	public static final int EVENT_WEB_CONTENT_COLOR_CHANGED = 2000;

	private static LeEventCenter sInstance;

	private List<LeEventNode> mEventList;

	private Handler mHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			syncBroadcastEvent(msg.what, msg.obj);
			super.handleMessage(msg);
		}

	};

	private LeEventCenter() {
		super(true);
		mEventList = new ArrayList<LeEventNode>();
	}

	public static LeEventCenter getInstance() {
		if (sInstance != null && sInstance.reuse()) {
			return sInstance;
		}
		if (sInstance == null) {
			synchronized (LeEventCenter.class) {
				if (sInstance == null) {
					sInstance = new LeEventCenter();
				}
			}
		}
		return sInstance;
	}

	public void init() {

	}
	
	protected boolean onRelease() {
		synchronized (mEventList) {			
			for (LeEventNode node : mEventList) {
				node.release();
			}
		}
		mEventList.clear();
		mEventList = null;
		
		sInstance = null;
		return true;
	}

	public void registerObserver(LeEventObserver eventReciever, int event) {
		synchronized (mEventList) {
			for (LeEventNode node : mEventList) {
				if (node.getEventId() == event) {
					node.addEventReciever(eventReciever);
					return;
				}
			}
			LeEventNode node = new LeEventNode(event);
			node.addEventReciever(eventReciever);
			mEventList.add(node);
		}
	}

	public void unregisterObserver(LeEventObserver observer, int event) {
		synchronized (mEventList) {			
			for (LeEventNode node : mEventList) {
				if (node.getEventId() == event) {
					node.removeEventReciever(observer);
				}
			}
		}
	}

	
	public void broadcastEvent(int eventId) {
		broadcastEvent(eventId, null);
	}

	public void broadcastEventDelayed(int eventId, long delayTime) {
		broadcastEventDelayed(eventId, null, delayTime);
	}

	public void broadcastEvent(int eventId, Object data) {
		Message msg = Message.obtain();
		msg.what = eventId;
		msg.obj = data;
		mHandler.sendMessage(msg);
	}

	public void broadcastEventDelayed(int eventId, Object data, long delayTime) {
		Message msg = Message.obtain();
		msg.what = eventId;
		msg.obj = data;
		mHandler.sendMessageDelayed(msg, delayTime);
	}
	
	public void syncBroadcastEvent(int eventId) {
		syncBroadcastEvent(eventId, null);
	}

	public void syncBroadcastEvent(int eventId, Object data) {
		try {
			LeLog.w("a event is broadcasting:" + eventId);
			for (LeEventNode node : mEventList) {
				if (node.getEventId() == eventId) {
					for (LeEventObserver eventObserver : node.getEventRecieverList()) {
						try {
							if (eventObserver != null) {
								eventObserver.onEventRecieved(eventId, data);
							}
						} catch (Exception e) {
							LeLog.e(e);
						}
					}
					return;
				}
			}
			LeLog.w("no one care about this event:" + eventId);
		} catch (Exception e) {
			LeLog.e(e.getMessage());
		}
	}

	private class LeEventNode {

		private int mEventId;

		private List<LeEventObserver> mRecieverList;

		public LeEventNode(int id) {
			mEventId = id;
			mRecieverList = new ArrayList<LeEventObserver>();
		}
		
		public void release() {
			mRecieverList.clear();
			mRecieverList = null;
		}

		public int getEventId() {
			return mEventId;
		}

		public List<LeEventObserver> getEventRecieverList() {
			return mRecieverList;
		}

		public void addEventReciever(LeEventObserver eventReciever) {
			mRecieverList.add(eventReciever);
		}

		public void removeEventReciever(LeEventObserver eventReciever) {
			mRecieverList.remove(eventReciever);
		}
	}

	public interface LeEventObserver {
		void onEventRecieved(int event, Object data);
	}
}
