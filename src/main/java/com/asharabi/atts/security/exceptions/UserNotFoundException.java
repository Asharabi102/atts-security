package com.asharabi.atts.security.exceptions;

public class UserNotFoundException extends Exception {
	
	private static final long serialVersionUID = -1577200280626967311L;

	public UserNotFoundException(String message) {
		super(message);
	}
}