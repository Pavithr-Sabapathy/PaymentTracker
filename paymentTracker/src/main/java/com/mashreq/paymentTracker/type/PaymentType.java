package com.mashreq.paymentTracker.type;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum PaymentType {
	OUTWARD("OUTWARD"), INWARD("INWARD"), INWARD_RESULTING_INTO_OUTWARD("IRIO");

	private String value;
	private static final Map<String, PaymentType> reverseLookupMap = new HashMap<String, PaymentType>();

	static {
		for (PaymentType paymentType : EnumSet.allOf(PaymentType.class)) {
			reverseLookupMap.put(paymentType.value, paymentType);
		}
	}

	PaymentType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static PaymentType getType(String value) {
		return reverseLookupMap.get(value);
	}

}
