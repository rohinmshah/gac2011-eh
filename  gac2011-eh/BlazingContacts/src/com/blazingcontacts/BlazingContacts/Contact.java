package com.blazingcontacts.BlazingContacts;

/**
 * @author Rohin
 * 
 *         Represents a Contact, which could be the user.
 * 
 */
public class Contact {

	private String firstName, lastName, phoneNumber, email;

	/**
	 * Creates a new Contact.
	 * 
	 * @param firstName
	 *            - the first name of the Contact.
	 * @param lastName
	 *            - the last name of the Contact.
	 * @param phoneNumber
	 *            - the phone number of the Contact.
	 * @param email
	 *            - the email address of the Contact.
	 */
	public Contact(String firstName, String lastName, String phoneNumber,
			String email) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
		this.email = email;
	}

	/**
	 * @return the first name of this Contact
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @return the last name of this Contact
	 */
	public String getLastName() {
		return lastName;
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
