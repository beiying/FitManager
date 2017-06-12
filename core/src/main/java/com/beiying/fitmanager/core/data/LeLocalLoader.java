package com.beiying.fitmanager.core.data;

import com.beiying.fitmanager.core.ContextContainer;
import com.beiying.fitmanager.core.LeLog;
import com.beiying.fitmanager.core.LeThreadCore;
import com.beiying.fitmanager.core.LeThreadTask;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class LeLocalLoader {
	private static final boolean DEBUG = false;
	
	private static final int K_LENGTH = 1024;

	protected String mCachePath;
	protected String mAssertFile;
	
	protected boolean mCacheLoaded;

	public LeLocalLoader(String cachePath, String assertFile) {
		mCachePath = cachePath;
		mAssertFile = assertFile;

		mCacheLoaded = false;
	}
	
	public void load() {
		load(LeThreadTask.PRIORITY_DEFAULT);
	}
	
	/**
	 * 
	 * @param priority see {@link LeThreadTask#LeThreadTask(int)}
	 */
	public void load(int priority) {
		LeThreadCore.getInstance().runAsDefaultPriority(new LeThreadTask(priority) {
			
			@Override
			public void runSafely() {
				realLoad();
			}
		});
	}

	private void realLoad() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		
		InputStream is = null;
		boolean isFromAsset = false;
		try {
			File file = new File(mCachePath);
			if (file.exists() && file.length() > 0) {
				is = new FileInputStream(mCachePath);
			} else if (mAssertFile != null) {
				is = ContextContainer.sContext.getAssets().open(mAssertFile);
				isFromAsset = true;
			}
			if (is != null) {
				byte[] buffer = new byte[K_LENGTH];
				int len;
				while ((len = is.read(buffer)) != -1) {
					dos.write(buffer, 0, len);
				}
				mCacheLoaded = true;
				onCacheLoaded(new String(baos.toByteArray(), "utf-8"), isFromAsset);
				if (DEBUG) {
					LeLog.i("httptask debug load:" + mCachePath);
				}
			}
		} catch (FileNotFoundException e) {
			LeLog.e(e);
		} catch (Exception e) {
			LeLog.e(e);
		} finally {
			if (!mCacheLoaded) {
				onLoadFail();
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					LeLog.e(e);
				}
			}
			try {
				dos.close();
				baos.close();
			} catch (IOException e) {
				
			}
		}
	}
	
	public void saveCache(String data) {
		if (data == null) {
			return ;
		}
		saveCache(data.getBytes());
	}
	
	public void saveCache(byte[] data) {
		saveCache(data, false);
	}
	
	public void saveCache(byte[] data, boolean append) {
        if (mCachePath != null) {
            createDirsIfNotExisted(mCachePath);
            try {
                FileOutputStream fos = new FileOutputStream(mCachePath, append);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                LeLog.e(e.toString());
            } catch (IOException e) {
                LeLog.e(e.toString());
            }
        }
	}
	
	private boolean createDirsIfNotExisted(String path) {
		if (path == null) {
			return false;
		}
		int index = path.lastIndexOf(File.separator);
		if (index != -1) {
			String dir = path.substring(0, index);
			File dirs = new File(dir);
			if (!dirs.exists()) {
				return dirs.mkdirs();
			}
		}
		return false;
	}
	
	public boolean isLoadFromCache() {
		return mCacheLoaded;
	}

	protected void onCacheLoaded(String data, boolean assetFile) {

	}

	protected void onLoadFail() {
		
	}
}
