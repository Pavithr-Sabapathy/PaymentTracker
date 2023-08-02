package com.mashreq.paymentTracker.type;

public enum MessageType {

	INCOMING("INCOMING"), OUTGOING("OUTGOING"), INCOMING_ENQUIRY("IENQUIRY"), OUTGOING_ENQUIRY("OENQUIRY"),
	TRCH_INCOMING_GPI("TRCH_IGPI"), IPALA_INCOMING_GPI("IPALA_IGPI"), TRCH_OUTGOING_GPI("TRCH_OGPI"),
	IPALA_OUTGOING_GPI("IPALA_OGPI");

	private String value;

	MessageType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
