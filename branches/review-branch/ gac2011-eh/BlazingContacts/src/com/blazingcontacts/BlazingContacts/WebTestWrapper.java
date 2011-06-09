package com.blazingcontacts.BlazingContacts;

import java.util.Date;

public class WebTestWrapper {

	private int remainingTime = 30000;
	private int groupMax;
	private int members = 0;

	public WebTestWrapper(String newGroupName, String newPassword,
			Contact newContact) {
		groupMax = 20;
	}

	public WebTestWrapper(String newGroupName, String newPassword,
			Contact newContact, Date expiration, int groupMax) {
		this.groupMax = groupMax;
	}

	public GroupStatus getStatus() {
		remainingTime -= 1000;
		if (Math.random() > 0.5) {
			members++;
		}
		return new GroupStatus(new Date(remainingTime), groupMax, members);
	}

	public Contact[] downloadContactInfo() {
		return new Contact[] { new Contact("Test Name", "(814) 145-1435",
				"test.name@gmail.com") };
	}
}
