package com.blazingcontacts.BlazingContacts;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.util.Log;
import android.view.View;

public abstract class GroupActivity extends Activity {

	private static final int PICK_CONTACT = 3;

	public void onChooseContactClick(View v) {
		Intent i = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
		startActivityForResult(i, PICK_CONTACT);
	}

	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		switch (reqCode) {
		case PICK_CONTACT:
			if (resultCode == Activity.RESULT_OK) {
				Uri content = data.getData();
				Cursor c = managedQuery(content, null, null, null, null);
				for (int i = 0; i < c.getColumnCount(); i++) {
					Log.i("Column " + i, c.getColumnName(i));
				}
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

	abstract protected void populateContact(String name, String phoneNumber,
			String email);

}