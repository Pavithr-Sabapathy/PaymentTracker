package com.mashreq.paymentTracker.dto;

public class SWIFTMessageDetailsReportOutput extends ReportBaseOutput {

	private String key;
	private String value;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "SWIFTMessageDetailsFederatedReportOutput [key=" + key + ", value=" + value + "]";
	}

}
