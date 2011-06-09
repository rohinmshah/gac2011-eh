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
	private PendingIntent contentIntent;
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
			}
			break;
		}
		askForStatusLoop();
		return null;
	}

	private void askForStatusLoop() {
		CharSequence contentTitle = "", contentText = "";
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
			contentText = "Error occurred while accessing the group.  Aborting.\nDetails: "
					+ e.getMessage();
			mNotification.setLatestEventInfo(mService.getApplicationContext(),
					contentTitle, contentText, contentIntent);
			mManager.notify(ContactDownloadService.NOTIFICATION_ID,
					mNotification);
		}

	}

	@Override
	protected void onProgressUpdate(GroupStatus... values) {
		CharSequence contentTitle = "Progress Update";
		CharSequence contentText = "";
		if (values[0].isFinished()) {
			contentTitle = "Finished!";
			contentText = values[0].getMemberCount()
					+ " contacts were added to your Contacts";
		} else {
			long milliseconds = values[0].getRemainingTime().getTime() / 1000;
			contentText = "Time left: " + milliseconds;
			if (values[0].getGroupMax() != WebWrapper.NO_GROUP_MAX) {
				contentText = contentText
						+ "\nNumber of people left: "
						+ (values[0].getGroupMax() - values[0].getMemberCount());
			}
		}
		mNotification.setLatestEventInfo(mService.getApplicationContext(),
				contentTitle, contentText, contentIntent);
		mManager.notify(ContactDownloadService.NOTIFICATION_ID, mNotification);
	}
}
