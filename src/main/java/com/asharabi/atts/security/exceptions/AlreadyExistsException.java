package com.asharabi.atts.security.exceptions;

public class AlreadyExistsException extends RuntimeException {
	
	private static final long serialVersionUID = 3821043153829603415L;

	public AlreadyExistsException(String message) {
        super(message);
    }
	
}