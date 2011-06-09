package com.blazingcontacts.BlazingContacts;

/**
 * A normal operations server-size exception
 * @author Sam Pottinger
 */
public class ServerException extends Exception {
	
	ServerException(String strMessage){
		super(strMessage);
	}
	
}
