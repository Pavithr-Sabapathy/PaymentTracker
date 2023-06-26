package com.mashreq.paymentTracker;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ApiError {
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
	private LocalDateTime timestamp;
	private HttpStatus status;
	private String message;
	private List errors;

	public ApiError(LocalDateTime timestamp, HttpStatus status, String message, List errors) {
		super();
		this.timestamp = timestamp;
		this.status = status;
		this.message = message;
		this.errors = errors;
	}

	public ApiError(HttpStatus badRequest, String localizedMessage, String error) {
		// TODO Auto-generated constructor stub
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List getErrors() {
		return errors;
	}

	public void setErrors(List errors) {
		this.errors = errors;
	}

	@Override
	public String toString() {
		return "ApiError [timestamp=" + timestamp + ", status=" + status + ", message=" + message + ", errors=" + errors
				+ "]";
	}

}