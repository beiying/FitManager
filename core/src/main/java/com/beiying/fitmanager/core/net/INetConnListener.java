package com.beiying.fitmanager.core.net;

public interface INetConnListener {

	void onConnStart(LeNetTask task);

	void onReceiveHead(LeNetTask task);

	void onResponseCode(LeNetTask task, int resCode);

	void onReceiveData(LeNetTask task, byte[] data, int len);

	void onDisConnect(LeNetTask task);

	void onThrowException(LeNetTask task);


}
