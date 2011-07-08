package com.blazingcontacts.BlazingContacts;

import java.util.ArrayList;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.util.Log;

/**
 * Simplified interface to the Android native contacts provider
 */
public class ContactsProviderWrapper {

	private int accountType;
	private Context parent;

	public ContactsProviderWrapper(Context parent, int accountType) {
		this.accountType = accountType;
		this.parent = parent;
	}

	/**
	 * Adds a new contact to the system's collection of contact information
	 * 
	 * @param newContact
	 *            Structure containing the new contact information
	 * @throws OperationApplicationException
	 * @throws RemoteException
	 */
	public void addContact(Contact newContact)
			throws OperationApplicationException, RemoteException {
		String[] columns = newContact.getColumns();
		String where = Data.DISPLAY_NAME + " = ?";
		String[] whereParameters = { newContact.getName() };
		Cursor contacts = parent.getContentResolver().query(Data.CONTENT_URI,
				columns, where, whereParameters, null);
		if (contacts.moveToFirst()) {
			Log.e("addContact", "Contact already exists");
			if (!newContact.hasPhoneNumber() && !newContact.hasEmail()) {
				return;
			}
			// Uri workUri = Uri.withAppendedPath(Data.CONTENT_URI, contacts
			// .getString(contacts.getColumnIndexOrThrow(Data._ID)));
			// ContentValues values = new ContentValues();
			// if (newContact.hasPhoneNumber()) {
			// values.put(Phone.NUMBER, newContact.getPhoneNumber());
			// }
			// String id = contacts.getString(contacts
			// .getColumnIndexOrThrow(Data._ID));
			where = Data.DISPLAY_NAME + " = ? AND " + Data.MIMETYPE + " = ?";
			ArrayList<ContentProviderOperation> ops;
			if (newContact.hasPhoneNumber()) {
				whereParameters = new String[] { newContact.getName(),
						Phone.CONTENT_ITEM_TYPE };
				ops = new ArrayList<ContentProviderOperation>();
				ops.add(ContentProviderOperation.newUpdate(Data.CONTENT_URI)
						.withSelection(where, whereParameters)
						.withValue(Phone.NUMBER, newContact.getPhoneNumber())
						.build());
				parent.getContentResolver().applyBatch(
						ContactsContract.AUTHORITY, ops);
			}
			if (newContact.hasEmail()) {
				whereParameters = new String[] { newContact.getName(),
						Email.CONTENT_ITEM_TYPE };
				ops = new ArrayList<ContentProviderOperation>();
				ops.add(ContentProviderOperation.newUpdate(Data.CONTENT_URI)
						.withSelection(where, whereParameters)
						.withValue(Email.DATA, newContact.getEmail()).build());
				parent.getContentResolver().applyBatch(
						ContactsContract.AUTHORITY, ops);
			}
			// if (newContact.hasEmail()) {
			// values.put(Email.DISPLAY_NAME, newContact.getEmail());
			// }
			// Log.e("ROWS UPDATED",
			// "N. "
			// + parent.getContentResolver().update(workUri,
			// values, null, null));
			// Toast.makeText(parent,
			// newContact.getName() + " is already in your contacts",
			// Toast.LENGTH_SHORT).show();
		} else {
			ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.RawContacts.CONTENT_URI)
					.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE,
							accountType)
					.withValue(ContactsContract.RawContacts.ACCOUNT_NAME,
							newContact.getName()).build()); // The name
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(ContactsContract.Data.MIMETYPE,
							StructuredName.CONTENT_ITEM_TYPE)
					.withValue(StructuredName.DISPLAY_NAME,
							newContact.getName()).build()); // The name field
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(ContactsContract.Data.MIMETYPE,
							Phone.CONTENT_ITEM_TYPE)
					.withValue(Phone.NUMBER, newContact.getPhoneNumber())
					.withValue(Phone.TYPE, Phone.TYPE_HOME).build()); // The
																		// phone
																		// number
																		// field
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(ContactsContract.Data.MIMETYPE,
							Email.CONTENT_ITEM_TYPE)
					.withValue(Email.DATA, newContact.getEmail())
					.withValue(Email.TYPE, Email.TYPE_HOME).build()); // The
																		// email
																		// field

			parent.getContentResolver().applyBatch(ContactsContract.AUTHORITY,
					ops);
		}
		contacts.close();
	}
}
