package com.beiying.fitmanager.core.utils;

import android.content.Context;

import com.beiying.fitmanager.core.LeCoreR;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LeDateUtils {
	
	private static final int MILLISECOND_PER_MINUTE = 60000;
	private static final int MINUTE_PER_HOUR = 60;
	private static final int HOUR_PER_DAY = 24;
	private static final int DAY_PER_MONTH = 30;
	private static final int MONTH_PER_YEAR = 12;
	
	private static final String MOST_USED_DATE_FOMAT = "yyyy-MM-dd HH:mm:ss";
	
	private LeDateUtils(){}
	
	public static String getChineseYMDHM(long time) {
		Date date = new Date(time);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return format.format(date);
	}
	
	public static String getMostUsedDateString() {
		return getMostUsedDateString(new Date());
	}
	
	public static String getMostUsedDateString(Date date) {
		DateFormat df = new SimpleDateFormat(MOST_USED_DATE_FOMAT);
		return df.format(date);
	}  

	public static String format(Date d, String pattern) {
		DateFormat dateFormat = new SimpleDateFormat(pattern, Locale.CHINA);
		return dateFormat.format(d);
	}
	
	public static String format(long when, String pattern) {
		Date date = new Date(when);
		return format(date, pattern);
	}

	public static boolean isSameMonth(Date date1, Date date2) {
		Calendar calendar1 = Calendar.getInstance();
		Calendar calendar2 = Calendar.getInstance();

		calendar1.setTime(date1);
		calendar2.setTime(date2);

		return calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)
				&& calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR);
	}
	
	public static boolean isSameDay(long when1, long when2) {
		Calendar calendar1 = Calendar.getInstance();
		Calendar calendar2 = Calendar.getInstance();

		calendar1.setTimeInMillis(when1);
		calendar2.setTimeInMillis(when2);

		return calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)
				&& calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
				&& calendar1.get(Calendar.DATE) == calendar2.get(Calendar.DATE);
	}

	public static boolean isSameDay(Date date1, Date date2) {
		Calendar calendar1 = Calendar.getInstance();
		Calendar calendar2 = Calendar.getInstance();

		calendar1.setTime(date1);
		calendar2.setTime(date2);

		return calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)
				&& calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
				&& calendar1.get(Calendar.DATE) == calendar2.get(Calendar.DATE);
	}

	public static String formatSyncTime(long lastSyncTime) {
		if(lastSyncTime <= 0)
			return null;
		
		Date syncTime = new Date(lastSyncTime);
		
		if(isSameDay(syncTime, new Date())) {
			return format(syncTime, "HH:mm");
		} else {
			return format(syncTime, "yyyy-MM-dd");
		}
	}
	
	public static boolean isToday(long when) {
		Calendar calendar1 = Calendar.getInstance();
		Calendar calendar2 = Calendar.getInstance();

		calendar1.setTimeInMillis(when);

		return calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)
				&& calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
				&& calendar1.get(Calendar.DATE) == calendar2.get(Calendar.DATE);
	}
	
	public static int getYear(long when) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(when);
		return calendar.get(Calendar.YEAR);
	}
	
	public static int getMonth(long when) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(when);
		return calendar.get(Calendar.MONTH);
	}
	
	public static int getDayOfMonth(long when) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(when);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}
	
	public static String getMostUserDateString(final long millis) {
		Date date = new Date(millis);
		DateFormat dateFormat = new SimpleDateFormat(MOST_USED_DATE_FOMAT);
		return dateFormat.format(date);
	}
	
	/**
	 * 获取系统时间 ，根据通用时间格式
	 */
	public static long getMillisFromMostUsedDateString(final String mostUserDateString) {
		DateFormat dateFormat = new SimpleDateFormat(MOST_USED_DATE_FOMAT);
		Date date;
		try {
			date = dateFormat.parse(mostUserDateString);
		} catch (ParseException e) {
			return -1;
		}
		return date.getTime();
		/*try {
			String temDateString = mostUserDateString.trim();
			int spaceIndex = temDateString.indexOf(' ');
			String dateString = mostUserDateString.substring(0, spaceIndex);
			String timeString = mostUserDateString.substring(spaceIndex + 1);
			
			int firstIndex = dateString.indexOf('-');
	        int secondIndex = dateString.indexOf('-', firstIndex + 1);
	        int year = Integer.parseInt(dateString.substring(0, firstIndex));
	        int month = Integer.parseInt(dateString.substring(firstIndex + 1, secondIndex));
	        int day = Integer.parseInt(dateString.substring(secondIndex + 1, dateString.length()));
	        
	        firstIndex = timeString.indexOf(':');
	        secondIndex = timeString.indexOf(':', firstIndex + 1);
	        int hourOfDay = Integer.parseInt(timeString.substring(0, firstIndex));
	        int minute = Integer.parseInt(timeString.substring(firstIndex + 1, secondIndex));
	        int second = Integer.parseInt(timeString.substring(secondIndex + 1, timeString.length()));
	        
	        GregorianCalendar canlendar = new GregorianCalendar();
	        canlendar.set(year, month - 1, day, hourOfDay, minute, second);
	        return canlendar.getTimeInMillis();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;*/
	}
	
	/**
	 * 获取时间间隔提示信息--根据系统时间
	 */
	public static String getGapRemindByMillis(Context context, long when) {
		long gapTime = System.currentTimeMillis() - when;
		if (gapTime <= MILLISECOND_PER_MINUTE) {
			return "";
//			return LeUtils.getString(R.string.date_just);
		} else {
			long gapMinute = gapTime / MILLISECOND_PER_MINUTE;
			if (gapMinute < MINUTE_PER_HOUR) {
				return String.format(LeUtils.getString(context, LeCoreR.getStringResId("date_minute_before")), gapMinute);
			}
			long gapHour = gapMinute / MINUTE_PER_HOUR;
			if (gapHour < HOUR_PER_DAY) {
				return String.format(LeUtils.getString(context, LeCoreR.getStringResId("date_hour_before")), gapHour);
			}
			long gapDay = gapHour / HOUR_PER_DAY;
			if (gapDay < DAY_PER_MONTH) {
				return String.format(LeUtils.getString(context, LeCoreR.getStringResId("date_day_before")), gapDay);
			}
			long gapMonth = gapDay / DAY_PER_MONTH;
			if (gapMonth < MONTH_PER_YEAR) {
				return String.format(LeUtils.getString(context, LeCoreR.getStringResId("date_month_before")), gapMonth);
			}
			long gapYear = gapMonth / MONTH_PER_YEAR;
			return String.format(LeUtils.getString(context, LeCoreR.getStringResId("date_year_before")), gapYear);
		}
	}
}
