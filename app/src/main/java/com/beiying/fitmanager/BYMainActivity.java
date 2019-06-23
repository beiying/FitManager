package com.beiying.fitmanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;

import com.beiying.fitmanager.core.LeLog;
import com.beiying.fitmanager.core.utils.LeUtils;
import com.beiying.fitmanager.framework.BYControlCenter;
import com.beiying.fitmanager.framework.BYFrameworkView;
import com.beiying.plugincore.PluginManager;
import com.beiying.plugincore.ProxyActivity;
import com.taobao.android.dexposed.DexposedBridge;
import com.taobao.android.dexposed.XC_MethodHook;

public class BYMainActivity extends AppCompatActivity {
	
	private BYFrameworkView mFrameworkView;
	private boolean mhasKeyDown = false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		LayoutInflaterCompat.setFactory2(getLayoutInflater(), new LayoutInflater.Factory2() {
			@Override
			public View onCreateView(String name, Context context, AttributeSet attrs) {
				return null;
			}

			@Override
			public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
				return null;
			}
		});
		super.onCreate(savedInstanceState);
		Debug.startMethodTracing("Fitness");
		
		if (isFinishing()) {
			return ;
		}

		new Handler().post(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		BYThreadPoolManager.getService().execute(new Runnable() {
			@Override
			public void run() {

			}
		});


		BYBasicContainer.switchActivity(this);
		PluginManager.getInstance().init(this);
//		mRootView = new BYRootView(this);
		mFrameworkView = new BYFrameworkView(this);
//		mFrameView = LayoutInflater.from(this).inflate(R.layout.main_activity_native, null);
		
//		setContentView(mFrameView);
		mFrameworkView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				LeLog.e("onPreDraw");
				return true;
			}
		});
		setContentView(mFrameworkView);




		setSupportActionBar(mFrameworkView.getMainContentView().getActionToolbar());
		if (getSupportActionBar() != null) {
			getSupportActionBar().setTitle("FitManager");
		}
		
		overridePendingTransition(0, 0);
//		initStep0();
//		new Thread(new Runnable() {
//            @Override
//            public void run() {
//                synchronized (WaMainActivity.this) {
//                    try {
//                        Thread.sleep(10000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//
//
//        synchronized (WaMainActivity.this) {
//            WaLog.e("liuyu", "卡顿啦");
//        }

//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

		Debug.stopMethodTracing();
	}
	
	private void initStep0() {
//		if(mRootView == null){
//			mRootView = new BYRootView(this);
//		}
//		
//		setContentView(mRootView);
		
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		BYLaunchTimer.endRecord("onWindowFocusChanged");
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int action = event.getAction();
		if (action == KeyEvent.ACTION_DOWN) {
			mhasKeyDown = true;
		}
		final boolean shouldHandle = mhasKeyDown;
		if (action == KeyEvent.ACTION_UP) {
			mhasKeyDown = false;
		}
		if (action == KeyEvent.ACTION_UP && shouldHandle && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			onBackPressed();
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public void onBackPressed() {
		if (!BYControlCenter.getInstance().onBackPressed()) {
			showExitDialog();
		}

//		boolean hasFloat = BYControlCenter.getInstance().canFloatViewBack();
//		if (hasFloat) {
//			return;
//		}
//
//		boolean hasCardDialogView = BYControlCenter.getInstance().backCardDialog();
//		if (hasCardDialogView) {
//			return;
//		}
//
//		boolean hasDetailCardView = BYControlCenter.getInstance().backDetailCardView();
//		if (hasDetailCardView) {
//			return;
//		}
//
//		boolean hasWaFeatureView = BYControlCenter.getInstance().backFeatureView();
//		if (hasWaFeatureView) {
//			return;
//		}
//
//		boolean isSpeedViewShow = BYControlCenter.getInstance().backSpeedView();
//		if (isSpeedViewShow) {
//			return;
//		}
//
//		if (!BYControlCenter.getInstance().backFullScreen()) {
//			showExitDialog();
//		}
	}

	private boolean isExiting = false;
	private void showExitDialog() {
		if (isExiting) {
			finish();
			return;
		}
		showExitToast();
	}

	private void showExitToast() {
		LeUtils.showToast(this, "再按你一次退出程序");
		isExiting = true;
//		BYControlCenter.getInstance().postToUiThread(new BYSafeRunnable() {
//			@Override
//			public void runSafely() {
//				isExiting = false;
//			}
//		}, 1500);

		HandlerThread thread = new HandlerThread("test");
		thread.start();
		Handler handler = new Handler(thread.getLooper()) {
			@Override
			public void handleMessage(Message msg) {
				LeLog.e("liuyu", "current thread=" + Thread.currentThread().getName());
			}
		};
		handler.sendMessage(Message.obtain());

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
//		if (mRootView != null) {
//			mRootView.getFrameworkView().setupSelf();
//		}
		if (mFrameworkView != null) {
			mFrameworkView.setupSelf();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		PermissionManager.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	//	private void test(){
//		CommunicationManager cm = CommunicationManager.getInstance();
//		
//		//创建数据库
//		cm.CreateStorage(getApplicationContext());
//		
//		//添加与应用通信的设备，手机设备不需要连接，shimmer设备需要连接
//		cm.addDeviceMobile(getApplicationContext(), "Device Mobile");
//		
//		cm.addDeviceShimmer(getApplicationContext(), "Device Shimmer", true);
//		cm.connect("Device Shimmer", "");
//		
//		ArrayList<SensorType> sensors = new ArrayList<SensorType>();
//		sensors.add(SensorType.ACCELEROMETER);
//		sensors.add(SensorType.MAGNETOMETER);
//		sensors.add(SensorType.GYROSCOPE);
//		
//		cm.setEnabledSensors("Mobile Device", sensors);
//		cm.setEnabledSensors("Device Shimmer", sensors);
//		
//		cm.setNumberOfSampleToStorage("Mobile Device", 2);
//		
//		cm.startStreaming("Mobile Device");
//		
//		
//		RemoteStorageManager rsm = RemoteStorageManager.getInstance();
//		rsm.CreateStorage(getApplicationContext());
//		rsm.setMobileMetadataPath("insert_mobile_metadata.php");
//		rsm.setMobileSignalsPath("insert_mobile_signals.php");
//		rsm.setMobileUnitsPath("insert_mobile_units.php");
//		rsm.setShimmerMetadataPath("insert_shimmer_metadata.php");
//		rsm.setShimmerSignalsPath("insert_shimmer_signals.php");
//		rsm.setShimmerUnitsPath("insert_shimmer_units.php");
//		rsm.setLastIDPath("get_last_ID.php");
//		
//		DataProcessingManager dpm = DataProcessingManager.getInstance();
//		dpm.createAndSetStorage(getApplicationContext());
//		ArrayList<Pair<ArrayList<SensorType>, String>> sensorsAndDevices = new
//		ArrayList<Pair<ArrayList<SensorType>, String>>();
//		ArrayList<SensorType> sensors1 = new ArrayList<SensorType>();
//		ArrayList<SensorType> sensors2 = new ArrayList<SensorType>();
//		sensors1.add(SensorType.ACCELEROMETER_X);
//		sensors1.add(SensorType.ACCELEROMETER_Y);
//		sensors1.add(SensorType.ACCELEROMETER_Z);
////		sensors1.add(SensorType.TIMESTAMP);
//		sensors2.add(SensorType.GYROSCOPE_X);
//		sensors2.add(SensorType.GYROSCOPE_Y);
//		sensors2.add(SensorType.GYROSCOPE_Z);
//		sensors2.add(SensorType.HUMIDITY);
////		sensors2.add(SensorType.TIMESTAMP);
//		String nameDevice1 = "Shimmer CHEST";
//		String nameDevice2 = "Mobile Device";
//		Pair<ArrayList<SensorType>, String> pair1 = new Pair(sensors1, nameDevice1);
//		Pair<ArrayList<SensorType>, String> pair2 = new Pair(sensors2, nameDevice2);
//		sensorsAndDevices.add(pair1);
//		sensorsAndDevices.add(pair2);
//		
//		dpm.readFile(Environment.getExternalStorageDirectory(), "example.arff");
//		dpm.setTrainInstances();
//		dpm.setTestInstances();
//		dpm.getTrainInstancesSummary();
//		
//		
//		VisualizationManager vm = VisualizationManager.getInstance();
//		
//	}
}
