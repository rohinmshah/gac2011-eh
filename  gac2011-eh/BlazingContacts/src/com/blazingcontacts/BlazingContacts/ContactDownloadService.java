package com.blazingcontacts.BlazingContacts;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

/**
 * Simple service to poll the group status and download contact information at
 * end of group lifetime
 */
public class ContactDownloadService extends Service {

	public static final int NOTIFICATION_ID = 943;

	private Notification mNotification;
	private NotificationManager mManager;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		// Create new notification
		mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		int icon = android.R.drawable.stat_notify_sync;
		long when = System.currentTimeMillis();
		mNotification = new Notification(icon, null, when);

		CharSequence contentTitle = "BlazingContacts Status";
		CharSequence contentText = "Starting the counter";
		Intent notificationIntent = new Intent(this,
				ContactDownloadService.class);
		PendingIntent contentIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, notificationIntent, 0);
		mNotification.setLatestEventInfo(getApplicationContext(), contentTitle,
				contentText, contentIntent);

		mManager.notify(NOTIFICATION_ID, mNotification);
		Toast.makeText(getApplicationContext(),
				"Waiting on group.  See notification for remaining time.",
				Toast.LENGTH_LONG).show();
		ContactDownloadAsyncTask cdat = new ContactDownloadAsyncTask(this,
				mNotification, mManager);
		cdat.execute(intent);
		return START_NOT_STICKY;
	}
}
