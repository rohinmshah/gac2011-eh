package com.blazingcontacts.BlazingContacts;

import org.json.JSONException;
import org.json.JSONObject;

import android.provider.ContactsContract;

/**
 * Contact with general personal information
 * 
 * @author Rohin
 */
public class Contact {

	private static final String JSON_NAME_ATTRIBUTE = "name";
	private static final String JSON_PHONE_ATTRIBUTE = "phone";
	private static final String JSON_EMAIL_ATTRIBUTE = "email";
	private static final String JSON_DATA_ATTRIBUTE = "data";
	private String name, phoneNumber, email;

	/**
	 * Creates a new Contact.
	 * 
	 * @param name
	 *            - the name of the Contact.
	 * @param phoneNumber
	 *            - the phone number of the Contact.
	 * @param email
	 *            - the email address of the Contact.
	 */
	public Contact(String name, String phoneNumber, String email) {
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.email = email;
	}

	/**
	 * @return the name of this Contact
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the phone number of this Contact
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @return the email address of this Contact
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Returns a JSON encoded version of this contact
	 * 
	 * @return A JSON encoded string representation of this object
	 * @throws JSONException
	 */
	public String toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(JSON_NAME_ATTRIBUTE, name);
		json.put(JSON_PHONE_ATTRIBUTE, phoneNumber);
		json.put(JSON_EMAIL_ATTRIBUTE, email);
		return json.toString();
	}

	/**
	 * At present, this method always returns the same array. However, in the
	 * future if more data fields are allowed, it may be changed to return only
	 * those columns for which this Contact holds data.
	 * 
	 * @return - the string array which can be used as a projection in an SQL
	 *         query to find only the required columns.
	 */
	public String[] getColumns() {
		return new String[] { ContactsContract.Data.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.NUMBER,
				ContactsContract.CommonDataKinds.Email.DISPLAY_NAME };
	}

	/**
	 * Creates a new contact by through the provided JSON encoded string
	 * 
	 * @param json
	 *            JSON encoded string
	 * @return Contact instance from json
	 * @throws JSONException
	 */
	public static Contact fromJSON(String json) throws JSONException {

		// Parse JSON
		JSONObject jsonObject = new JSONObject(json);
		return fromJSONObject(jsonObject);
	}

	/**
	 * Creates a new contact by through the provided JSON object
	 * 
	 * @param jsonObject
	 *            JSON decoded object
	 * @return Contact instance from json
	 * @throws JSONException
	 * @throws JSONException
	 */
	public static Contact fromJSONObject(JSONObject jsonObject)
			throws JSONException {
		JSONObject contact = new JSONObject(
				jsonObject.getString(JSON_DATA_ATTRIBUTE));
		String name = contact.getString(JSON_NAME_ATTRIBUTE);
		String phoneNumber = contact.getString(JSON_PHONE_ATTRIBUTE);
		String email = contact.getString(JSON_EMAIL_ATTRIBUTE);

		return new Contact(name, phoneNumber, email);
	}

}
