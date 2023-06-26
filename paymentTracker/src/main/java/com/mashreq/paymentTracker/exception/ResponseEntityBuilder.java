package com.mashreq.paymentTracker.exception;

import org.springframework.http.ResponseEntity;

import com.mashreq.paymentTracker.ApiError;

public class ResponseEntityBuilder {
	public static ResponseEntity<Object> build(ApiError apiError) {
		return new ResponseEntity<>(apiError, apiError.getStatus());
	}
}