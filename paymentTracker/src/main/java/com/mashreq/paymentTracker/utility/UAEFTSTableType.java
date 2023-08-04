package com.mashreq.paymentTracker.utility;

public enum UAEFTSTableType {
	MTINPUTMSGS("MTINPUTMSGS"), MANUALMSGS("MANUALMSGS"), MT202INPUTMSGS("MT202INPUTMSGS"), MT202("MT202"),
	FTSMSGS("FTSMSGS"), INCOMING_MTFN("INCOMING_MTFN"), MTQA("MTQA");

	private String value;

	UAEFTSTableType(String string) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
