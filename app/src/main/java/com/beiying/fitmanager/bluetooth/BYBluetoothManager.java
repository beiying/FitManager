package com.beiying.fitmanager.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.ScanCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;

import com.beiying.fitmanager.BYBasicContainer;
import com.beiying.fitmanager.core.LeLog;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * 1、建立蓝牙通信服务器端步骤： 1）
 * 
 * 包括对传统蓝牙和BLE的操作
 * 
 * @author beiying
 *
 */
public class BYBluetoothManager extends BYBasicContainer {
	public static final int STATE_DISCONNECTED = 0;
	public static final int STATE_CONNECTING = 1;
	public static final int STATE_CONNECTED = 2;

	public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
	public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
	public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
	public final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
	public final static String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";

	public final static UUID UUID_HEART_RATE_MEASUREMENT = UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);
	/*00001800-0000-1000-8000-00805f9b34fb
	00002a00-0000-1000-8000-00805f9b34fb
	00002a01-0000-1000-8000-00805f9b34fb*/
	
	// Intent request codes
    public static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    public static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    public static final int REQUEST_ENABLE_BT = 3;
    

	private static BYBluetoothManager sInstance = null;
	private static BluetoothAdapter sBluetoothAdapter = null;
	private static BluetoothGatt sBluetoothGatt = null;
	
	private static boolean mIsScanning;
	private static boolean mIsLeScanning;
	private static Set<BluetoothDevice> mBondedBluetoothDevices = null;
	private static List<BluetoothDevice> mScannedBluetoothDevices = null;
	
//	private static BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
//
//		@Override
//		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
//			mBondedBluetoothDevices.add(device);
//		}
//	};

//	private static ScanCallback mScanCallback = new ScanCallback() {
//
//		public void onBatchScanResults(List<ScanResult> results) {
//
//		};
//
//		public void onScanResult(int callbackType, ScanResult result) {
//			if (result != null) {
//				mScannedBluetoothDevices.add(result.getDevice());
//			}
//		};
//
//		public void onScanFailed(int errorCode) {
//
//		};
//	};

	private String mBluetoothDeviceAddress;
	private int mConnectionState = STATE_DISCONNECTED;
	
	public int getConnectionState() {
		return mConnectionState;
	}
	
	private BYBluetoothManager() {
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			sBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		} else {
			final BluetoothManager bluetoothManager = (BluetoothManager) sContext
					.getSystemService(Context.BLUETOOTH_SERVICE);
			sBluetoothAdapter = bluetoothManager.getAdapter();
		}

	}

	// Various callback methods defined by the BLE API.连接和读取低功耗蓝牙时的回调
	private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			String intentAction;
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				intentAction = ACTION_GATT_CONNECTED;
				mConnectionState = STATE_CONNECTED;
				broadcastUpdate(intentAction);
				LeLog.e("Connected to GATT server.");
				LeLog.e("Attempting to start service discovery:" + sBluetoothGatt.discoverServices());

			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				intentAction = ACTION_GATT_DISCONNECTED;
				mConnectionState = STATE_DISCONNECTED;
				LeLog.e("Disconnected from GATT server.");
				broadcastUpdate(intentAction);
			}
		}

		@Override
		// New services discovered
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
			} else {
				LeLog.e("onServicesDiscovered received: " + status);
			}
		}

		@Override
		// Result of a characteristic read operation 读取到低功耗蓝牙的数据时会回调该函数
		public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic,
				int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
			}
		}
	};

	private void broadcastUpdate(final String action) {
		final Intent intent = new Intent(action);
		sContext.sendBroadcast(intent);
	}

	private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
		final Intent intent = new Intent(action);

		// This is special handling for the Heart Rate Measurement profile. Data
		// parsing is carried out as per profile specifications.
		if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
			int flag = characteristic.getProperties();
			int format = -1;
			if ((flag & 0x01) != 0) {
				format = BluetoothGattCharacteristic.FORMAT_UINT16;
				LeLog.e("Heart rate format UINT16.");
			} else {
				format = BluetoothGattCharacteristic.FORMAT_UINT8;
				LeLog.e("Heart rate format UINT8.");
			}
			final int heartRate = characteristic.getIntValue(format, 1);
			LeLog.e(String.format("Received heart rate: %d", heartRate));
			intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
		} else {
			// For all other profiles, writes the data formatted in HEX.
			final byte[] data = characteristic.getValue();
			if (data != null && data.length > 0) {
				final StringBuilder stringBuilder = new StringBuilder(data.length);
				for (byte byteChar : data)
					stringBuilder.append(String.format("%02X ", byteChar));
				intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
			}
		}
		sContext.sendBroadcast(intent);
	}
	

	public static BYBluetoothManager getInstance() {
		if (sInstance == null) {
			sInstance = new BYBluetoothManager();
		}
		return sInstance;
	}

	/**
	 * 手机是否支持蓝牙功能
	 * 
	 * @return
	 */
	public boolean isSupportBluetooth() {
		return sBluetoothAdapter != null;
	}

	/**
	 * 蓝牙功能是否打开
	 * 
	 * @return
	 */
	public boolean isBluetoothEnabled() {
		if (sBluetoothAdapter != null) {
			return sBluetoothAdapter.isEnabled();
		}
		return false;
	}

	public BluetoothAdapter getBluetoothAdapter() {
		return sBluetoothAdapter;
	}
	
	public void requestEnableBluetooth() {
		Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        sActivity.startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
	}

	/**
	 * 使本机蓝牙处于可见
	 */
	public void ensureDiscoverable() {
		if (sBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			mIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			sActivity.startActivity(mIntent);
		}
	}

	/**
	 * 查询配对设备
	 * 
	 * @return
	 */
	public Set<BluetoothDevice> getBluetoothDevices() {
		mBondedBluetoothDevices = sBluetoothAdapter.getBondedDevices();
		return mBondedBluetoothDevices;
	}

	public boolean isDevicesExist() {
		mBondedBluetoothDevices = getBluetoothDevices();
		if (mBondedBluetoothDevices != null && mBondedBluetoothDevices.size() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 注册搜索设备的广播接收器
	 */
	public void registerFoundBroadcastReceiver() {
		BroadcastReceiver foundReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					mBondedBluetoothDevices.add(device);
				}
			}
		};

		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		sContext.registerReceiver(foundReceiver, filter);
	}

	/**
	 * 搜素BLE设备
	 * 
	 * @param enable
	 */
	public void scanLeDevice(final boolean enable, ScanCallback scanCallback) {
		if (enable) {
			Handler mHandler = new Handler();
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					LeLog.e("LY stopScan");
					mIsLeScanning = false;
					LeLog.e("BYBluetoothManager stopScan");
					sBluetoothAdapter.getBluetoothLeScanner().stopScan(null);
				}
			}, 30000);
			mIsLeScanning = true;
			sBluetoothAdapter.getBluetoothLeScanner().startScan(scanCallback);
			LeLog.e("BYBluetoothManager startScan");
		}
	}
	
	public void scanDevice() {
		sBluetoothAdapter.startDiscovery();
	}

	/**
	 * 连接低功耗蓝牙
	 * Connects to the GATT server hosted on the Bluetooth LE device.
	 *
	 * @param address
	 *            The device address of the destination device.
	 *
	 * @return Return true if the connection is initiated successfully. The
	 *         connection result is reported asynchronously through the
	 *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 *         callback.
	 */
	public boolean connectGatt(final String address) {
		if (sBluetoothAdapter == null || address == null) {
			LeLog.w("BluetoothAdapter not initialized or unspecified address.");
			return false;
		}

		// Previously connected device.  Try to reconnect.
		if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
				&& sBluetoothGatt != null) {
			LeLog.d("Trying to use an existing mBluetoothGatt for connection.");
			if (sBluetoothGatt.connect()) {
				mConnectionState = STATE_CONNECTING;
				return true;
			} else {
				return false;
			}
		}

		final BluetoothDevice device = sBluetoothAdapter.getRemoteDevice(address);
		if (device == null) {
			LeLog.w("Device not found.  Unable to connect.");
			return false;
		}
		// We want to directly connect to the device, so we are setting the autoConnect
		// parameter to false.
		sContext.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		sBluetoothGatt = device.connectGatt(sContext, false, mGattCallback);
		LeLog.d("Trying to create a new connection.");
		
		mBluetoothDeviceAddress = address;
		mConnectionState = STATE_CONNECTING;
		return true;
	}

	/**
	 * 断开低功耗蓝牙连接
	 * Disconnects an existing connection or cancel a pending connection. The
	 * disconnection result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 * callback.
	 */
	public void disconnect() {
		if (sBluetoothAdapter == null || sBluetoothGatt == null) {
			LeLog.w("BluetoothAdapter not initialized");
			return;
		}
		sBluetoothGatt.disconnect();
	}

	/**
	 * 释放低功耗蓝牙
	 * After using a given BLE device, the app must call this method to ensure
	 * resources are released properly.
	 */
	public void close() {
		if (sBluetoothGatt == null) {
			return;
		}
		sBluetoothGatt.close();
		sBluetoothGatt = null;
	}

	/**
	 * 从低功耗蓝牙端读取数据
	 * Request a read on a given {@code BluetoothGattCharacteristic}. The read
	 * result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
	 * callback.
	 *
	 * @param characteristic
	 *            The characteristic to read from.
	 */
	public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (sBluetoothAdapter == null || sBluetoothGatt == null) {
			LeLog.w("BluetoothAdapter not initialized");
			return;
		}
		sBluetoothGatt.readCharacteristic(characteristic);
	}

	/**
	 * Enables or disables notification on a give characteristic.
	 *
	 * @param characteristic
	 *            Characteristic to act on.
	 * @param enabled
	 *            If true, enable notification. False otherwise.
	 */
	public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
		if (sBluetoothAdapter == null || sBluetoothGatt == null) {
			//            Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		sBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

		// This is specific to Heart Rate Measurement.
		//        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
		//            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
		//                    UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
		//            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
		//            sBluetoothGatt.writeDescriptor(descriptor);
		//        }
	}

	/**
	 * 获取低功耗蓝牙端支持的Service
	 * Retrieves a list of supported GATT services on the connected device. This
	 * should be invoked only after {@code BluetoothGatt#discoverServices()}
	 * completes successfully.
	 *
	 * @return A {@code List} of supported services.
	 */
	public List<BluetoothGattService> getSupportedGattServices() {
		if (sBluetoothGatt == null)
			return null;

		return sBluetoothGatt.getServices();
	}

	/**
	 * 请求允许被搜索
	 */
	public Intent requestDiscoverable() {
		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
		return discoverableIntent;
	}

	/**
	 * 作为服务器端建立连接
	 * 
	 * @param name
	 * @param uuid
	 * @param socketManager
	 */
	public void getBluetoothSocketAsServer(final String name, final UUID uuid,
			final ConnectedSocketManager socketManager) {
		HandlerThread thread = new HandlerThread("bluetooth_server");
		thread.start();
		int a = BluetoothClass.Device.PHONE_ISDN;
		sBluetoothAdapter.getProfileProxy(sContext, new ServiceListener() {

			@Override
			public void onServiceDisconnected(int profile) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onServiceConnected(int profile, BluetoothProfile proxy) {
				// TODO Auto-generated method stub

			}
		}, BluetoothProfile.GATT);
		new Handler(thread.getLooper()).post(new Runnable() {

			@Override
			public void run() {
				BluetoothServerSocket serverSocket = null;
				BluetoothSocket socket = null;
				try {
					serverSocket = sBluetoothAdapter.listenUsingRfcommWithServiceRecord(name, uuid);
				} catch (IOException e) {
					e.printStackTrace();
				}
				while (true) {
					try {
						socket = serverSocket.accept();
						if (socket != null) {
							socketManager.manageConnectedSocket(socket);//管理连接
							serverSocket.close();//在处理完侦听到的连接后立即关闭BluetoothServerSocket，需要在线程中提供一个公共的方法来关闭私有的BluetoothSocket，停止服务端socket的侦听
							break;
						}
					} catch (IOException e) {
						e.printStackTrace();
						break;
					}

				}
			}
		});
	}

	/**
	 * 作为客户端建立连接
	 * 
	 * @param device
	 * @param uuid
	 * @param socketManager
	 */
	public void getBluetoothSocketAsClient(final BluetoothDevice device, final UUID uuid,
			final ConnectedSocketManager socketManager) {
		HandlerThread thread = new HandlerThread("bluetooth_server");
		thread.start();
		new Handler(thread.getLooper()).post(new Runnable() {

			@Override
			public void run() {
				BluetoothSocket socket = null;
				sBluetoothAdapter.cancelDiscovery();
				try {
					socket = device.createRfcommSocketToServiceRecord(uuid);
					socket.connect();
					socketManager.manageConnectedSocket(socket);
				} catch (IOException e) {
					if (socket != null) {
						try {
							socket.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					e.printStackTrace();
				}
			}

		});

	}

	private class ConnectedThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;

		public ConnectedThread(BluetoothSocket socket) {
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;
			// Get the input and output streams, using temp objects because //
			// member streams are final
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {

			}
			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public void run() {
			byte[] buffer = new byte[1024]; // buffer store for the stream
			int bytes; // bytes returned from read()
			// Keep listening to the InputStream until an exception occurs while
			// (true) {
			try {
				// Read from the InputStream
				bytes = mmInStream.read(buffer);
				// Send the obtained bytes to the UI Activity
				// mHandler.obtainMessage(MESSAGE_READ, bytes, -1,
				// buffer).sendToTarget();
			} catch (IOException e) {
			}
		}

		/* Call this from the main Activity to send data to the remote device */
		public void write(byte[] bytes) {
			try {
				mmOutStream.write(bytes);
			} catch (IOException e) {

			}
		}

		/* Call this from the main Activity to shutdown the connection */
		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {

			}
		}

	}
	private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

	// Handles various events fired by the Service.
	// ACTION_GATT_CONNECTED: connected to a GATT server.
	// ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
	// ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
	// ACTION_DATA_AVAILABLE: received data from the device. This can be a
	// result of read or notification operations.
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (ACTION_GATT_CONNECTED.equals(action)) {
				//	            mConnected = true;
				//	            updateConnectionState(R.string.connected);
				//	            invalidateOptionsMenu();
			} else if (ACTION_GATT_DISCONNECTED.equals(action)) {
				//	            mConnected = false;
				//	            updateConnectionState(R.string.disconnected);
				//	            invalidateOptionsMenu();
				//	            clearUI();
			} else if (ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
				// Show all the supported services and characteristics on the
				// user interface.
				//	            displayGattServices(mBluetoothLeService.getSupportedGattServices());
				String uuid = null;
				ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
		        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();
		        ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
				List<BluetoothGattService> services = sBluetoothGatt.getServices();
				for (BluetoothGattService service : services) {
					HashMap<String, String> currentServiceData = new HashMap<String, String>();
		            uuid = service.getUuid().toString();
		            currentServiceData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, "unknown service"));
		            currentServiceData.put(LIST_UUID, uuid);
		            gattServiceData.add(currentServiceData);

		            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
		                    new ArrayList<HashMap<String, String>>();
		            List<BluetoothGattCharacteristic> gattCharacteristics =
		            		service.getCharacteristics();
		            ArrayList<BluetoothGattCharacteristic> charas =
		                    new ArrayList<BluetoothGattCharacteristic>();

		            // Loops through available Characteristics.
		            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
		                charas.add(gattCharacteristic);
		                HashMap<String, String> currentCharaData = new HashMap<String, String>();
		                uuid = gattCharacteristic.getUuid().toString();
		                currentCharaData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, "unknown service"));
		                currentCharaData.put(LIST_UUID, uuid);
		                gattCharacteristicGroupData.add(currentCharaData);
		            }
		            mGattCharacteristics.add(charas);
		            gattCharacteristicData.add(gattCharacteristicGroupData);
				}
			} else if (ACTION_DATA_AVAILABLE.equals(action)) {
				//	            displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
			}
		}
	};
	
	private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_GATT_CONNECTED);
        intentFilter.addAction(ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

	/**
	 * 蓝牙设备建立连接后处理函数
	 * 
	 * @author beiying
	 *
	 */
	public interface ConnectedSocketManager {
		public void manageConnectedSocket(BluetoothSocket socket);
	}
}
