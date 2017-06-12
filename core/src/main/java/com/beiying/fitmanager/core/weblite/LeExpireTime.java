package com.beiying.fitmanager.core.weblite;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.text.format.DateUtils;

public class LeExpireTime {
	private static final long NEVER_EXPIRE_TIME = DateUtils.YEAR_IN_MILLIS * 100;
	//in millis 均为时间间隔
	public long mMin;
	public long mMax;
	public long mDefault;

	public LeExpireTime(long defaultValue, long min, long max) {
		mDefault = defaultValue;
		mMin = min;
		mMax = max;
	}
	
	public static LeExpireTime createNeverExpire() {
		return new LeExpireTime(NEVER_EXPIRE_TIME, NEVER_EXPIRE_TIME, NEVER_EXPIRE_TIME);
	}

	public static long parseExpires(String expire, long defaultValue, long min, long max) {
		long currentMillis = System.currentTimeMillis();
		long expireMs = currentMillis + defaultValue;
		String pattern = "E, dd MMM yyyy HH:mm:ss Z";
		DateFormat format = new SimpleDateFormat(pattern, Locale.US);
		try {
			Date date = format.parse(expire);
			long revExpire = date.getTime();
			if (currentMillis + min <= revExpire && revExpire <= currentMillis + max) {
				expireMs = revExpire;
			}
		} catch (ParseException e) {

		}
		return expireMs;
	}
}
