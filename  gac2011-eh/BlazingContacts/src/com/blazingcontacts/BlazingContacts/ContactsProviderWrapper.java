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
import android.provider.ContactsContract.RawContacts;
import android.util.Log;

/**
 * Simplified interface to the Android native contacts provider
 */
public class ContactsProviderWrapper {

	private int accountType;
	private Context mContext;

	public ContactsProviderWrapper(Context context, int accountType) {
		this.accountType = accountType;
		this.mContext = context;
	}

	/**
	 * Adds the new contact to Contacts. Returns true if a new contact was
	 * added, and false if one was updated.
	 * 
	 * @param newContact
	 *            - Structure containing the new contact information.
	 * @return - true if a contact was added, false if it was updated.
	 * @throws OperationApplicationException
	 * @throws RemoteException
	 */
	public boolean addContact(Contact newContact)
			throws OperationApplicationException, RemoteException {
		// First, see if the contact already exists, by searching for any
		// contacts with the same display name.
		String[] columns = newContact.getColumns();
		String where = Data.DISPLAY_NAME + " = ?";
		String[] whereParameters = { newContact.getName() };
		Cursor contacts = mContext.getContentResolver().query(Data.CONTENT_URI,
				columns, where, whereParameters, null);

		if (contacts.moveToFirst()) {
			// This means that there was already a contact with the given
			// display name. So, update the phone number and email.
			Log.i("addContact", "Contact already exists");
			if (!newContact.hasPhoneNumber() && !newContact.hasEmail()) {
				contacts.close();
				return false;
			}

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
				mContext.getContentResolver().applyBatch(
						ContactsContract.AUTHORITY, ops);
			}

			if (newContact.hasEmail()) {
				whereParameters = new String[] { newContact.getName(),
						Email.CONTENT_ITEM_TYPE };
				ops = new ArrayList<ContentProviderOperation>();
				ops.add(ContentProviderOperation.newUpdate(Data.CONTENT_URI)
						.withSelection(where, whereParameters)
						.withValue(Email.DATA, newContact.getEmail()).build());
				mContext.getContentResolver().applyBatch(
						ContactsContract.AUTHORITY, ops);
			}
			contacts.close();
			return false;
		} else {
			// Create a new entry in the contacts database

			ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
			ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
					.withValue(RawContacts.ACCOUNT_TYPE, accountType)
					.withValue(RawContacts.ACCOUNT_NAME, newContact.getName())
					.build()); // The name

			// The name field
			ops.add(ContentProviderOperation
					.newInsert(Data.CONTENT_URI)
					.withValueBackReference(Data.RAW_CONTACT_ID, 0)
					.withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
					.withValue(StructuredName.DISPLAY_NAME,
							newContact.getName()).build());

			// Phone number field
			ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
					.withValueBackReference(Data.RAW_CONTACT_ID, 0)
					.withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
					.withValue(Phone.NUMBER, newContact.getPhoneNumber())
					.withValue(Phone.TYPE, Phone.TYPE_HOME).build());

			// The email field
			ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
					.withValueBackReference(Data.RAW_CONTACT_ID, 0)
					.withValue(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE)
					.withValue(Email.DATA, newContact.getEmail())
					.withValue(Email.TYPE, Email.TYPE_HOME).build());

			mContext.getContentResolver().applyBatch(
					ContactsContract.AUTHORITY, ops);
			contacts.close();
			return true;
		}
	}
}
