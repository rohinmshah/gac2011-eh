package com.blazingcontacts.BlazingContacts;

import java.util.Date;

public class WebTestWrapper {

	private int remainingTime = 20000;
	private int groupMax;
	private int members = 0;
	private Contact mContact;

	public WebTestWrapper(String newGroupName, String newPassword,
			Contact newContact) {
		groupMax = WebWrapper.NO_GROUP_MAX;
		mContact = newContact;
	}

	public WebTestWrapper(String newGroupName, String newPassword,
			Contact newContact, Date expiration, int groupMax) {
		this.groupMax = groupMax;
		mContact = newContact;
	}

	public GroupStatus getStatus() {
		remainingTime -= 1000;
		if (Math.random() > 0.5) {
			members++;
		}
		return new GroupStatus(new Date(remainingTime), groupMax, members);
	}

	public Contact[] downloadContactInfo() {
		// Contact[] res = new Contact[members];
		// for (int i = 0; i < members; i++) {
		// res[i] = new Contact("Test Name", "9224183954",
		// "testname@gmail.com");
		// }
		// return res;
		return new Contact[] {
				new Contact("Test Name", "", "testname@gmail.com"),
				new Contact("Test Name", "9224183954", "testname@gmail.com"),
				new Contact("Test Name", "", "testname@yahoo.com") };
	}
}
