package com.mashreq.paymentTracker.constants;

import java.util.ArrayList;
import java.util.List;

public class MashreqFederatedReportConstants {
	public static final List<String> INCOMING_PAYMENT_CODES_LIST = new ArrayList<String>();
	public static final List<String> OUTGOING_PAYMENT_CODES_LIST = new ArrayList<String>();
	static {
		INCOMING_PAYMENT_CODES_LIST.add("O 103");
		INCOMING_PAYMENT_CODES_LIST.add("O 102");
		INCOMING_PAYMENT_CODES_LIST.add("O 202");
		INCOMING_PAYMENT_CODES_LIST.add("O 203");
		INCOMING_PAYMENT_CODES_LIST.add("O 200");

		OUTGOING_PAYMENT_CODES_LIST.add("I 103");
		OUTGOING_PAYMENT_CODES_LIST.add("I 102");
		OUTGOING_PAYMENT_CODES_LIST.add("I 202");
		OUTGOING_PAYMENT_CODES_LIST.add("I 203");
		OUTGOING_PAYMENT_CODES_LIST.add("I 200");

	}
}