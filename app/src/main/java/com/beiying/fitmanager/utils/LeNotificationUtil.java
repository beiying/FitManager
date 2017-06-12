package com.beiying.fitmanager.utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.RemoteViews;

import com.beiying.fitmanager.BYMainActivity;
import com.beiying.fitmanager.R;


public class LeNotificationUtil {
	
	public static Notification buildNotification(Context context, String title, String msg) {
		if (Build.VERSION.SDK_INT >= 16) {
			return buildNotificationWithBuilder(context, title, msg);
		} else {
			return buildNotificationWithRemoteView(context, title, msg);
		}
	}
	
	@SuppressLint("NewApi")
	private static Notification buildNotificationWithBuilder(Context context, String title, String msg) {
		Notification.Builder builder = new Notification.Builder(context);
		builder.setWhen(System.currentTimeMillis());
//		builder.setSmallIcon(R.drawable.notification_icon);
		builder.setAutoCancel(true);
		builder.setSound(null);
//		builder.setLargeIcon(LeUtils.getBitmapUsingDrawable(context, R.drawable.ic_launcher_browser));
		
		Intent intent = new Intent(context, BYMainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
		builder.setContentIntent(PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT));
		
		builder.setContentTitle(title);
		builder.setContentText(msg);
		
		Notification notification = builder.build();
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		return notification;
	}
	
	private static Notification buildNotificationWithRemoteView(Context context, String title, String msg) {
		Notification notification = new Notification(android.R.drawable.stat_notify_more, title, System.currentTimeMillis());
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_item);
		remoteViews.setTextViewText(R.id.notif_title, title);
		
		remoteViews.setViewVisibility(R.id.notif_progress, View.INVISIBLE);
		notification.contentView = remoteViews;
		Intent intent = new Intent(context, BYMainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
		notification.contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		return notification;
	}
}
