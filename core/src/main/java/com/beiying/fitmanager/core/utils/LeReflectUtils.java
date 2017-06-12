package com.beiying.fitmanager.core.utils;

import android.util.Log;

import com.beiying.fitmanager.core.LeBuildConfig;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 反射实用类
 */
public final class LeReflectUtils {

	/** DEBUG mode */
	private static final boolean DEBUG = LeBuildConfig.DEBUG;
	/** Log TAG */
	private static final String LOG_TAG = "LeReflectUtils";

	/**
	 * Constructor
	 */
	private LeReflectUtils() {
	}

	/**
	 * 获取指定类的声明方法
	 * 
	 * @param aClass
	 *            指定类
	 * @param methodName
	 *            方法名
	 * @param paramTypes
	 *            参数类型
	 * @return 指定类的方法
	 */
	public static Method getDeclaredMethod(Class<?> aClass, String methodName, Class<?>... paramTypes) {
		try {
			Method method = aClass.getDeclaredMethod(methodName, paramTypes);
			method.setAccessible(true);
			return method;
		} catch (Exception e) {
			if (DEBUG) {
				Log.w(LOG_TAG, "getMethod Exception", e);
			}
		} catch (Error e) {
			if (DEBUG) {
				Log.w(LOG_TAG, "getMethod Error", e);
			}
		}
		return null;
	}
	
	public static Method getMethod(Class<?> aClass, String methodName, Class<?>... paramTypes) {
		try {
			Method method = aClass.getMethod(methodName, paramTypes);
			method.setAccessible(true);
			return method;
		} catch (Exception e) {
			if (DEBUG) {
				Log.w(LOG_TAG, "getMethod Exception", e);
			}
		} catch (Error e) {
			if (DEBUG) {
				Log.w(LOG_TAG, "getMethod Error", e);
			}
		}
		return null;
	}

	/**
	 * 调用指定对象的方法
	 * 
	 * @param method
	 *            指定的方法
	 * @param object
	 *            指定的对象
	 * @param defaultValue
	 *            默认返回值
	 * @param args
	 *            方法传递参数
	 * @return 方法返回值
	 */
	public static Object invoke(Method method, Object object, Object defaultValue, Object... args) {
		try {
			return method.invoke(object, args);
		} catch (Exception e) {
			if (DEBUG) {
				Log.w(LOG_TAG, "invoke Exception", e);
			}
		} catch (Error e) {
			if (DEBUG) {
				Log.w(LOG_TAG, "invoke Error", e);
			}
		}
		return defaultValue;
	}
	
	/**
	 * Find and invoke the method on the object if it exists.
	 * <p>
	 * If result is an array of size 1, then return the return value from
	 * invoking the method in result[0]. Note that if the method returns "void",
	 * the result object (result[0]) will be null.
	 * 
	 * @return true if method was found and invoked; otherwise, false.
	 */
	public static boolean invokeMethod(Object object, String methodName, Object[] params, Object[] result) { // SUPPRESS CHECKSTYLE
		Class<?>[] paramTypes = params == null ? null : new Class<?>[params.length];
		if (params != null) {
			for (int i = 0; i < params.length; ++i) {
				paramTypes[i] = params[i] == null ? null : params[i].getClass();
			}
		}
		return invokeMethod(object, methodName, paramTypes, params, result);
	}

	/**
	 * Find and invoke the method on the object if it exists.
	 * <p>
	 * If result is an array of size 1, then return the return value from
	 * invoking the method in result[0]. Note that if the method returns "void",
	 * the result object (result[0]) will be null.
	 * 
	 * @param object
	 *            要反射的对象实例
	 * @param methodName
	 *            要反射的方法名
	 * @param paramTypes
	 *            方法的参数类型
	 * @param params
	 *            方法的参数
	 * @param result
	 *            方法的返回结果
	 * @return true if method was found and invoked; otherwise, false.
	 */
	public static boolean invokeMethod(Object object, String methodName, Class<?>[] paramTypes, Object[] params, // SUPPRESS CHECKSTYLE
									   Object[] result) { // SUPPRESS CHECKSTYLE
		if (object == null) {
			return false;
		}

		boolean retval = false;

		Method method = getDeclaredMethod(object.getClass(), methodName, paramTypes);
		// invoke
		if (method != null) {
			method.setAccessible(true);
			try {
				final Object invocationResult = method.invoke(object, params);
				if (result != null && result.length > 0) {
					result[0] = invocationResult;
				}
				retval = true;
			} catch (IllegalAccessException iae) {
				throw new IllegalArgumentException(methodName, iae);
			} catch (InvocationTargetException ite) {
				throw new IllegalArgumentException(methodName, ite);
			} catch (ExceptionInInitializerError eiie) {
				throw new IllegalArgumentException(methodName, eiie);
			}
		} else {
			if (DEBUG) {
				Log.w(LOG_TAG, methodName + " not found.");
			}
		}

		return retval;
	}
	
	/**
	 * 静态方法调用
	 * @param cls
	 * @param methodName
	 * @param paramTypes
	 * @param params
	 * @param result
	 * @return
	 */
	public static Object invokeStaticMethod(Class cls, String methodName, Class<?>[] paramTypes, Object[] params) { // SUPPRESS CHECKSTYLE

		Object ret = null;

		Method method = getMethod(cls, methodName, paramTypes);
		// invoke
		if (method != null) {
			method.setAccessible(true);
			try {
				final Object invocationResult = method.invoke(cls, params);
				ret = invocationResult;
			} catch (IllegalAccessException iae) {
			} catch (InvocationTargetException ite) {
			} catch (ExceptionInInitializerError eiie) {
			}
		} else {
			if (DEBUG) {
				Log.w(LOG_TAG, methodName + " not found.");
			}
		}

		return ret;
	}

	/**
	 * 获取指定类对象的属性
	 * 
	 * @param aClass
	 *            类对象
	 * @param fileldName
	 *            属性名
	 * @return 属性
	 */
	public static Field getField(Class<?> aClass, String fileldName) {
		try {
			Field field = aClass.getDeclaredField(fileldName);
			field.setAccessible(true);
			return field;
		} catch (Exception e) {
			if (DEBUG) {
				Log.w(LOG_TAG, "getField Exception", e);
			}
		} catch (Error e) {
			if (DEBUG) {
				Log.w(LOG_TAG, "getField Error", e);
			}
		}
		return null;
	}

	/**
	 * 获取指定类对象的属性值
	 * 
	 * @param field
	 *            属性
	 * @param object
	 *            类对象
	 * @param defaultValue
	 *            默认值
	 * @return 属性值
	 */
	public static Object getFieldValue(Field field, Object object, Object defaultValue) {
		try {
			return field.get(object);
		} catch (Exception e) {
			if (DEBUG) {
				Log.w(LOG_TAG, "getFieldValue Exception", e);
			}
		} catch (Error e) {
			if (DEBUG) {
				Log.w(LOG_TAG, "getFieldValue Error", e);
			}
		}
		return defaultValue;
	}
	
	/**
	 * 获取指定类对象的属性值
	 * 
	 * @param object
	 *            类对象
	 * @param fieldName
	 *            属性名
	 * @param defaultValue
	 *            默认值
	 * @return 属性值
	 */
	public static Object getFieldValue(Object object, String fieldName, Object defaultValue) {
		Field field = getField(object.getClass(), fieldName);
		if (field != null) {
			return getFieldValue(field, object, defaultValue);
		}
		return defaultValue;
	}

	/**
	 * 设置指定类对象的属性值
	 * 
	 * @param field
	 *            类对象
	 * @param object
	 *            属性名
	 * @param value
	 *            属性值
	 * @return 设置成功则返回true，否则返回false
	 */
	public static boolean setFieldValue(Field field, Object object, Object value) {
		try {
			field.set(object, value);
			return true;
		} catch (Exception e) {
			if (DEBUG) {
				Log.w(LOG_TAG, "setFieldValue Exception", e);
			}
		} catch (Error e) {
			if (DEBUG) {
				Log.w(LOG_TAG, "setFieldValue Error", e);
			}
		}
		return false;
	}

	/**
	 * 设置指定类对象的属性值
	 * 
	 * @param object
	 *            类对象
	 * @param fieldName
	 *            属性名
	 * @param value
	 *            属性值
	 * @return 如果设置成功则返回true，否则返回false
	 */
	public static boolean setFieldValue(Object object, String fieldName, Object value) {
		Field field = getField(object.getClass(), fieldName);
		if (field != null) {
			return setFieldValue(field, object, value);
		}
		return false;
	}

	/**
	 * 创建类实例
	 * 
	 * @param aClass
	 *            所属类
	 * @param paramTypes
	 *            构造方法参数类型
	 * @param args
	 *            构造方法参数
	 * @return 创建的类实例
	 */
	public static Object newInstance(Class<?> aClass, Class<?>[] paramTypes, Object[] args) {
		try {
			Constructor<?> constructor = aClass.getDeclaredConstructor(paramTypes);
			constructor.setAccessible(true);
			return constructor.newInstance(args);
		} catch (Exception e) {
			if (DEBUG) {
				Log.w(LOG_TAG, "newInstance Exception", e);
			}
		} catch (Error e) {
			if (DEBUG) {
				Log.w(LOG_TAG, "newInstance Error", e);
			}
		}
		return null;
	}

}
