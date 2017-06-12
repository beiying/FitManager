package com.beiying.fitmanager.core.net;


import com.beiying.fitmanager.core.LeLog;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class LeNetTask {
	
	private static final String DEBUG_TAG = "LeNetTask";

	//联网方式
	private byte netMode = LeNet.GET;

	// 联接网址
	private String m_url;

	//是否关闭重定向
	private boolean isShutRedirects;

	//侦听
	private INetConnListener listener;

	// //发送头
	private Map<String, String> requestHeads;

	// /post的数据体
	private byte[] requestBody;

	//数据长度
	private int bodyLenght;

	private HttpURLConnection urlConn = null;

	private boolean isStop;

	private int readTimeOut = 25 * 1000;

	private int connTimeOut;
	
	private Object mSetting;

	public LeNetTask() {
		requestHeads = new HashMap<String, String>();
	}
	
	public void configurePostMode() {
		setNetMode(LeNet.POST);
		addHttpHeads("Connection", "Keep-Alive");
		addHttpHeads("Content-Type", "application/x-www-form-urlencoded");
	}

	public void addHttpHeads(String field, String value) {
		requestHeads.put(field, value);
		LeLog.v(field + "=====" + value);
	}

	// //停止
	public void stopConn() {
		isStop = true;
		if (urlConn != null) {
			urlConn.disconnect();
			urlConn = null;
		}
	}

	public String getM_url() {
		return m_url;
	}

	public void setM_url(String url) {
		this.m_url = url;
	}

	public INetConnListener getListener() {
		return listener;
	}

	public void setListener(INetConnListener listener) {
		this.listener = listener;
	}

	public Map<String, String> getRequestHeads() {
		return requestHeads;
	}

	public void setRequestHeads(Map<String, String> requestHeads) {
		this.requestHeads = requestHeads;
	}

	public byte[] getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(byte[] requestBody) {
		this.requestBody = requestBody;
	}

	public int getBodyLenght() {
		return bodyLenght;
	}

	public void setBodyLenght(int bodyLenght) {
		this.bodyLenght = bodyLenght;
	}

	public byte getNetMode() {
		return netMode;
	}

	public void setNetMode(byte netMode) {
		this.netMode = netMode;
	}

	public HttpURLConnection getUrlConn() {
		return urlConn;
	}

	public void setUrlConn(HttpURLConnection urlConn) {
		this.urlConn = urlConn;
	}

	public boolean isStop() {
		return isStop;
	}

	public void setStop(boolean isStop) {
		this.isStop = isStop;
	}

	public boolean isShutRedirects() {
		return isShutRedirects;
	}

	public void setShutRedirects(boolean isShutRedirects) {
		this.isShutRedirects = isShutRedirects;
	}

	public int getReadTimeOut() {
		return readTimeOut;
	}

	// 设置超时
	public void setReadTimeOut(int readTimeOut) {
		this.readTimeOut = readTimeOut;
	}

	/**
	 * @return the connTimeOut
	 */
	public int getConnTimeOut() {
		return connTimeOut;
	}

	/**
	 * @param connTimeOut
	 *            the connTimeOut to set
	 */
	public void setConnTimeOut(int connTimeOut) {
		this.connTimeOut = connTimeOut;
	}

	public Object getSetting() {
		return mSetting;
	}

	public void setSetting(Object setting) {
		this.mSetting = setting;
	}
	
	public void dump(String tag, String invoker) {
		LeLog.i(tag, invoker);
		LeLog.i(tag, m_url);
		if (requestHeads != null) {
			Set<Entry<String, String>> set = requestHeads.entrySet();
			for (Entry<String, String> entry : set) {
				String key = entry.getKey();
				String value = entry.getValue();
				LeLog.i(tag, key + ":" + value);
			}
		}
		if (requestBody != null) {
			LeLog.i(tag, new String(requestBody));
		}
	}

}
