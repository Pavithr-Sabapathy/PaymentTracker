package com.mashreq.paymentTracker.utility;

public enum SWIFTDetailedReportType {

	RMESG("RMESG"), RINTV("RINTV"), NA("NA");

	private String value;

	SWIFTDetailedReportType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}