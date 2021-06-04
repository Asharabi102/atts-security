package com.asharabi.atts.security.exceptions;

public class UsernameNotAllowed extends Exception {
    
	private static final long serialVersionUID = 1921447471095092544L;

	public UsernameNotAllowed(String message) {
        super(message);
    }
}