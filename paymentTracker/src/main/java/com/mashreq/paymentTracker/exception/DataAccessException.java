package com.mashreq.paymentTracker.exception;

public class DataAccessException extends BaseException {

	private static final long serialVersionUID = 1195866424500237737L;

	public DataAccessException(int exceptionCode, String message) {
		super(exceptionCode, message);
	}

	public DataAccessException(int exceptionCode, String message, Throwable cause) {
		super(exceptionCode, message, cause);
	}

	public DataAccessException(int exceptionCode, Throwable cause) {
		super(exceptionCode, cause);
	}

}
