package com.mashreq.paymentTracker.exception;

public class BaseException extends java.lang.Exception {

	private static final long serialVersionUID = 1L;

	public BaseException(int exceptionCode, String message) {
		super(message);
		this.code = exceptionCode;
	}

	public BaseException(int exceptionCode, String message, Throwable cause) {
		super(message, cause);
		this.code = exceptionCode;
	}

	public BaseException(int exceptionCode, Throwable cause) {
		super(cause);
		this.code = exceptionCode;
	}

	/** */
	public int code = ExceptionCodes.DEFAULT_EXCEPTION_CODE;

	/** */
	public int getCode() {
		return this.code;
	}
}
