package com.blazingcontacts.BlazingContacts;

import java.util.Date;

public class WebTestWrapper {

	private int remainingTime = 20000;
	private int groupMax;
	private int members = 0;

	public WebTestWrapper(String newGroupName, String newPassword,
			Contact newContact) {
		groupMax = WebWrapper.NO_GROUP_MAX;
	}

	public WebTestWrapper(String newGroupName, String newPassword,
			Contact newContact, Date expiration, int groupMax) {
		this.groupMax = groupMax;
	}

	public GroupStatus getStatus() {
		remainingTime -= 1000;
		if (Math.random() > 0.2) {
			members++;
		}
		return new GroupStatus(new Date(remainingTime), groupMax, members);
	}

	public Contact[] downloadContactInfo() {
		Contact[] res = new Contact[members];
		for (int i = 0; i < members; i++) {
			res[i] = new Contact("Test" + i + " Name", "(823) 234-2985",
					"testname@gmail.com");
		}
		return res;
	}
}
