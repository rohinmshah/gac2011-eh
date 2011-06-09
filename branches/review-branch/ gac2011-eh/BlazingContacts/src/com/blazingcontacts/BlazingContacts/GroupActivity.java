package com.blazingcontacts.BlazingContacts;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.view.View;

/**
 * @author Rohin
 * 
 *         An abstract class that implements some of the shared functionality in
 *         other Activities. Specifically, it is meant for Activities which have
 *         to ask the user to choose a contact, and then extract the name, phone
 *         number and email address from it.
 * 
 */
public abstract class GroupActivity extends Activity {

	private static final int PICK_CONTACT = 3;

	public static final String MY_NAME = "My Name";
	public static final String MY_PHONE_NUMBER = "My Phone Number";
	public static final String MY_EMAIL = "My Email";
	public static final String GROUP_NAME = "Group Name";
	public static final String GROUP_TYPE = "Group Type";
	public static final int TYPE_START = 0;
	public static final int TYPE_JOIN = 1;

	/**
	 * The onClickListener method for a Button which sets its onClick tag to
	 * this method in the layout file. When clicked, an Intent will be fired to
	 * allow the user to choose a contact using the Contacts app.
	 * 
	 * @param v
	 *            - the Button which was clicked.
	 */
	public void onChooseContactClick(View v) {
		Intent i = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
		startActivityForResult(i, PICK_CONTACT);
	}

	/*
	 * The method which filters the Intents sent back when an Activity is
	 * started for a result from this Activity. At present, it handles such
	 * Intents sent by onChooseContactClick. It uses the Intent to get a Cursor
	 * and extract the Contact's name, phone number, and email address, which it
	 * passes to populateContact.
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		switch (reqCode) {
		case PICK_CONTACT:
			if (resultCode == Activity.RESULT_OK) {
				Uri content = data.getData();
				Cursor c = managedQuery(content, null, null, null, null);

				if (c.moveToFirst()) {
					String rowId = c.getString(c.getColumnIndex(Contacts._ID));
					String[] rowIdArray = { rowId };
					String name = c.getString(c
							.getColumnIndex(Contacts.DISPLAY_NAME));
					String phone = "";
					if (Integer.parseInt(c.getString(c
							.getColumnIndex(Contacts.HAS_PHONE_NUMBER))) != 0) {
						Cursor phoneCursor = managedQuery(Phone.CONTENT_URI,
								null, Phone.CONTACT_ID + " = ?", rowIdArray,
								null);
						if (phoneCursor.moveToFirst()) {
							phone = phoneCursor.getString(phoneCursor
									.getColumnIndex(Phone.NUMBER));
						}
					}

					String email = "";
					Cursor emailCursor = managedQuery(Email.CONTENT_URI, null,
							Email.CONTACT_ID + " = ?", rowIdArray, null);
					if (emailCursor.moveToFirst()) {
						email = emailCursor.getString(emailCursor
								.getColumnIndexOrThrow(Email.DATA));
					}
					populateContact(name, phone, email);
				}
			}
		}
	}

	/**
	 * A method required to do further computation on the data extracted by
	 * onActivityResult. Each subclass has to implement this in order to make
	 * use of the data extracted from the Contact.
	 * 
	 * @param name
	 *            - the Contact's name
	 * @param phoneNumber
	 *            - the Contact's phone number
	 * @param email
	 *            - the Contact's email
	 */
	abstract protected void populateContact(String name, String phoneNumber,
			String email);

}