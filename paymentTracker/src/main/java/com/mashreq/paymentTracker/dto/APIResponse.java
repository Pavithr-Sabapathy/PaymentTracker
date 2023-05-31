package com.mashreq.paymentTracker.dto;

public class APIResponse {
	private Object data;
	private String message;
	private boolean status;

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "APIResponse [data=" + data + ", message=" + message + ", status=" + status + "]";
	}

}