package com.blazingcontacts.BlazingContacts;

import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

/**
 * @author Rohin
 * 
 *         An AsyncTask (i.e. creates a new thread) which manages the
 *         notification and downloads the contact information once the group is
 *         full or has expired.
 * 
 */
public class ContactDownloadAsyncTask extends
		AsyncTask<Intent, GroupStatus, Void> {

	private ContactDownloadService mService;
	private WebTestWrapper mWrapper;
	private GroupStatus mStatus;
	private Notification mNotification;
	private NotificationManager mManager;
	private int mType;
	private Toast errorToast;
	private String userName;
	private String userPhone;
	private String userEmail;

	/**
	 * Creates a new AsyncTask with a notification and a ContactDownloadService.
	 * 
	 * @param service
	 *            - the ContactDownloadService that (presumably) created this
	 *            AsyncTask.
	 * @param notification
	 *            - the notification to use
	 * @param manager
	 *            - the manager for the notification
	 */
	public ContactDownloadAsyncTask(ContactDownloadService service,
			Notification notification, NotificationManager manager) {
		mService = service;
		mNotification = notification;
		mManager = manager;
		errorToast = Toast.makeText(mService.getApplicationContext(),
				"An error occurred.  See the notification for details.",
				Toast.LENGTH_LONG);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 * 
	 * The method that runs on the new thread. Sets up the AsyncTask by getting
	 * the information from the given intent, and then calls askForStatusLoop.
	 */
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
		userName = extras.getString(GroupActivity.MY_NAME);
		userPhone = extras.getString(GroupActivity.MY_PHONE_NUMBER);
		userEmail = extras.getString(GroupActivity.MY_EMAIL);
		Contact user = new Contact(userName, userPhone, userEmail);

		switch (mType) {
		case GroupActivity.TYPE_START:
			try {
				Date time = WebWrapper.getDateFromNow(extras
						.getLong(StartActivity.LIFETIME_MILLIS));
				int maxPeople = extras.getInt(StartActivity.MAX_PEOPLE);
				mWrapper = new WebTestWrapper(groupName, password, user, time,
						maxPeople);
			} catch (Exception e) {
				Log.e("ContactDownloadAsyncTask", e.getMessage());
				contentTitle = "Connection failed";
				contentText = "Could not connect to the server.  Aborting.";
				mNotification.setLatestEventInfo(
						mService.getApplicationContext(), contentTitle,
						contentText, contentIntent);
				mManager.notify(ContactDownloadService.NOTIFICATION_ID,
						mNotification);
				errorToast.show();
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
				errorToast.show();
				return null;
			}
			break;
		}
		askForStatusLoop();
		return null;
	}

	/**
	 * Asks the server for the status periodically, and acts if necessary. Sends
	 * the GroupStatus to publishProgress so that the notification can be
	 * updated.
	 */
	private void askForStatusLoop() {
		while (true) {
			Intent notificationIntent = new Intent(mService,
					ContactDownloadService.class);
			PendingIntent contentIntent = PendingIntent.getActivity(
					mService.getApplicationContext(), 0, notificationIntent, 0);
			CharSequence contentTitle = "", contentText = "";
			try {
				Thread.sleep(1000);
				mStatus = mWrapper.getStatus();
				if (mStatus.isFinished()) {
					Contact[] contactsToAdd = mWrapper.downloadContactInfo();
					if (mStatus.getMemberCount() != contactsToAdd.length) {
						Log.e("ContactDownloadAsyncTask", "Inconsistent Data");
					}
					ContactsProviderWrapper cpw = new ContactsProviderWrapper(
							mService.getApplicationContext(), 0);
					for (Contact contactToAdd : contactsToAdd) {
						if (!isUser(contactToAdd)) {
							cpw.addContact(contactToAdd);
						}
					}
					publishProgress(mStatus);
					break;
				} else {
					publishProgress(mStatus);
				}
			} catch (InterruptedException i) {
				Log.e("InterruptedException", i.getMessage());
				contentTitle = "Interruption";
				contentText = "The service was interrupted while waiting for the group to fill.  Aborting.";
				mNotification.setLatestEventInfo(
						mService.getApplicationContext(), contentTitle,
						contentText, contentIntent);
				mManager.notify(ContactDownloadService.NOTIFICATION_ID,
						mNotification);
				errorToast.show();
				break;
			} catch (OperationApplicationException oae) {
				Log.e("OperationApplicationException", oae.getMessage());
				contentTitle = "Operation Error";
				contentText = "The contacts could not be updated.  Aborting.";
				mNotification.setLatestEventInfo(
						mService.getApplicationContext(), contentTitle,
						contentText, contentIntent);
				mManager.notify(ContactDownloadService.NOTIFICATION_ID,
						mNotification);
				errorToast.show();
				break;
			} catch (RemoteException re) {
				Log.e("RemoteException", re.getMessage());
				contentTitle = "Remote Error";
				contentText = "The contacts could not be updated.  Aborting.";
				mNotification.setLatestEventInfo(
						mService.getApplicationContext(), contentTitle,
						contentText, contentIntent);
				mManager.notify(ContactDownloadService.NOTIFICATION_ID,
						mNotification);
				errorToast.show();
				break;
			} catch (Exception e) {
				Log.e("Exception", e.getMessage());
				contentTitle = "Web service problem";
				contentText = e.getMessage();
				if (contentText == null || contentText.equals("")) {
					contentText = "Unknown error";
				}
				mNotification.setLatestEventInfo(
						mService.getApplicationContext(), contentTitle,
						contentText, null);
				mManager.notify(ContactDownloadService.NOTIFICATION_ID,
						mNotification);
				errorToast.show();
				break;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
	 * 
	 * Updates the notification upon receiving an update from askForStatusLoop.
	 */
	@Override
	protected void onProgressUpdate(GroupStatus... values) {
		CharSequence contentTitle = "Progress Update", contentText = "";
		Intent notificationIntent = new Intent(mService,
				ContactDownloadService.class);
		PendingIntent contentIntent = PendingIntent.getActivity(
				mService.getApplicationContext(), 0, notificationIntent, 0);
		if (values[0].isFinished()) {
			contentTitle = "Finished!";
			contentText = (values[0].getMemberCount() - 1)
					+ " contacts were added to your Contacts";
			Toast.makeText(mService.getApplicationContext(),
					"Finished! " + contentText, Toast.LENGTH_LONG).show();
		} else {

			long seconds = values[0].getRemainingTime().getTime() / 1000;
			contentText = "Time left: " + seconds;
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

	private boolean isUser(Contact toCheck) {
		if (toCheck.getPhoneNumber().equals("")) {
			if (toCheck.getEmail().equals("")) {
				return toCheck.getName().equals(userName);
			} else if (toCheck.getEmail().equals(userEmail)) {
				return true;
			}
		} else if (toCheck.getPhoneNumber().equals(userPhone)) {
			return true;
		}
		return false;
	}
}
