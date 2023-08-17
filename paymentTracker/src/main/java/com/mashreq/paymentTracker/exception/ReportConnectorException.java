package com.mashreq.paymentTracker.exception;

public class ReportConnectorException extends Exception {

	private static final long serialVersionUID = -1486331368138873436L;
	/** */
	public int code = ExceptionCodes.DEFAULT_EXCEPTION_CODE;

	public ReportConnectorException(int exceptionCode, String message) {
		super(message);
		this.code = exceptionCode;
	}

	public ReportConnectorException(int exceptionCode, String message, Throwable cause) {
		super(message, cause);
		this.code = exceptionCode;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

}