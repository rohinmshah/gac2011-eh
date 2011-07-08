package com.blazingcontacts.BlazingContacts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class MainActivity extends Activity {
	/** Called when the activity is first created. */
	Button startSession;
	Button joinSession;
	final String TAG = "Main Activity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Remove title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.main);

		startSession = (Button) findViewById(R.id.startButton);
		joinSession = (Button) findViewById(R.id.joinButton);

		startSession.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),
						StartActivity.class);
				startActivity(i);
			}
		});
		joinSession.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),
						JoinActivity.class);
				startActivity(i);
			}
		});

		// Testing update features:

		// try {
		// Cursor d = getContentResolver().query(Data.CONTENT_URI, null,
		// Data.DISPLAY_NAME + " = ? AND " + Data.MIMETYPE + " = ?",
		// new String[] { "Rohin Shah", Phone.CONTENT_ITEM_TYPE },
		// null);
		// d.moveToFirst();
		// for (int i = 0; i < d.getColumnCount(); i++) {
		// Log.e("Data Column " + i, d.getColumnName(i));
		// }
		// String no = d.getString(d.getColumnIndex(Phone.NUMBER));
		// Log.e("Rohin?", no == null ? "None" : no);
		// String id = d.getString(d.getColumnIndexOrThrow(Data._ID));
		// Log.e("ID", id);
		// d.close();
		// Cursor e = getContentResolver().query(Phone.CONTENT_URI, null,
		// null, null, null);
		// if (!e.moveToFirst()) {
		// Log.e("NOTHING", "Seems no phone data table");
		// return;
		// }
		// for (int i = 0; i < e.getColumnCount(); i++) {
		// Log.e("Phone Column " + i, e.getColumnName(i));
		// }
		// do {
		// Log.e("Phone Number",
		// e.getString(e.getColumnIndexOrThrow(Phone.NUMBER)));
		// Log.e("Phone Id",
		// e.getString(e.getColumnIndexOrThrow(Phone.CONTACT_ID)));
		// Log.e("Phone Raw Id", e.getString(e
		// .getColumnIndexOrThrow(Phone.RAW_CONTACT_ID)));
		// } while (e.moveToNext());
		// e.close();
		// Cursor c = getContentResolver().query(Data.CONTENT_URI, null,
		// where, whereParameters, null);
		// Log.e("TRY", "Got the cursor");
		// if (c.moveToFirst()) {
		// for (int i = 0; i < c.getColumnCount(); i++) {
		// String s = c.getString(i);
		// Log.e(c.getColumnName(i), s == null ? "NONE" : s);
		// }
		// lookupKey = c
		// .getString(c
		// .getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY));
		// Log.e("TRY", "Lookup key: " + lookupKey);
		// c.close();
		// } else {
		// Log.e("TRY", "Nothing found");
		// c.close();
		// return;
		// }
		// Uri contactUri = Uri.withAppendedPath(
		// ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
		// String where = Data.DISPLAY_NAME + " = ? AND " + Data.MIMETYPE
		// + " = ?";
		// String[] whereParameters = { "Test Name", Phone.CONTENT_ITEM_TYPE };
		// ArrayList<ContentProviderOperation> ops = new
		// ArrayList<ContentProviderOperation>();
		// ops.add(ContentProviderOperation.newUpdate(Data.CONTENT_URI)
		// .withSelection(where, whereParameters)
		// .withValue(Phone.NUMBER, "(444) 444-6666").build());
		// Log.e("TRY", "Applying the batch update");
		// getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
		// } catch (OperationApplicationException oae) {
		// Log.e("Exception", "Weird-OA", oae);
		// } catch (RemoteException re) {
		// Log.e("Exception", "Weird-R", re);
		// }

		// ContentResolver cr = getContentResolver();
		//
		// String id = null;
		// Cursor emailCursor = managedQuery(Email.CONTENT_URI, null, null,
		// null,
		// null);
		// Log.e("TRY", "TRYING");
		// if (emailCursor.moveToFirst()) {
		// do {
		// String email = emailCursor.getString(emailCursor
		// .getColumnIndexOrThrow(Email.DATA));
		// Log.e("EMAIL", email == null ? "NONE" : email);
		// } while (emailCursor.moveToNext());
		// }
		// if (id == null) {
		// Log.e("TRY", "email NOT FOUND");
		// return;
		// }
		//
		// Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
		// ContactsContract.Contacts._ID + " = ?", new String[] { id },
		// null);
		// while (cur.moveToNext()) {
		// Log.e("HERE", "HERE");
		// try {
		// String lookupKey = cur.getString(cur
		// .getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
		// Uri uri = Uri
		// .withAppendedPath(
		// ContactsContract.Contacts.CONTENT_LOOKUP_URI,
		// lookupKey);
		// System.out.println("The uri is " + uri.toString());
		// cr.delete(uri, null, null);
		// } catch (Exception e) {
		// System.out.println(e.getStackTrace());
		// }
		// }
	}
}