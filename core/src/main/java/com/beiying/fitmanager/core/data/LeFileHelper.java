package com.beiying.fitmanager.core.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import com.beiying.fitmanager.core.LeCoreException;
import com.beiying.fitmanager.core.LeLog;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

public class LeFileHelper {

	public static final int SIZETYPE_B = 1;//获取文件大小单位为B的double值
	public static final int SIZETYPE_KB = 2;//获取文件大小单位为KB的double值
	public static final int SIZETYPE_MB = 3;//获取文件大小单位为MB的double值
	public static final int SIZETYPE_GB = 4;//获取文件大小单位为GB的double值

	private LeFileHelper() {
	}

	public static byte[] readFile(String path) {
		InputStream is = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			File file = new File(path);
			if (file.exists() && file.length() > 0) {
				is = new FileInputStream(path);
			}
			if (is != null) {
				byte[] buffer = new byte[1024];
				int len;
				while ((len = is.read(buffer)) != -1) {
					dos.write(buffer, 0, len);
				}
				return baos.toByteArray();
			}
		} catch (FileNotFoundException e) {
			LeLog.e(e);
		} catch (Exception e) {
			LeLog.e(e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					LeLog.e(e);
				}
			}
		}
		return null;
	}

    public static String readFileToString(String path){
        byte buffer[] = readFile(path);

        if(buffer == null || buffer.length <= 0){
            return null;
        }

        String result = null;
        try{
            result = new String(buffer, "utf-8");
        }catch (Exception e){
        	LeLog.e(e);
        }

        return result;
    }

	public static boolean saveFile(byte[] data, String path) {
		createDirsIfNotExisted(path);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(path);
			fos.write(data);
			return true;
		} catch (FileNotFoundException e) {
			LeLog.e(e);
		} catch (IOException e) {
			LeLog.e(e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					LeLog.e(e);
				}
			}
		}
		return false;
	}

	public static boolean createDirsIfNotExisted(String path) {
		if (path == null) {
			return false;
		}
		int index = path.lastIndexOf(File.separator);
		if (index != -1) {
			String dir = path.substring(0, index);
			File dirs = new File(dir);
			if (!dirs.exists()) {
				return dirs.mkdirs();
			} else if (!dirs.isDirectory()) {
				throw new LeCoreException("Cache dir is a file not a dir, try change your path.");
			}
		}
		return false;
	}

	public static String rectSeparator(String path) {
		if (path != null) {
			path = path.replace(File.separator + File.separator, File.separator);
		}
		return path;
	}

	public static String joinPath(String head, String tail) {
		if (head == null) {
			return tail;
		}
		if (tail == null) {
			return head;
		}
		String path;
		if (head.endsWith(File.separator)) {
			if (tail.startsWith(File.separator)) {
				path = head + tail.substring(1);
			} else {
				path = head + tail;
			}
		} else {
			if (tail.startsWith(File.separator)) {
				path = head + tail;
			} else {
				path = head + File.separator + tail;
			}
		}
		return path;
	}

	/**
	 * 转换文件大小,指定转换的类型
	 *
	 * @param fileS
	 * @param sizeType
	 * @return
	 */
	public static String formatFileSize(long fileS, int sizeType) {
		DecimalFormat df = new DecimalFormat("#.0");
		double fileSizeLong = 0;
		switch (sizeType) {
			case SIZETYPE_B:
				fileSizeLong = Double.valueOf(df.format((double) fileS));
				break;
			case SIZETYPE_KB:
				fileSizeLong = Double.valueOf(df.format((double) fileS / 1024));
				break;
			case SIZETYPE_MB:
				fileSizeLong = Double.valueOf(df.format((double) fileS / 1048576));
				break;
			case SIZETYPE_GB:
				fileSizeLong = Double.valueOf(df.format((double) fileS / 1073741824));
				break;
			default:
				break;
		}

		String value;
		if (fileSizeLong < 1) {
			value = String.format("%.1f", fileSizeLong);
		} else if (fileSizeLong < 10) {
			value = String.format("%.1f", fileSizeLong);
		} else if (fileSizeLong < 100) {
			value = String.format("%.1f", fileSizeLong);
		} else {
			value = String.format("%.0f", fileSizeLong);
		}
		return value;
	}

	public static String getFromAsset(Context context, String fileName) {
		String result = "";
		try {
			InputStream in = context.getResources().getAssets().open(fileName);
			int length = in.available();
			byte[] buffer = new byte[length];
			in.read(buffer);
			result = new String(buffer, "utf-8");
		} catch (IOException e) {
			LeLog.e(e);
		} catch (Exception e) {
			LeLog.e(e);
		}
		return result;
	}

	public static void deleteFile(File dir) {
		if (dir != null && dir.exists()) {
			if (dir.isDirectory()) {
				File[] files = dir.listFiles();
				if (files.length == 0) {
				} else {
					for (File child : dir.listFiles()) {
						if (child.isDirectory()) {
							deleteFile(child);
						} else {
							child.delete();
						}
					}
				}
			} else {
				dir.delete();
			}
		}
	}

	public static Bitmap readBitmapFile(String aFileName) {
		Bitmap bitmap = null;
		File file = new File(aFileName);
		if (file.isFile()) {
			try {
				FileInputStream fis = new FileInputStream(file);
				bitmap = BitmapFactory.decodeStream(fis);
				fis.close();
			} catch (FileNotFoundException e) {
				LeLog.e("FileNotFoundException");
			} catch (IOException e) {
				LeLog.e("IOException");
			} catch (OutOfMemoryError e) {
				LeLog.e("OutOfMemoryError");
			}
		}
		return bitmap;
	}

	public static boolean isIllegalFilename(String filename) {
		if (filename.contains("/")) {
			return true;
		}
		if (filename.contains("\\")) {
			return true;
		}
		if (filename.contains("?")) {
			return true;
		}
		if (filename.contains("*")) {
			return true;
		}
		if (filename.contains(":")) {
			return true;
		}
		if (filename.equals("<")) {
			return true;
		}
		if (filename.equals(">")) {
			return true;
		}
		if (filename.equals("|")) {
			return true;
		}
        if (filename.equals("\"")) {
            return true;
        }
        if (filename.contains("\"")) {
            return true;
        }
        return false;
	}


	public static boolean appendStringFileToSD(String aFileName, String aContent) {
		try {
			File file = new File(getSdPath() + "//" + aFileName);
			LeLog.e("zyb: " + file.getAbsolutePath());
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file, true);
			fos.write(aContent.getBytes());
			fos.close();
			return true;
		} catch (FileNotFoundException e) {
			LeLog.e(e);
		} catch (IOException e) {
			LeLog.e(e);
		}

		return false;
	}

	public static String getSdPath() {
		boolean hasSD = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

		if (hasSD) {
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		} else {
			return null;
		}
	}

	/** 复制文件 */
	public static boolean copyFile(String aOldPath, String aNewPath) {
		boolean result = false;
		try {
			int byteread = 0;
			File oldfile = new File(aOldPath);
			if (oldfile.exists()) { // 文件存在时  
				InputStream inStream = new FileInputStream(aOldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(aNewPath);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				fs.close();
				result = true;
			}
		} catch (Exception e) {
			LeLog.e(e);
		}
		return result;
	}

	/** 复制文件 */
	public static boolean copyFile(InputStream in, String aNewPath) {
		boolean result = false;
		try {
			int byteread = 0;
			FileOutputStream fs = new FileOutputStream(aNewPath);
			byte[] buffer = new byte[1444];
			while ((byteread = in.read(buffer)) != -1) {
				fs.write(buffer, 0, byteread);
			}
			in.close();
			fs.close();
			result = true;
		} catch (Exception e) {
			LeLog.e(e);
		}
		return result;
	}
}
