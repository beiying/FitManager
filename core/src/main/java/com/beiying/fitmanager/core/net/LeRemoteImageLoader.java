package com.beiying.fitmanager.core.net;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.beiying.fitmanager.core.LeLog;
import com.beiying.fitmanager.core.LeThreadCore;
import com.beiying.fitmanager.core.LeThreadTask;
import com.beiying.fitmanager.core.data.LeFileHelper;
import com.beiying.fitmanager.core.utils.LeUriUtils;
import com.beiying.fitmanager.core.utils.LeUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class LeRemoteImageLoader {
	private static final String PNG_APPENDIX = ".png";
	
	private String mCacheDirPath;
	private String mFileName;
	private String mRemoteUrl;

	private LeRemoteImageLoaderListener mListener;

	private Context mContext;
	
	private boolean mCheckNetwork;

	public LeRemoteImageLoader(Context context, String cacheDirPath,
							   String filename, String remoteUrl, boolean checkNetwork) {
		mContext = context;

		mCacheDirPath = cacheDirPath;
		mFileName = filename;
		mRemoteUrl = remoteUrl;
		mCheckNetwork = checkNetwork;

		if (mCacheDirPath != null) {
			LeFileHelper.createDirsIfNotExisted(LeUriUtils.combinePath(mCacheDirPath, mFileName));
		}

	}

	public boolean clearResourcesIfNecessary(String oldFilePath, String oldRemoteUrl, String newRemoteUrl) {
		if (oldFilePath == null) {
			return false;
		}
		if (oldRemoteUrl == null && newRemoteUrl == null) {
			return false;
		}
		if (oldRemoteUrl != null && oldRemoteUrl.equals(newRemoteUrl)) {
			return false;
		}
		LeFileHelper.deleteFile(new File(oldFilePath));
		return true;
		
//		if ((oldRemoteUrl == null && newRemoteUrl != null)
//				|| (oldRemoteUrl != null && (newRemoteUrl == null || !newRemoteUrl.equals(oldRemoteUrl)))) {
//			LeFileUtils.deleteFile(new File(oldFilePath));
//			return true;
//		}
//		return false;
	}

	public void loadRemoteImage() {
		loadRemoteImage(false, LeThreadTask.PRIORITY_DEFAULT);
	}
	
	public void loadRemoteImage(int priority) {
		loadRemoteImage(false, priority);
	}

	public void loadRemoteImage(final boolean cacheOnly, int priority) {
		LeThreadCore.getInstance().runAsDefaultPriority(new LeThreadTask(priority) {

			@Override
			public void runSafely() {
				Bitmap bitmap;
				if (mCacheDirPath != null) {
					String filename = mCacheDirPath + "/" + mFileName;

					bitmap = LeFileHelper.readBitmapFile(filename);
					if (bitmap != null) { // 已有缓存图
						LeLog.i("already has cache");
						if (mListener != null) {
							mListener.onCacheLoaded(bitmap);
						}
						return;
					}

					if (cacheOnly) {
						return;
					}
				}
				if (mCheckNetwork && LeNetStatus.is2G()) {
					return;
				}

				InputStream stream = null;
				try {
					LeLog.i("cw user remote portait:" + mRemoteUrl);
					stream = new URL(mRemoteUrl).openStream();
					bitmap = BitmapFactory.decodeStream(stream);

					if (bitmap != null) {
						if (mListener != null) {
							mListener.onRemoteLoaded(bitmap);
						}
					}
					saveImage(bitmap);

				} catch (Exception e) {
					if (mListener != null) {
						mListener.onException((byte) 1);
					}
					LeLog.e(e.getMessage());
				} catch (Error e) {
					if (mListener != null) {
						mListener.onException((byte) 1);
					}
					LeLog.e(e.getMessage());
				} finally {
					if (stream != null) {
						try {
							stream.close();
						} catch (IOException e) {
							LeLog.e(e);
						}
					}
				}
			}
		});
	}

	public void loadOnlyRemoteImage() {
		LeThreadCore.getInstance().runAsDefaultPriority(new LeThreadTask( LeThreadTask.PRIORITY_DEFAULT) {

			@Override
			public void runSafely() {
				Bitmap bitmap;
				if (mCacheDirPath != null) {
					String filename = mCacheDirPath + "/" + mFileName;
					File file = new File(filename);
					if (file.exists()) {
						LeLog.e("zyb has exists~~~~~~~~~~~");
						return;
					}
				}
				if (mCheckNetwork && LeNetStatus.is2G()) {
					return ;
				}

				InputStream stream = null;
				try {
					LeLog.i("cw user remote portait:" + mRemoteUrl);
					stream = new URL(mRemoteUrl).openStream();
					bitmap = BitmapFactory.decodeStream(stream);

					if (bitmap != null) {
						if (mListener != null) {
							mListener.onRemoteLoaded(bitmap);
						}
					}
					saveImage(bitmap);

				} catch (Exception e) {
					if (mListener != null) {
						mListener.onException((byte) 1);
					}
					LeLog.e(e.getMessage());
				} catch (Error e) {
					if (mListener != null) {
						mListener.onException((byte) 1);
					}
					LeLog.e(e.getMessage());
				} finally {
					if (stream != null) {
						try {
							stream.close();
						} catch (IOException e) {
							LeLog.e(e);
						}
					}
				}
			}
		});
	}

	private void saveImage(Bitmap bitmap) {
		String filename = null;
		if (mCacheDirPath != null) {
			filename = mCacheDirPath + "/" + mFileName;
	
			LeFileHelper.deleteFile(new File(filename));
	
			if (filename.toLowerCase().endsWith(PNG_APPENDIX)) {
				LeUtils.savePNGBitmap(bitmap, filename);
			} else {
				LeUtils.saveJPEGBitmap(bitmap, filename);
			}
		}
		if (mListener != null) {
			mListener.onCacheSaved(filename, bitmap);
		}
	}
	
	public void deleteImage() {
		if (mCacheDirPath != null) {
			String filename = mCacheDirPath + "/" + mFileName;
	
			LeFileHelper.deleteFile(new File(filename));
		}
	}

	public void setListener(LeRemoteImageLoaderListener listener) {
		mListener = listener;
	}

	public String getFilePath() {
		if (mCacheDirPath == null || mFileName == null) {
			return null;
		}
		return mCacheDirPath + "/" + mFileName;
	}

	public String getFileName() {
		return mFileName;
	}

	public String getRemoteUrl() {
		return mRemoteUrl;
	}
	
	public void setCheckNetwork(boolean check) {
		mCheckNetwork = check;
	}

	public interface LeRemoteImageLoaderListener {
		void onCacheLoaded(Bitmap bitmap);

		void onRemoteLoaded(Bitmap bitmap);

		void onException(byte aErrorCode);
		
		void onCacheSaved(String filePath, Bitmap bitmap);
	}
}
