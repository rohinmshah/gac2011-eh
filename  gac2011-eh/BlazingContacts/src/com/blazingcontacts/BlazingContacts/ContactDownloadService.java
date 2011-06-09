package com.blazingcontacts.BlazingContacts;

import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

public class ContactDownloadService extends Service {

	private static final int NOTIFICATION_ID = 1;

	private WebWrapper wrapper;
	private Notification notification;
	private NotificationManager mManager;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		int icon = android.R.drawable.stat_notify_sync;
		CharSequence text = "Starting BlazingContact Download Counter";
		long when = System.currentTimeMillis();
		notification = new Notification(icon, text, when);

		CharSequence contentTitle = "BlazingContacts Status";
		CharSequence contentText = "Starting the counter";
		Intent notificationIntent = new Intent(this,
				ContactDownloadService.class);
		PendingIntent contentIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, notificationIntent, 0);
		notification.setLatestEventInfo(getApplicationContext(), contentTitle,
				contentText, contentIntent);

		mManager.notify(NOTIFICATION_ID, notification);
		stopSelf();

		Bundle extras = intent.getExtras();
		int type = extras.getInt(GroupActivity.GROUP_TYPE);
		String groupName = extras.getString(GroupActivity.GROUP_NAME);
		String password = "blank";
		Contact user = new Contact(extras.getString(GroupActivity.MY_NAME),
				extras.getString(GroupActivity.MY_PHONE_NUMBER),
				extras.getString(GroupActivity.MY_EMAIL));
		Date time = WebWrapper.getDateFromNow(extras
				.getLong(StartActivity.LIFETIME_MILLIS));
		int maxPeople = extras.getInt(StartActivity.MAX_PEOPLE);
		switch (type) {
		case GroupActivity.TYPE_START:
			try {
				wrapper = new WebWrapper(groupName, password, user, time,
						maxPeople);
			} catch (Exception e) {

			}
			break;
		case GroupActivity.TYPE_JOIN:
			break;
		}
		return 1;
	}
}
