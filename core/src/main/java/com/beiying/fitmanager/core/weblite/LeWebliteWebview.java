package com.beiying.fitmanager.core.weblite;
//package com.lenovo.browser.core.weblite;
//
//import android.content.Context;
//
//import com.lenovo.browser.core.net.LeNetTask;
//import com.lenovo.browser.core.weblite.LeWeblite.LeWebliteListener;
//import com.lenovo.webkit.LeWebView;
//import com.lenovo.browser.tester.T;
//
//public class LeWebliteWebview extends LeWebView {
//	
//	private LeWeblite mWeblite;
//
//	public LeWebliteWebview(Context context, String assertFile, String cacheRoot, String remoteUrl, boolean ourServer,
//			LeExpireTime expireTime) {
//		super(context);
//		
//		setTag(T.EXPLORE_VIEW);
//		
//		initSettings();
//		
////		setWebViewClient(new WebViewClient());
////		setWebChromeClient(new WebChromeClient());
//		
//		mWeblite = new LeWeblite(context, assertFile, cacheRoot, remoteUrl, ourServer, expireTime);
//		mWeblite.setWebliteListener(new LeWebliteListener() {
//			
//			@Override
//			public void setupNetTask(LeNetTask netTask) {
//				
//			}
//			
//			@Override
//			public void onLoadLocalSuccess(String baseUrl, String data, String mimeType, String encoding,
//					String failUrl) {
//				loadDataWithBaseURL(baseUrl, data, mimeType, encoding, failUrl);
//			}
//			
//			@Override
//			public void onLoadFromServerSuccess(String baseUrl, String data, String mimeType, String encoding,
//					String failUrl) {
//				
//			}
//			
//			@Override
//			public void onLoadFromServerFail() {
//				
//			}
//
//			@Override
//			public void onOverrideToRemoteUrl(String remoteUrl) {
//				loadUrl(remoteUrl);
//			}
//		});
//	}
//	
//	public void loadLocal() {
//		mWeblite.loadLocal();
//	}
//	
//	public void fetchRemote(boolean checkExpired) {
//		mWeblite.loadFromServer(checkExpired);
//	}
//	
//	private void initSettings() {
////		WebSettings settings = getSettings();
////		settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
////		settings.setJavaScriptEnabled(true);
////		
////		settings.setLightTouchEnabled(false);
////		settings.setNeedInitialFocus(false);
////
////		settings.setLoadsImagesAutomatically(true); // 是否加载图片
////		settings.setSupportZoom(true);
////		settings.setBuiltInZoomControls(true);
////		// hide zoomcontroler button i.e. +/- buttons on right bottom
////		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
////			settings.setDisplayZoomControls(false);
////		} else {
////			// version <= 2.3:  reflect 'getZoomButtonControlers' as View, and hide it
////			// NOTE do under UI-Thread
////		}
////		settings.setDefaultTextEncodingName("GBK");
////		settings.setLoadWithOverviewMode(true); // 是否缩放到最小
////		settings.setUseWideViewPort(true);
////
////		// 解决地图不加载 // html5
////		settings.setGeolocationEnabled(true);
////		settings.setDatabaseEnabled(true);
////		settings.setDomStorageEnabled(true);
////		settings.setAppCacheEnabled(true);
////
////		settings.setPluginState(PluginState.ON_DEMAND);
////		settings.setSavePassword(true);
////		settings.setSaveFormData(true);
////
////		// 允许打开多窗口
////		settings.setSupportMultipleWindows(true);
//
//	}
//
//}
