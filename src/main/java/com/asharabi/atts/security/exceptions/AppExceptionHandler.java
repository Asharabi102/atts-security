package com.asharabi.atts.security.exceptions;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.asharabi.atts.security.exception.model.ErrorMessage;

@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(value = { Exception.class })
	public ResponseEntity<Object> handleAnyException(Exception ex, WebRequest request) throws Exception {
		return super.handleException(ex, request);
	}

	@ExceptionHandler(value = { UserNotFoundException.class })
	public ResponseEntity<Object> userNotFoundException(Exception ex, WebRequest request,
			HttpServletRequest httpRequest) {
		ErrorMessage errorMessage = new ErrorMessage(new Date(), HttpStatus.NOT_FOUND.value(), ex.getLocalizedMessage(),
				ex.toString(), httpRequest.getRequestURI());
		return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(value = { AlreadyExistsException.class })
	public ResponseEntity<Object> alreadyExist(Exception ex, WebRequest request, HttpServletRequest httpRequest) {
		ErrorMessage errorMessage = new ErrorMessage(new Date(), HttpStatus.CONFLICT.value(), ex.getLocalizedMessage(),
				ex.toString(), httpRequest.getRequestURI());
		return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.CONFLICT);
	}

	@ExceptionHandler(value = { UsernameNotAllowed.class })
	public ResponseEntity<Object> usernameNotAllowed(Exception ex, WebRequest request, HttpServletRequest httpRequest) {
		ErrorMessage errorMessage = new ErrorMessage(new Date(), HttpStatus.FORBIDDEN.value(), ex.getLocalizedMessage(),
				ex.toString(), httpRequest.getRequestURI());
		return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.FORBIDDEN);
	}

}