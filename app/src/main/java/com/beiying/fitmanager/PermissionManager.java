package com.beiying.fitmanager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import com.beiying.fitmanager.core.utils.LeUtils;
import com.beiying.fitmanager.framework.BYControlCenter;

import java.util.HashMap;


import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

public class PermissionManager extends BYBasicContainer{

	public static final int REQUEST_CODE = 1000;

	public static final int REQUEST_WRITE_CONTACTS = 1;
	public static final int REQUEST_READ_CONTACTS = 2;
	public static final int REQUEST_WRITE_EXTERNAL_STORAGE = 3;
	public static final int REQUEST_WRITE_MEDIA_STORAGE = 4;
	public static final int REQUEST_ACCESS_COARSE_LOCATION = 5;
	public static final int REQUEST_ACCESS_FINE_LOCATION = 6;
	public static final int REQUEST_READ_PHONE_STATE = 7;
	public static final int REQUEST_CAMERA = 8;
	public static final int REQUEST_RECEIVE_SMS = 9;
	public static final int REQUEST_READ_SMS = 10;
	public static final int REQUEST_WRITE_SMS = 11;
	public static final int REQUEST_READ_EXTERNAL_STORAGE = 12;
	public static final int REQUEST_GET_ACCOUNTS = 13;
	public static final int REQUEST_NOTIFICATION_POLICY = 14;
	public static final int REQUEST_RECORD_AUDIO = 15;
	public static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 100;


	private static PermissionManager sInstance;

	private HashMap<Integer, LePermissionProcessor> mProcessors;
	private HashMap<Integer, String> mPermissions;
	private LePermissionProcessor mSettingProcessor = null;
	private PermissionManager() {
		mPermissions = new HashMap<Integer, String>();
		mProcessors = new HashMap<Integer, LePermissionProcessor>();
		mPermissions.put(REQUEST_WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		mPermissions.put(REQUEST_WRITE_MEDIA_STORAGE, Manifest.permission.MEDIA_CONTENT_CONTROL);
		mPermissions.put(REQUEST_ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
		mPermissions.put(REQUEST_ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
		mPermissions.put(REQUEST_CAMERA, Manifest.permission.CAMERA);
		mPermissions.put(REQUEST_READ_PHONE_STATE, Manifest.permission.READ_PHONE_STATE);
		mPermissions.put(REQUEST_READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
		mPermissions.put(REQUEST_GET_ACCOUNTS, Manifest.permission.GET_ACCOUNTS);
		mPermissions.put(REQUEST_NOTIFICATION_POLICY, Manifest.permission.ACCESS_NOTIFICATION_POLICY);
		mPermissions.put(REQUEST_RECORD_AUDIO, Manifest.permission.RECORD_AUDIO);
		mPermissions.put(REQUEST_READ_CONTACTS, Manifest.permission.READ_CONTACTS);
	}

	public static PermissionManager getInstance() {
		if (sInstance == null) {
			synchronized (PermissionManager.class) {
				sInstance = new PermissionManager();
			}
		}
		return sInstance;
	}

	public void processPermission(int id, LePermissionProcessor processor) {
		processPermission(id, processor, null);
	}

	public static boolean checkLocationPermission(Context context) {
		return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED;
	}
	public static boolean checkPermission(Context context, @NonNull String permission) {
		return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
	}

	public static void checkPermissions(Activity activity, String[] permissions) {
		ActivityCompat.requestPermissions(activity, permissions, 1);
	}
	/**
	 * 申请一个权限
	 * @param id
	 * @param processor
	 */
	public void processPermission(int id, LePermissionProcessor processor, Activity activity) {
		String permission = mPermissions.get(id);
		mProcessors.put(id, processor);
		if (!TextUtils.isEmpty(permission)) {
			int uid = Process.myUid();
			int pid = Process.myPid();
			if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION) || permission.equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
				pid = 0;
				uid = 0;
			}
			Activity activityF = sActivity;
			if (activity != null) {
				activityF = activity;
			}
			if (activityF == null) {
				return;
			}
			int contactPermission = activityF.checkPermission(permission, pid, uid);
			if (contactPermission != PackageManager.PERMISSION_GRANTED) {
//				if (ActivityCompat.shouldShowRequestPermissionRationale(sActivity, permission)) {
//					showRequestPermissionDialog(id, new String[]{permission});
//					return;
//				}
				ActivityCompat.requestPermissions(activityF, new String[]{permission}, id);
				return;
			}
			if (processor != null) {
				processor.doOnGrantedPermission();
				if (mProcessors != null) {
					mProcessors.remove(id);
				}
			}
		}
	}

	public void onRequestPermissionsResult(int requestCode,
										   String[] permissions, int[] grantResults) {
		if (grantResults == null || grantResults.length == 0) {
			if (requestCode == REQUEST_READ_PHONE_STATE) {
				mProcessors.get(requestCode).doOnDeniedPermission();
				mProcessors.remove(requestCode);
			}
			return;
		}
		try{
			switch (requestCode) {
				case REQUEST_WRITE_CONTACTS:
					mProcessors.get(REQUEST_WRITE_CONTACTS).doOnGrantedPermission();
					break;
				case REQUEST_CAMERA:
					if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
						mProcessors.get(requestCode).doOnGrantedPermission();
					} else {
						mProcessors.get(requestCode).doOnDeniedPermission();
						LeUtils.showToast(sContext,"相机未授权无法打开相机");
					}
					break;
				case REQUEST_WRITE_EXTERNAL_STORAGE:
					if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
						mProcessors.get(requestCode).doOnGrantedPermission();
					} else {
						LeUtils.showToast(sContext,"写外置存储的权限未授权");
						mProcessors.get(requestCode).doOnDeniedPermission();
					}
					break;
				case REQUEST_ACCESS_COARSE_LOCATION:
					if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
						mProcessors.get(requestCode).doOnGrantedPermission();
					} else {
						mProcessors.get(requestCode).doOnDeniedPermission();
					}
					break;
				case REQUEST_READ_EXTERNAL_STORAGE:
					if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
						mProcessors.get(requestCode).doOnGrantedPermission();
					} else {
						LeUtils.showToast(sContext,"读外置存储的权限未授权");
                        mProcessors.get(requestCode).doOnDeniedPermission();
                    }
					break;
				case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
					if (grantResults.length == 2) {
						if (grantResults[0] == PackageManager.PERMISSION_GRANTED
								&& grantResults[1] == PackageManager.PERMISSION_GRANTED) {
							mProcessors.get(requestCode).doOnGrantedPermission();
						} else {
							if (!ActivityCompat.shouldShowRequestPermissionRationale(sActivity, permissions[0]) || !ActivityCompat.shouldShowRequestPermissionRationale(sActivity, permissions[1])) {
								return;
							}
							mProcessors.get(requestCode).doOnDeniedPermission();
						}
					}
					if (grantResults.length == 1) {
						if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
							mProcessors.get(requestCode).doOnGrantedPermission();
						} else {
							if (!ActivityCompat.shouldShowRequestPermissionRationale(sActivity, permissions[0])) {
								return;
							}
							mProcessors.get(requestCode).doOnDeniedPermission();
						}
					}

					break;
				case REQUEST_GET_ACCOUNTS:
				case REQUEST_RECORD_AUDIO:
				case REQUEST_READ_CONTACTS:
				case REQUEST_READ_PHONE_STATE:
					if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
						mProcessors.get(requestCode).doOnGrantedPermission();
					} else {
						mProcessors.get(requestCode).doOnDeniedPermission();
					}
					break;
				default:
					break;
			}
			mProcessors.remove(requestCode);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void recycle() {
		if (sInstance == null) {
			return;
		}
		sInstance.release();
		sInstance = null;
	}

	private void release() {
		if (mPermissions != null) {
			mPermissions.clear();
			mPermissions = null;
		}

		if (mProcessors != null) {
			mProcessors.clear();
			mProcessors = null;
		}
	}



	public static abstract class LePermissionProcessor {
		public void doBeforeRequestPermission() {};
		public void doAfterRequestPermission() {};
		public abstract void doOnGrantedPermission();
		public void doOnDeniedPermission() {};
	}
}
