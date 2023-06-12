package com.mashreq.paymentTracker.type;

public enum ExecutionStatusType {
	SCHEDULED("S"), INPROGRESS("I"), COMPLETED("CO"), CANCELLED("CA");

	private String value;

	ExecutionStatusType(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}
}
