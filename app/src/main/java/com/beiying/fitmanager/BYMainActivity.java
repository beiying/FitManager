package com.beiying.fitmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.beiying.fitmanager.framework.BYFrameworkView;

public class BYMainActivity extends AppCompatActivity {
	
	private BYFrameworkView mFrameworkView;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		if (isFinishing()) {
			return ;
		}
		
		BYBasicContainer.switchActivity(this);
//		mRootView = new BYRootView(this);
		mFrameworkView = new BYFrameworkView(this);
//		mFrameView = LayoutInflater.from(this).inflate(R.layout.main_activity_native, null);
		
//		setContentView(mFrameView);
		setContentView(mFrameworkView);
		
		setSupportActionBar(mFrameworkView.getMainContentView().getActionToolbar());
		if (getSupportActionBar() != null) {
			getSupportActionBar().setTitle("FitManager");
		}
		
		overridePendingTransition(0, 0);
//		initStep0();
			
	}
	
	private void initStep0() {
//		if(mRootView == null){
//			mRootView = new BYRootView(this);
//		}
//		
//		setContentView(mRootView);
		
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
