package com.beiying.fitmanager.core;

import android.util.Log;

public final class LeLog {

	/** 调试总开关 */
	public static boolean DEBUG = LeBuildConfig.DEBUG;
	/** Log TAG */
	public static final String LOG_TAG = "Lenovo";
	public static final String TAG_EXCEPTION = "Exception";

	/** DEBUG调试开关 */
	public static final boolean DEBUG_DEBUG = true;
	/** ERROR调试开关 */
	public static final boolean DEBUG_ERROR = true;
	/** INFO调试开关 */
	public static final boolean DEBUG_INFO = true;
	/** VERBOSE调试开关 */
	public static final boolean DEBUG_VERBOSE = false;
	/** WARN调试开关 */
	public static final boolean DEBUG_WARN = true;

	/** TAG过滤掉FILE_TYPE */
	private static final String FILE_TYPE = ".java";

	private enum LogLevel {
		DEBUG,
		ERROR,
		INFO,
		VERBOSE,
		WARN,
	}

	private LeLog() {
	}
	
	public static void init(boolean debug) {
		DEBUG = debug;
	}
	
	public static void d(String msg) {
		d(LOG_TAG, msg);
	}

	/**
	 * DEBUG信息输出
	 * 
	 * @param msg
	 *            输出信息
	 */
	public static void d(String tag, String msg) {
		if (DEBUG && DEBUG_DEBUG) {
			doLog(tag, LogLevel.DEBUG, msg, 2, true, null);
		}
	}
	
	public static void d(String msg, Throwable throwable) {
		d(LOG_TAG, msg, throwable);
	}

	/**
	 * DEBUG信息输出
	 * 
	 * @param msg
	 *            输出信息
	 * @param throwable
	 *            输出异常
	 */
	public static void d(String tag, String msg, Throwable throwable) {
		if (DEBUG && DEBUG_DEBUG) {
			doLog(tag, LogLevel.DEBUG, msg, 2, true, throwable);
		}
	}
	
	public static void e(Throwable t) {
		e(TAG_EXCEPTION, "", t);
	}
	
	public static void e(String msg) {
		e(LOG_TAG, msg);
	}

	/**
	 * ERROR信息输出
	 * 
	 * @param msg
	 *            输出信息
	 */
	public static void e(String tag, String msg) {
		if (DEBUG && DEBUG_ERROR) {
			doLog(tag, LogLevel.ERROR, msg, 2, true, null);
		}
	}
	
	public static void e(String msg, Throwable throwable) {
		e(LOG_TAG, msg, throwable);
	}

	/**
	 * ERROR信息输出
	 * 
	 * @param msg
	 *            输出信息
	 * @param throwable
	 *            输出异常
	 */
	public static void e(String tag, String msg, Throwable throwable) {
		if (DEBUG && DEBUG_ERROR) {
			doLog(tag, LogLevel.ERROR, msg, 2, true, throwable);
		}
	}

	/**
	 * INFO信息输出
	 * 
	 * @param msg
	 *            输出信息
	 */
	public static void i(String tag, String msg) {
		if (DEBUG && DEBUG_INFO) {
			doLog(tag, LogLevel.INFO, msg, 2, true, null);
		}
	}
	
	public static void i(String msg) {
		i(LOG_TAG, msg);
	}
	
	public static void i(String msg, Throwable throwable) {
		i(LOG_TAG, msg, throwable);
	}

	/**
	 * INFO信息输出
	 * 
	 * @param msg
	 *            输出信息
	 * @param throwable
	 *            输出异常
	 */
	public static void i(String tag, String msg, Throwable throwable) {
		if (DEBUG && DEBUG_INFO) {
			doLog(tag, LogLevel.INFO, msg, 2, true, throwable);
		}
	}
	
	public static void v(String msg) {
		v(LOG_TAG, msg);
	}

	/**
	 * VERBOSE信息输出
	 * 
	 * @param msg
	 *            输出信息
	 */
	@SuppressWarnings("unused")
	public static void v(String tag, String msg) {
		if (DEBUG && DEBUG_VERBOSE) {
			doLog(tag, LogLevel.VERBOSE, msg, 2, true, null);
		}
	}
	
	public static void v(String msg, Throwable throwable) {
		v(LOG_TAG, msg, throwable);
	}

	/**
	 * VERBOSE信息输出
	 * 
	 * @param msg
	 *            输出信息
	 * @param throwable
	 *            输出异常
	 */
	@SuppressWarnings("unused")
	public static void v(String tag, String msg, Throwable throwable) {
		if (DEBUG && DEBUG_VERBOSE) {
			doLog(tag, LogLevel.VERBOSE, msg, 2, true, throwable);
		}
	}
	
	public static void w(String msg) {
		w(LOG_TAG, msg);
	}

	/**
	 * WARN信息输出
	 * 
	 * @param msg
	 *            输出信息
	 */
	public static void w(String tag, String msg) {
		if (DEBUG && DEBUG_WARN) {
			doLog(tag, LogLevel.WARN, msg, 2, true, null);
		}
	}
	
	public static void w(String msg, Throwable throwable) {
		w(LOG_TAG, msg, throwable);
	}

	/**
	 * WARN信息输出
	 * 
	 * @param msg
	 *            输出信息
	 * @param throwable
	 *            输出异常
	 */
	public static void w(String tag, String msg, Throwable throwable) {
		if (DEBUG && DEBUG_WARN) {
			doLog(tag, LogLevel.WARN, msg, 2, true, throwable);
		}
	}

	/**
	 * @param level
	 *            Log级别
	 * @param msg
	 *            要输出的Log信息
	 * @param stackTraceLevel
	 *            函数调用栈层级
	 * @param showMethod
	 *            是否输出调用log的类方法
	 * @param throwable
	 *            输出异常栈信息
	 */
	private static void doLog(String tag, LogLevel level, String msg, int stackTraceLevel, boolean showMethod, Throwable throwable) {
		StackTraceElement stackTrace = (new Throwable()).getStackTrace()[stackTraceLevel];
		String filename = stackTrace.getFileName();
		String methodname = stackTrace.getMethodName();
		int linenumber = stackTrace.getLineNumber();
		//当心！proguard混淆以后getFileName会是一个null值！
		if (filename != null && filename.contains(FILE_TYPE)) {
			filename = filename.replace(FILE_TYPE, "");
		}

		String output = "";
		if (showMethod) {
			output = String.format("at (%s.java:%d)%s: %s", filename, linenumber, methodname, msg);
		} else {
			output = String.format("at (%s.java:%d)%s", filename, linenumber, msg);
		}
		tag = tag == null ? LOG_TAG: tag;
		switch (level) {
			case DEBUG:
				if (throwable == null) {
					Log.d(tag, output);
				} else {
					Log.d(tag, output, throwable);
				}
				break;
			case ERROR:
				if (throwable == null) {
					Log.e(tag, output);
				} else {
					Log.e(tag, output, throwable);
				}
				break;
			case INFO:
				if (throwable == null) {
					Log.i(tag, output);
				} else {
					Log.i(tag, output, throwable);
				}
				break;
			case VERBOSE:
				if (throwable == null) {
					Log.v(tag, output);
				} else {
					Log.v(tag, output, throwable);
				}
				break;
			case WARN:
				if (throwable == null) {
					Log.w(tag, output);
				} else {
					Log.w(tag, output, throwable);
				}
				break;
			default:
				break;
		}
	}
}
