package com.beiying.fitmanager.core.net;

import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import com.beiying.fitmanager.core.LeLog;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

class LeNetEngine extends Thread {

	/** DEBUG mode */
	private static final boolean DEBUG = false;
	/** Log TAG */
	private static final String LOG_TAG = "LeNetEngine";

	/** CMWAP Proxy */
	public static final String URI_PROXY_CMWAP = "10.0.0.172";
	/** CTWAP Proxy */
	public static final String URI_PROXY_CTWAP = "10.0.0.200";
	/** Default Proxy Port */
	public static final int DEFAULT_PROXY_PORT = 80;

	private boolean run;

	private boolean isStarted;

	private LeNet net;

	private LeNetTask myTask;

	// 压缩类型
	public static final byte COMPRESS_NONE = 0;

	public static final byte COMPRESS_GZIP = 1;

	public static final byte COMPRESS_DEFLATE = 2;

	@SuppressWarnings("unused")
	private byte compressType;

	private HttpURLConnection urlConn = null;

	private boolean stop;

	public LeNetEngine(LeNet net) {
		this.net = net;

		Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

	}

	public void doConnection() {
		if (myTask == null) {
			LeLog.e("myTask is null");
			return;
		}

		isStarted = true;
		while (!stop) {
			//			Log.v("=============doConnection=======", this+"----:"+myTask);
			long timer = System.currentTimeMillis();
			run = true;
			try {
				if (LeNetStatus.isWap()) {
					//					String[] temp = getHostPath(myTask.getM_url());
					//					String sProxyurl = temp[0];
					//					Proxy proxy = null;
					//					if (Global.WAP_URL.startsWith("10.0.0.200")) {
					//						proxy = new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("10.0.0.200", 80));
					//					} else {
					//						proxy = new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("10.0.0.172", 80));
					//					}
					//					URL url = new URL(myTask.getM_url());
					//					urlConn = (HttpURLConnection) url.openConnection(proxy);
					//					urlConn.setRequestProperty("X-Online-Host", sProxyurl);

					if (LeNetStatus.isCtwap()) { // ctwap // FLYFLOW-2915
						URL url = new URL(myTask.getM_url());
						urlConn = (HttpURLConnection) url.openConnection();
					} else { // cmwap
						URL url = new URL(myTask.getM_url());
						URL cmwapUrl = convertToCmwapUrl(url);
						urlConn = (HttpURLConnection) cmwapUrl.openConnection();
						urlConn.setRequestProperty("X-Online-Host", url.getHost());
					}
				} else {
					URL url = new URL(myTask.getM_url());
					urlConn = (HttpURLConnection) url.openConnection();
				}
				if (myTask.getM_url().toLowerCase().startsWith("https")) {
					((HttpsURLConnection) urlConn).setSSLSocketFactory(getSSLSocketFactory());
				}
				urlConn.setConnectTimeout(myTask.getConnTimeOut());
				urlConn.setReadTimeout(myTask.getReadTimeOut());
				if (myTask.isShutRedirects()) {
					urlConn.setInstanceFollowRedirects(false);
				}
				if (!myTask.isStop())
					myTask.setUrlConn(urlConn);
				// //加入头信息
				addHeads(myTask.getRequestHeads(), urlConn);
				if (myTask.getListener() != null && !myTask.isStop()) {
					myTask.getListener().onConnStart(myTask);
				}
				if (myTask.getNetMode() == LeNet.POST) {
					urlConn.setDoOutput(true);
					urlConn.setDoInput(true);
					urlConn.setUseCaches(false);
					urlConn.setRequestMethod("POST");
					urlConn.setRequestProperty("Content-length", "" + myTask.getBodyLenght());
				}

				if (myTask.getNetMode() == LeNet.HEAD) {
					urlConn.setRequestMethod("HEAD");
				}

				LeLog.v("===================== net start connect =============================:"+myTask.getM_url());
				// //连接
				if (!myTask.isStop()) {
					urlConn.connect();
				}

				if (myTask.getNetMode() == LeNet.POST && !myTask.isStop()) {
					// /建立流
					DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());
					out.write(myTask.getRequestBody(), 0, myTask.getBodyLenght());
					out.flush();
					out.close();
				}

				int rcode = urlConn.getResponseCode();
				if (myTask.getListener() != null && !myTask.isStop()) {
					myTask.getListener().onResponseCode(myTask, rcode);
				}

				LeLog.v("response code=" + rcode);
				switch (rcode) {
					case HttpURLConnection.HTTP_OK:

					case HttpURLConnection.HTTP_PARTIAL:
						boolean isGzip = false;
						String contentEncoding = urlConn.getHeaderField("Content-Encoding");
						if (contentEncoding != null && contentEncoding.indexOf("gzip") >= 0) {
							isGzip = true;
						}
						// /取http头
						checkResponseHeads(urlConn);
						// /打开流
						// ByteArrayOutputStream out = new ByteArrayOutputStream();
						InputStream in = urlConn.getInputStream();
						//					Log.v("---------------isGzip--------------: ", "" + isGzip);
						if (isGzip) {
							GZIPInputStream gzin = new GZIPInputStream(in);
							if (gzin != null) {
								byte[] tmp = new byte[1024 * 4];
								int bytesRead = 0;
								while (((bytesRead = gzin.read(tmp)) != -1)) {
									LeNetStatus.sFlowCount += bytesRead; //统计流量
									if (myTask.getListener() != null && !myTask.isStop())
										myTask.getListener().onReceiveData(myTask, tmp, bytesRead);
									// out.write(tmp, 0, bytesRead);
								}
								gzin.close();
							}
							// FileManager.writeToSdcard(out.toByteArray(),
							// out.toByteArray().length, "111");
							if (in != null)
								in.close();
						} else {
							if (in != null) {
								byte[] tmp = new byte[1024 * 4];
								int bytesRead = 0;

								while (((bytesRead = in.read(tmp)) != -1)) {
									LeNetStatus.sFlowCount += bytesRead; //统计流量
									if (myTask != null && myTask.getListener() != null && !myTask.isStop())
										myTask.getListener().onReceiveData(myTask, tmp, bytesRead);
									// out.write(tmp, 0, bytesRead);
								}
								// byte[] bff = out.toByteArray();
								// if(listener !=
								// null)listener.onReceiveData(bff,bff.length);
								// FileManager.writeToSdcard(out.toByteArray(),
								// out.toByteArray().length, "111");
								in.close();
								// out.close();
							}
						}

						break;
					case HttpURLConnection.HTTP_NOT_FOUND:

						break;
					case HttpURLConnection.HTTP_SERVER_ERROR:

						break;

					case HttpURLConnection.HTTP_UNAVAILABLE:
						if (myTask.getListener() != null)
							myTask.getListener().onThrowException(myTask);
						break;
					case HttpURLConnection.HTTP_NOT_MODIFIED:
						// 取http头
						checkResponseHeads(urlConn);
						break;
					default:
						break;
				}

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				if (myTask.getListener() != null && !myTask.isStop()) {
					myTask.getListener().onThrowException(myTask);
					LeLog.e(e);
				}
			} catch (IOException e) {
				if (myTask.getListener() != null && !myTask.isStop()) {
					myTask.getListener().onThrowException(myTask);
					LeLog.e(e);
				}
			} catch (Exception e) {
				if (myTask.getListener() != null && !myTask.isStop()) {
					myTask.getListener().onThrowException(myTask);
					LeLog.e(e);
				}
			} finally {
				if (urlConn != null && !myTask.isStop()) {
					try {
						urlConn.disconnect();
					} catch (Exception e) {
						LeLog.e(e);
					}
				}
				if (myTask.getListener() != null && !myTask.isStop())// && !myTask.isStop()) 去掉云浏览要求的判断条件
				{
					myTask.getListener().onDisConnect(myTask);
				}
				LeLog.v("================== one task net finish =========================: "+(System.currentTimeMillis() - timer)+" = "+myTask.getM_url());
				myTask = null;
				run = startNext();
				//				Log.v("---------------continue------wait--------: ", "=" + run);
				if (!run) {
					mWait();
				}

			}

		}

	}

	public void mWait() {
		try {
			synchronized (this) {
				int a = 1;
				while (a == 1) {
					this.wait();
					a = 0;
				}
			}
		} catch (InterruptedException e) {
			LeLog.e(e);
		}
	}

	public void mNotify() {
		synchronized(this) {
			int a = 1;
			if (a == 1) {
				this.notify();
			}
		}
	}

	public boolean startNext() {
		LeNetTask task = net.getNetTask();
		if (task != null) {
			myTask = task;
			return true;
		}
		return false;
	}

	public void checkResponseHeads(HttpURLConnection urlConn) {
		if (myTask.getListener() != null && !myTask.isStop()) {
			myTask.getListener().onReceiveHead(myTask);
		}
		//		Log.v("----------------checkResponseHeads----------------: ", "=");
	}

	public void addHeads(Map<String, String> requestHeads, HttpURLConnection urlConn) {
		if (urlConn != null) {
			Iterator<Entry<String, String>> it = requestHeads.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String> entry = it.next();
				//				Log.v("----------------addHeads----------------: ", key + "=" + requestHeads.get(key));
				urlConn.addRequestProperty(entry.getKey(), entry.getValue());
			}
		}
	}

	public void run() {
		doConnection();
	}

	public static String[] getHostPath(String url) {
		String httpHeader = "http://";
		int pos = url.indexOf('/', httpHeader.length());
		if (pos < 0) {
			pos = url.indexOf('?');
		}
		String host = null;
		String subUrl = "/";
		if (pos > 0) {
			host = url.substring(httpHeader.length(), pos);
			if (pos < url.length() - 1) {
				subUrl = url.substring(pos);
			}
		} else {
			host = url.substring(httpHeader.length());
		}
		return new String[] { host, subUrl };
	}

	// ////////////////////////////////

	public HttpURLConnection getUrlConn() {
		return urlConn;
	}

	public void setUrlConn(HttpURLConnection urlConn) {
		this.urlConn = urlConn;
	}

	public LeNetTask getMyTask() {
		return myTask;
	}

	public void setMyTask(LeNetTask myTask) {
		this.myTask = myTask;
	}

	public LeNet getNet() {
		return net;
	}

	public void setNet(LeNet net) {
		this.net = net;
	}

	public boolean isRun() {
		return run;
	}

	public void setRun(boolean run) {
		this.run = run;
	}

	public boolean isStarted() {
		return isStarted;
	}

	public void setStarted(boolean isStarted) {
		this.isStarted = isStarted;
	}

	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}

	// obtain empty trust socket
	private static SSLSocketFactory getSSLSocketFactory() {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[] {};
			}

			public void checkClientTrusted(X509Certificate[] chain, String authType)
					throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] chain, String authType)
					throws CertificateException {
			}
		} };

		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			return sc.getSocketFactory();
		} catch (Exception e) {
			LeLog.e(e);
		}
		return null;
	}

	/**
	 * 转换为Cmwap的URL
	 *
	 * @param url
	 *            待转换的URL
	 * @return 转换后的URL
	 */
	private URL convertToCmwapUrl(URL url) {
		URL cmwapUrl = null;
		try {
			StringBuffer result = new StringBuffer();
			result.append("http://");
			result.append(URI_PROXY_CMWAP);
			String fileAndQuery = url.getFile();
			if (!TextUtils.isEmpty(fileAndQuery)) {
				result.append(fileAndQuery);
			}
			String fragment = url.getRef();
			if (!TextUtils.isEmpty(fragment)) {
				result.append("#");
				result.append(fragment);
			}
			cmwapUrl = new URL(result.toString());
			return cmwapUrl;
		} catch (Exception e) {
			if (DEBUG) {
				Log.w(LOG_TAG, "convertToCmwapUrl Exception", e);
			}
		} finally {
			if (cmwapUrl == null) {
				try {
					cmwapUrl = new URL("http://" + URI_PROXY_CMWAP);
				} catch (Exception e) {
					if (DEBUG) {
						Log.w(LOG_TAG, "convertToCmwapUrl close Exception", e);
					}
					cmwapUrl = url;
				}
			}
		}
		return cmwapUrl;
	}

	public class NullHostNameVerifier implements HostnameVerifier {

		@Override
		public boolean verify(String hostname, SSLSession session) {
			Log.i("RestUtilImpl", "Approving certificate for " + hostname);
			return true;
		}

	}
}
