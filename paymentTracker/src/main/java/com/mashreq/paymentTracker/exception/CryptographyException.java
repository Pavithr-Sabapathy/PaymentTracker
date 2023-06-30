package com.mashreq.paymentTracker.exception;

public class CryptographyException extends Exception {

	private static final long serialVersionUID = 1L;

	/** */
	public int code = 10000;

	/** */
	public int getCode() {
		return this.code;
	}

	public CryptographyException(int exceptionCode, String message) {
		super(message);
		this.code = exceptionCode;
	}

	public CryptographyException(int exceptionCode, String message, Throwable cause) {
		super(message, cause);
		this.code = exceptionCode;
	}

	public CryptographyException(int exceptionCode, Throwable cause) {
		super(cause);
		this.code = exceptionCode;

	}

}
