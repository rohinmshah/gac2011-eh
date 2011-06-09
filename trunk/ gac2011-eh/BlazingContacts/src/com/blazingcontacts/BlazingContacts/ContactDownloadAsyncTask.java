package com.blazingcontacts.BlazingContacts;

import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class ContactDownloadAsyncTask extends
		AsyncTask<Intent, GroupStatus, Void> {

	private ContactDownloadService mService;
	private WebTestWrapper mWrapper;
	private GroupStatus mStatus;
	private Notification mNotification;
	private NotificationManager mManager;
	private int mType;

	public ContactDownloadAsyncTask(ContactDownloadService service,
			Notification notification, NotificationManager manager) {
		mService = service;
		mNotification = notification;
		mManager = manager;
	}

	@Override
	protected Void doInBackground(Intent... params) {
		CharSequence contentTitle, contentText;
		Intent notificationIntent = new Intent(mService,
				ContactDownloadService.class);
		PendingIntent contentIntent = PendingIntent.getActivity(
				mService.getApplicationContext(), 0, notificationIntent, 0);

		Intent intent = params[0];
		Bundle extras = intent.getExtras();

		mType = extras.getInt(GroupActivity.GROUP_TYPE);
		String groupName = extras.getString(GroupActivity.GROUP_NAME);
		String password = "blank";
		Contact user = new Contact(extras.getString(GroupActivity.MY_NAME),
				extras.getString(GroupActivity.MY_PHONE_NUMBER),
				extras.getString(GroupActivity.MY_EMAIL));

		switch (mType) {
		case GroupActivity.TYPE_START:
			try {
				Date time = WebWrapper.getDateFromNow(extras
						.getLong(StartActivity.LIFETIME_MILLIS));
				int maxPeople = extras.getInt(StartActivity.MAX_PEOPLE);
				mWrapper = new WebTestWrapper(groupName, password, user, time,
						maxPeople);
			} catch (Exception e) {
				contentTitle = "Connection failed";
				contentText = "Could not connect to the server.  Aborting.";
				mNotification.setLatestEventInfo(
						mService.getApplicationContext(), contentTitle,
						contentText, contentIntent);
				mManager.notify(ContactDownloadService.NOTIFICATION_ID,
						mNotification);
				return null;
			}
			break;

		case GroupActivity.TYPE_JOIN:
			try {
				mWrapper = new WebTestWrapper(groupName, password, user);
			} catch (Exception e) {
				contentTitle = "Connection failed";
				contentText = "Could not connect to the server.  Aborting.";
				mNotification.setLatestEventInfo(
						mService.getApplicationContext(), contentTitle,
						contentText, contentIntent);
				mManager.notify(ContactDownloadService.NOTIFICATION_ID,
						mNotification);
				return null;
			}
			break;
		}
		askForStatusLoop();
		return null;
	}

	private void askForStatusLoop() {
		CharSequence contentTitle = "", contentText = "";
		Intent notificationIntent = new Intent(mService,
				ContactDownloadService.class);
		PendingIntent contentIntent = PendingIntent.getActivity(
				mService.getApplicationContext(), 0, notificationIntent, 0);
		try {
			Thread.sleep(1000);
			mStatus = mWrapper.getStatus();
			if (mStatus.isFinished()) {
				Contact[] contactsToAdd = mWrapper.downloadContactInfo();
				ContactsProviderWrapper cpw = new ContactsProviderWrapper(
						mService.getApplicationContext(), 0);
				for (Contact contactToAdd : contactsToAdd) {
					cpw.addContact(contactToAdd);
				}
				publishProgress(mStatus);
			} else {
				publishProgress(mStatus);
				askForStatusLoop();
			}
		} catch (InterruptedException i) {
			contentTitle = "Interruption";
			contentText = "The service was interrupted while waiting for the group to fill.  Aborting.";
			mNotification.setLatestEventInfo(mService.getApplicationContext(),
					contentTitle, contentText, contentIntent);
			mManager.notify(ContactDownloadService.NOTIFICATION_ID,
					mNotification);
		} catch (Exception e) {
			contentTitle = "Web service problem";
			contentText = e.getMessage();
			if (contentText == null || contentText.equals("")) {
				contentText = "Unknown error";
			}
			mNotification.setLatestEventInfo(mService.getApplicationContext(),
					contentTitle, contentText, contentIntent);
			mManager.notify(ContactDownloadService.NOTIFICATION_ID,
					mNotification);
		}

	}

	@Override
	protected void onProgressUpdate(GroupStatus... values) {
		CharSequence contentTitle = "Progress Update", contentText = "";
		Intent notificationIntent = new Intent(mService,
				ContactDownloadService.class);
		PendingIntent contentIntent = PendingIntent.getActivity(
				mService.getApplicationContext(), 0, notificationIntent, 0);

		if (values[0].isFinished()) {
			contentTitle = "Finished!";
			contentText = values[0].getMemberCount()
					+ " contacts were added to your Contacts";
			mNotification.setLatestEventInfo(mService.getApplicationContext(),
					contentTitle, contentText, contentIntent);
			mManager.notify(ContactDownloadService.NOTIFICATION_ID,
					mNotification);
		} else {
			long milliseconds = values[0].getRemainingTime().getTime() / 1000;
			contentText = "Time: " + milliseconds;
			if (values[0].getGroupMax() != WebWrapper.NO_GROUP_MAX) {
				contentText = contentText
						+ "People: "
						+ (values[0].getGroupMax() - values[0].getMemberCount());
			}
			mNotification.setLatestEventInfo(mService.getApplicationContext(),
					contentTitle, contentText, contentIntent);
			mManager.notify(ContactDownloadService.NOTIFICATION_ID,
					mNotification);
		}
	}
}
