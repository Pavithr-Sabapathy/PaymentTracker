package com.mashreq.paymentTracker.type;

public enum PromptValueType {
	ENTITY("E"), VALUE("V"), DATE("D");

	private String value;

	PromptValueType(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

}
