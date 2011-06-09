package com.blazingcontacts.BlazingContacts;

import java.util.ArrayList;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;

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
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		ops.add(ContentProviderOperation
				.newInsert(ContactsContract.RawContacts.CONTENT_URI)
				.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE,
						accountType)
				.withValue(ContactsContract.RawContacts.ACCOUNT_NAME,
						newContact.getName()).build()); // The name
		ops.add(ContentProviderOperation
				.newInsert(ContactsContract.Data.CONTENT_URI)
				.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
				.withValue(ContactsContract.Data.MIMETYPE,
						StructuredName.CONTENT_ITEM_TYPE)
				.withValue(StructuredName.DISPLAY_NAME, newContact.getName())
				.build()); // The name field
		ops.add(ContentProviderOperation
				.newInsert(ContactsContract.Data.CONTENT_URI)
				.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
				.withValue(ContactsContract.Data.MIMETYPE,
						Phone.CONTENT_ITEM_TYPE)
				.withValue(Phone.NUMBER, newContact.getPhoneNumber())
				.withValue(Phone.TYPE, Phone.TYPE_HOME).build()); // The phone
																	// number
																	// field
		ops.add(ContentProviderOperation
				.newInsert(ContactsContract.Data.CONTENT_URI)
				.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
				.withValue(ContactsContract.Data.MIMETYPE,
						Email.CONTENT_ITEM_TYPE)
				.withValue(Email.DATA, newContact.getEmail())
				.withValue(Email.TYPE, Email.TYPE_HOME).build()); // The email
																	// field

		parent.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
	}

}