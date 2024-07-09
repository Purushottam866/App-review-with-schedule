package com.linkedinAppReview.exception;

public class BadRequestException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String message;

	public BadRequestException(String message) {
		this.message = message;
	}
}
