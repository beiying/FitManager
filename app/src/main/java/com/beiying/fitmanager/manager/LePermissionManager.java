package com.beiying.fitmanager.manager;

import com.beiying.fitmanager.BYBasicContainer;

public class LePermissionManager extends BYBasicContainer {

//	public static final int REQUEST_CODE = 1000;
//
//	public static final int REQUEST_WRITE_CONTACTS = 1;
//	public static final int REQUEST_READ_CONTACTS = 2;
//	public static final int REQUEST_WRITE_EXTERNAL_STORAGE = 3;
//	public static final int REQUEST_WRITE_MEDIA_STORAGE = 4;
//	public static final int REQUEST_ACCESS_COARSE_LOCATION = 5;
//	public static final int REQUEST_ACCESS_FINE_LOCATION = 6;
//	public static final int REQUEST_READ_PHONE_STATE = 7;
//	public static final int REQUEST_CAMERA = 8;
//	public static final int REQUEST_RECEIVE_SMS = 9;
//	public static final int REQUEST_READ_SMS = 10;
//	public static final int REQUEST_WRITE_SMS = 11;
//	public static final int REQUEST_READ_EXTERNAL_STORAGE = 12;
//	public static final int REQUEST_GET_ACCOUNTS = 13;
//	public static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 100;
//
//
//	private static LePermissionManager sInstance;
//
//	private HashMap<Integer, LePermissionProcessor> mProcessors;
//	private HashMap<Integer, String> mPermissions;
//	private LePermissionProcessor mSettingProcessor = null;
//	private LePermissionManager() {
//		mPermissions = new HashMap<Integer, String>();
//		mProcessors = new HashMap<Integer, LePermissionProcessor>();
//		mPermissions.put(REQUEST_WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//		mPermissions.put(REQUEST_WRITE_MEDIA_STORAGE, Manifest.permission.MEDIA_CONTENT_CONTROL);
//		mPermissions.put(REQUEST_ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
//		mPermissions.put(REQUEST_CAMERA, Manifest.permission.CAMERA);
//		mPermissions.put(REQUEST_READ_PHONE_STATE, Manifest.permission.READ_PHONE_STATE);
//		mPermissions.put(REQUEST_READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
//		mPermissions.put(REQUEST_GET_ACCOUNTS, Manifest.permission.GET_ACCOUNTS);
//	}
//
//	public static LePermissionManager getInstance() {
//		if (sInstance == null) {
//			synchronized (LePermissionManager.class) {
//				sInstance = new LePermissionManager();
//			}
//		}
//		return sInstance;
//	}
//	/**
//	 * 申请一个权限
//	 * @param id
//	 * @param processor
//	 */
//	public void processPermission(int id, LePermissionProcessor processor) {
//		String permission = mPermissions.get(id);
//		mProcessors.put(id, processor);
//		if (!TextUtils.isEmpty(permission)) {
//			int contactPermission = ContextCompat.checkSelfPermission(sActivity, permission);
//			if (contactPermission != PackageManager.PERMISSION_GRANTED) {
////				if (ActivityCompat.shouldShowRequestPermissionRationale(sActivity, permission)) {
////					showRequestPermissionDialog(id, new String[]{permission});
////					return;
////				}
//				ActivityCompat.requestPermissions(sActivity, new String[]{permission}, id);
//				return;
//			}
//			if (processor != null) {
//				processor.doOnGrantedPermission();
//				if (mProcessors != null) {
//					mProcessors.remove(id);
//				}
//			}
//		}
//	}
//	/**
//	 * 申请多个权限
//	 * @param permissions
//	 * @param processor
//	 */
//	public void processMulPermission(List<String> permissions, LePermissionProcessor processor) {
//		boolean isAllGranted = true;
//		List<String> permissionNeeds = new ArrayList<String>();
//		List<String> permissionRequest = new ArrayList<String>();
//		mProcessors.put(REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS, processor);
//		for (int i = 0;i < permissions.size();i++) {
//			String permisson = permissions.get(i);
//			if (ContextCompat.checkSelfPermission(sActivity, permisson) != PackageManager.PERMISSION_GRANTED) {
//				isAllGranted = false;
//				if (!ActivityCompat.shouldShowRequestPermissionRationale(sActivity, permisson)) {
//					permissionNeeds.add(permisson);
//				}
//				permissionRequest.add(permisson);
//			}
//		}
//
//
//		if (permissionRequest.size() > 0) {
//			if (permissionNeeds.size() > 0) {
//				showRequestPermissionDialog(REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS, permissionNeeds.toArray(new String[permissionNeeds.size()]));
//				return;
//			}
//			ActivityCompat.requestPermissions(sActivity, permissionRequest.toArray(new String[permissionRequest.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
//			return;
//		}
//
//		if (isAllGranted) {
//			processor.doOnGrantedPermission();
//			mProcessors.remove(REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
//		}
//
//	}
//
//	public void requestSettingPermission(LePermissionProcessor settingProcessor) {
//		mSettingProcessor = settingProcessor;
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//			if (!Settings.System.canWrite(sActivity)) {
//				Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
//						Uri.parse("package:" + sActivity.getPackageName()));
//				sActivity.startActivityForResult(intent, REQUEST_CODE);
//				return;
//			}
//		}
//		mSettingProcessor.doOnGrantedPermission();
//		mSettingProcessor = null;
//	}
//
//	/**
//	 * 检查是否已被授予该权限
//	 */
//	public boolean checkSelfPermission(int id) {
//		String permission = mPermissions.get(id);
//		if (permission != null) {
//			return ContextCompat.checkSelfPermission(sActivity, permission) == PackageManager.PERMISSION_GRANTED;
//		}
//		return false;
//	}
//
//	private void showRequestPermissionDialog(final int id, final String[] permissions) {
//		String title = sContext.getString(R.string.permission_prompt);
//		String msg = null;
//		switch (id) {
//			case REQUEST_CAMERA:
//				msg = sContext.getString(R.string.need_permission_camera);
//				break;
//			case REQUEST_READ_PHONE_STATE:
//				msg = sContext.getString(R.string.need_permission_phone_state);;
//				break;
//			case REQUEST_WRITE_EXTERNAL_STORAGE:
//				msg = sContext.getString(R.string.need_permission_storage);
//				break;
//			case REQUEST_READ_EXTERNAL_STORAGE:
//				msg = sContext.getString(R.string.need_permission_read_storage);
//				break;
//			case REQUEST_ACCESS_COARSE_LOCATION:
//				msg = sContext.getString(R.string.need_permission_location);
//				break;
//			case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
//				title = sContext.getString(R.string.traffic_tip_title);
//				msg = sContext.getString(R.string.permission_prompt_msg);
//				break;
//			default:
//				break;
//		}
//		final String t = title;
//		final String m = msg;
//		sActivity.runOnUiThread(new Runnable() {
//
//			@Override
//			public void run() {
//				if (id == REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS) {
//					showSystemDialog(id, permissions, t, m);
//				} else {
//					showSimpleDialog(id, permissions, t, m);
//				}
//			}
//		});
//
//	}
//
//	private void showGoSettingDialog(final int id, String title, String msg) {
//		final LeSysDialogContentView dialogContent = new LeSysDialogContentView(sActivity);
//		dialogContent.setMessage(String.format(sContext.getResources().getString(R.string.permission_setting_msg), msg));
//
//		Builder builder = new Builder(sActivity);
//		builder.setNegativeButton(R.string.common_cancel, new OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				android.os.Process.killProcess(android.os.Process.myPid());
//			}
//		});
//
//		builder.setPositiveButton(R.string.permission_go_setting, new OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				Uri packageURI = Uri.parse("package:" + sPackageName);
//				Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,packageURI);
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				sActivity.startActivity(intent);
//			}
//		});
//
//		AlertDialog dialog = builder.create();
//		dialog.setTitle(title);
//		dialog.setView(dialogContent, 0, 0, 0, 0);
//		dialog.setCanceledOnTouchOutside(false);
//		dialog.setOnCancelListener(new OnCancelListener() {
//
//			@Override
//			public void onCancel(DialogInterface dialog) {
//				android.os.Process.killProcess(android.os.Process.myPid());
//			}
//		});
//
//		dialog.show();
//	}
//
//	private void showSimpleDialog(final int id, final String[] permissions,
//								  String title, String msg) {
//		LeSimpleDialog popupDialog = new LeSimpleDialog(sActivity, msg);
//		popupDialog.setOnClickListener(new LeSimpleDialog.OnClickListener() {
//			@Override
//			public void onPositiveClick() {
//				ActivityCompat.requestPermissions(sActivity, permissions, id);
//			}
//		});
//		popupDialog.showWithAnim();
//	}
//
//	private void showSystemDialog(final int id, final String[] permissions,
//								  String title, String msg) {
//		final LeSysDialogContentView dialogContent = new LeSysDialogContentView(sActivity);
//		dialogContent.setMessage(msg);
//
//		Builder builder = new Builder(sActivity);
//		builder.setNegativeButton(R.string.traffic_tip_notallow, new OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				android.os.Process.killProcess(android.os.Process.myPid());
//			}
//		});
//
//		builder.setPositiveButton(R.string.traffic_tip_allow, new OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				ActivityCompat.requestPermissions(sActivity, permissions, id);
//			}
//		});
//
//		AlertDialog dialog = builder.create();
//		dialog.setTitle(title);
//		dialog.setView(dialogContent, 0, 0, 0, 0);
//		dialog.setCanceledOnTouchOutside(false);
//		dialog.setOnCancelListener(new OnCancelListener() {
//
//			@Override
//			public void onCancel(DialogInterface dialog) {
//				android.os.Process.killProcess(android.os.Process.myPid());
//			}
//		});
//
//		dialog.show();
//	}
//
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//			if (Settings.System.canWrite(sActivity)) {
//				if (mSettingProcessor != null) {
//					mSettingProcessor.doOnGrantedPermission();
//				}
//			} else {
//				if (mSettingProcessor != null) {
//					mSettingProcessor.doOnDeniedPermission();
//				}
//			}
//		}
//	}
//	public void onRequestPermissionsResult(int requestCode,
//										   String[] permissions, int[] grantResults) {
//		if (grantResults == null || grantResults.length == 0) {
//			return;
//		}
//		try{
//			switch (requestCode) {
//				case REQUEST_WRITE_CONTACTS:
//					mProcessors.get(REQUEST_WRITE_CONTACTS).doOnGrantedPermission();
//					break;
//				case REQUEST_CAMERA:
//					if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//						mProcessors.get(requestCode).doOnGrantedPermission();
//					} else {
//						LeUtils.showToast(sContext, "相机未授权无法使用二维码扫描");
//					}
//					break;
//				case REQUEST_READ_PHONE_STATE:
//					if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//						mProcessors.get(requestCode).doOnGrantedPermission();
//					} else {
//						LeUtils.showToast(sContext, "读取手机状态的权限未授权");
//					}
//					break;
//				case REQUEST_WRITE_EXTERNAL_STORAGE:
//					if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//						mProcessors.get(requestCode).doOnGrantedPermission();
//					} else {
//						LeUtils.showToast(sContext, "写外置存储的权限未授权");
//					}
//					break;
//				case REQUEST_ACCESS_COARSE_LOCATION:
//					if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//						mProcessors.get(requestCode).doOnGrantedPermission();
//					} else {
////					LeUtils.showToast(sContext, "获取地理位置的权限未授权");
//						mProcessors.get(requestCode).doOnDeniedPermission();
//					}
//					break;
//				case REQUEST_READ_EXTERNAL_STORAGE:
//					if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//						mProcessors.get(requestCode).doOnGrantedPermission();
//					} else {
//						LeUtils.showToast(sContext, "读外置存储的权限未授权");
//					}
//					break;
//				case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
//					if (grantResults.length == 2) {
//						if (grantResults[0] == PackageManager.PERMISSION_GRANTED
//								&& grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//							mProcessors.get(requestCode).doOnGrantedPermission();
//						} else {
//							if (!ActivityCompat.shouldShowRequestPermissionRationale(sActivity, permissions[0]) || !ActivityCompat.shouldShowRequestPermissionRationale(sActivity, permissions[1])) {
//								showGoSettingDialog(REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS, sContext.getString(R.string.permission_prompt), "读取手机状态、地理位置等权限");
//								return;
//							}
//							mProcessors.get(requestCode).doOnDeniedPermission();
//						}
//					}
//					if (grantResults.length == 1) {
//						if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//							mProcessors.get(requestCode).doOnGrantedPermission();
//						} else {
//							if (!ActivityCompat.shouldShowRequestPermissionRationale(sActivity, permissions[0])) {
//								showGoSettingDialog(REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS, sContext.getString(R.string.permission_prompt), "读取手机状态、地理位置等权限");
//								return;
//							}
//							mProcessors.get(requestCode).doOnDeniedPermission();
//						}
//					}
//
//					break;
//				case REQUEST_GET_ACCOUNTS:
//					if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//						mProcessors.get(requestCode).doOnGrantedPermission();
//					} else {
//						mProcessors.get(requestCode).doOnDeniedPermission();
//					}
//					break;
//				default:
//					break;
//			}
//			mProcessors.remove(requestCode);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}
//
//	public static void recycle() {
//		if (sInstance == null) {
//			return;
//		}
//		sInstance.release();
//		sInstance = null;
//	}
//
//	private void release() {
//		if (mPermissions != null) {
//			mPermissions.clear();
//			mPermissions = null;
//		}
//
//		if (mProcessors != null) {
//			mProcessors.clear();
//			mProcessors = null;
//		}
//	}
//
//
//
//	public static abstract class LePermissionProcessor {
//		public void doBeforeRequestPermission() {};
//		public void doAfterRequestPermission() {};
//		public abstract void doOnGrantedPermission();
//		public void doOnDeniedPermission() {};
//	}
}
