package com.mashreq.paymentTracker.type;

public enum EDMSProcessType {
	FTO("FTO"), RID("RID"), EDD("EDD");

	private String value;

	EDMSProcessType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
