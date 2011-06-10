package com.blazingcontacts.BlazingContacts;

import java.util.Date;

/**
 * Represents the status of the group - the remaining time in terms of time
 * and/or number of people in the group.
 * 
 * @author Rohin
 */
public class GroupStatus {

	private Date remainingTime;
	private int groupMax, memberCount;
	private boolean finished;

	/**
	 * Creates a new GroupStatus.
	 * 
	 * @param remainingTime
	 * @param groupMax
	 * @param memberCount
	 */
	public GroupStatus(Date remainingTime, int groupMax, int memberCount) {
		this.remainingTime = remainingTime;
		this.groupMax = groupMax;
		this.memberCount = memberCount;
	}

	/**
	 * Returns the time remaining before the group expires.
	 * 
	 * @return - the time remaining before the group expires.
	 */
	public Date getRemainingTime() {
		return remainingTime;
	}

	/**
	 * Returns the maximum number of people that can join the group.
	 * 
	 * @return - the maximum number of people that can join the group.
	 */
	public int getGroupMax() {
		return groupMax;
	}

	/**
	 * Returns the current number of people in the group.
	 * 
	 * @return - the current number of people in the group.
	 */
	public int getMemberCount() {
		return memberCount;
	}

	/**
	 * Returns true if the group has expired, which is when the group is
	 * completely filled (or filled above capacity, in case of an error in the
	 * web application), or if there is no remaining time.
	 * 
	 * @return - true if the group has expired
	 */
	public boolean isFinished() {
		return (groupMax <= memberCount) || remainingTime.getTime() <= 0;
	}
}
