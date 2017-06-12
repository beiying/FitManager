package com.beiying.fitmanager.core.utils;

import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 内存信息读取工具
 */
public final class LeMemUtil {

	/** DEBUG mode */
	private static final boolean DEBUG = false;
	/** Log TAG */
	private static final String LOG_TAG = "LeMemUtil";

	/** 获取系统内存的属性集 */
	public static final String[] FIELDS_SYS_MEMINFO = { "MemTotal:", "MemFree:", "Buffers:", "Cached:",
			"Active:", "Inactive:", "Dirty:" };
	/** 获取当前进程内存的属性集 */
	public static final String[] FIELDS_PROC_MEMINFO = { "VmLck:", "VmRSS:", "VmSize:", "VmExe:", "VmStk:",
			"VmLib", "Threads:" };

	/**
	 * 该函数通过反射读取/proc/meminfo 目前返回参数列表如下,如果需要增加，只需要在 FIELDS_SYS_MEMINFO中增加相关列即可
	 * 
	 * @return 返回一个Map 格式如下 MemTotal: long 所有可用RAM大小 MemFree: long
	 *         LowFree与HighFree的总和，被系统留着未使用的内存 Buffers: long 用来给文件做缓冲大小 Cached:
	 *         long 被高速缓冲存储器（cache memory）用的内存的大小 Active: long
	 *         在活跃使用中的缓冲或高速缓冲存储器页面文件的大小 Inactive: long
	 *         在不经常使用中的缓冲或高速缓冲存储器页面文件的大小. Dirty: long 等待被写回到磁盘的内存大小
	 * 
	 */
	public static Map<String, Long> getSysMemoryInfo() {
		Map<String, Long> result = new HashMap<String, Long>();
		try {
			@SuppressWarnings("rawtypes")
			Class procClass = Class.forName("android.os.Process");
			@SuppressWarnings("rawtypes")
			Class[] parameterTypes = new Class[] { String.class, String[].class, long[].class };
			Method readProclines = procClass.getMethod("readProcLines", parameterTypes);
			if (readProclines != null) {
				Object arglist[] = new Object[3];// SUPPRESS CHECKSTYLE
				long[] memInfoSizes = new long[FIELDS_SYS_MEMINFO.length];
				// CHECKSTYLE:OFF
				memInfoSizes[0] = 30;
				memInfoSizes[1] = -30;
				// CHECKSTYLE:
				arglist[0] = "/proc/meminfo";
				arglist[1] = FIELDS_SYS_MEMINFO;
				arglist[2] = memInfoSizes;

				readProclines.invoke(null, arglist);
				for (int i = 0; i < memInfoSizes.length; i++) {
					result.put(FIELDS_SYS_MEMINFO[i], memInfoSizes[i]);
				}
			}
		} catch (ClassNotFoundException e) {
			if (DEBUG) {
				Log.w(LOG_TAG, "getSysMemoryInfo Exception", e);
			}
			return null;
		} catch (SecurityException e) {
			if (DEBUG) {
				Log.w(LOG_TAG, "getSysMemoryInfo Exception", e);
			}
			return null;
		} catch (IllegalArgumentException e) {
			if (DEBUG) {
				Log.w(LOG_TAG, "getSysMemoryInfo Exception", e);
			}
			return null;
		} catch (IllegalAccessException e) {
			if (DEBUG) {
				Log.w(LOG_TAG, "getSysMemoryInfo Exception", e);
			}
			return null;
		} catch (InvocationTargetException e) {
			if (DEBUG) {
				Log.w(LOG_TAG, "getSysMemoryInfo Exception", e);
			}
			return null;
		} catch (NoSuchMethodException e) {
			if (DEBUG) {
				Log.w(LOG_TAG, "getSysMemoryInfo Exception", e);
			}
			return null;
		}
		return result;
	}

	/**
	 * 该函数通过反射读取/proc/pid/status 目前返回参数列表如下,如果需要增加，只需要在 mSysMemInfoFields
	 * 中增加相关列即可
	 * 
	 * @return 返回一个ArrayList 格式如下 VmLck: long 任务已经锁住的物理内存的大小。锁住的物理内存不能交换到硬盘
	 *         VmRSS: long 应用程序正在使用的物理内存的大小，就是用ps命令的参数rss的值 VmSize: long
	 *         程序数据段的大小（所占虚拟内存的大小），存放初始化了的数据 VmExe: long
	 *         程序所拥有的可执行虚拟内存的大小，代码段，不包括任务使用的库 VmStk: long 任务在用户态的栈的大小 VmLib:
	 *         long 被映像到任务的虚拟内存空间的库的大小. Threads: long 共享使用该信号描述符的任务的个数
	 * 
	 */
	public Map<String, Long> getProcMemoryInfo() {
		Map<String, Long> result = new HashMap<String, Long>();
		try {
			@SuppressWarnings("rawtypes")
			Class procClass = Class.forName("android.os.Process");
			@SuppressWarnings("rawtypes")
			Class[] parameterTypes = new Class[] { String.class, String[].class, long[].class };
			Method readProclines = procClass.getMethod("readProcLines", parameterTypes);
			if (readProclines != null) {
				Object arglist[] = new Object[3];

				long[] memInfoSizes = new long[FIELDS_PROC_MEMINFO.length];
				memInfoSizes[0] = -1;// SUPPRESS CHECKSTYLE
				int id = android.os.Process.myPid();
				arglist[0] = new String("/proc/" + id + "/status");
				arglist[1] = FIELDS_PROC_MEMINFO;
				arglist[2] = memInfoSizes;

				readProclines.invoke(null, arglist);
				for (int i = 0; i < memInfoSizes.length; i++) {
					result.put(FIELDS_PROC_MEMINFO[i], memInfoSizes[i]);
				}
			}
		} catch (ClassNotFoundException e) {
			if (DEBUG) {
				Log.w(LOG_TAG, "getProcMemoryInfo Exception", e);
			}
			return null;
		} catch (SecurityException e) {
			if (DEBUG) {
				Log.w(LOG_TAG, "getProcMemoryInfo Exception", e);
			}
			return null;
		} catch (IllegalArgumentException e) {
			if (DEBUG) {
				Log.w(LOG_TAG, "getProcMemoryInfo Exception", e);
			}
			return null;
		} catch (IllegalAccessException e) {
			if (DEBUG) {
				Log.w(LOG_TAG, "getProcMemoryInfo Exception", e);
			}
			return null;
		} catch (InvocationTargetException e) {
			if (DEBUG) {
				Log.w(LOG_TAG, "getProcMemoryInfo Exception", e);
			}
			return null;
		} catch (NoSuchMethodException e) {
			if (DEBUG) {
				Log.w(LOG_TAG, "getProcMemoryInfo Exception", e);
			}
			return null;
		}
		return result;
	}

}
