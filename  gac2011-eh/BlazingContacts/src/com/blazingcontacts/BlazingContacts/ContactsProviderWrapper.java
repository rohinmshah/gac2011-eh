package com.blazingcontacts.BlazingContacts;

import java.util.ArrayList;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;

public class ContactsProviderWrapper {

	private ContentResolver contactResolver;
	private int accountType;

	public ContactsProviderWrapper(ContentResolver resolver, int accountType) {
		contactResolver = resolver;
		this.accountType = accountType;
	}

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

		contactResolver.applyBatch(ContactsContract.AUTHORITY, ops);
	}
}
