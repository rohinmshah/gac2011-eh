package com.blazingcontacts.BlazingContacts;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Contact with general personal information
 * @author Rohin
 */
public class Contact {
 
	private static final String JSON_NAME_ATTRIBUTE = "name";
	private static final String JSON_PHONE_ATTRIBUTE = "phone";
	private static final String JSON_EMAIL_ATTRIBUTE = "email";
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
	 * Creates a new contact by through the provided JSON object
	 * @param json JSON encoded string
	 * @return Contact instance from json
	 * @throws JSONException 
	 */
	public static Contact fromJSON(String json) throws JSONException {
		
		// Parse JSON
		JSONObject jsonObject = new JSONObject(json);
		String name = jsonObject.getString(JSON_NAME_ATTRIBUTE);
		String phoneNumber = jsonObject.getString(JSON_PHONE_ATTRIBUTE);
		String email = jsonObject.getString(JSON_EMAIL_ATTRIBUTE);
		
		
		return new Contact(name, phoneNumber, email);
	}

}
