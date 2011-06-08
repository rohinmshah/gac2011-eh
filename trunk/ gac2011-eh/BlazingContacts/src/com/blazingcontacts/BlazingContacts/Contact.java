package com.blazingcontacts.BlazingContacts;

/**
 * @author Rohin
 * 
 *         Represents a Contact, which could be the user.
 * 
 */
public class Contact {

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

}
