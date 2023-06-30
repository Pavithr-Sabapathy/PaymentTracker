package com.mashreq.paymentTracker.type;

public enum EncryptionAlgorithm {
	DES("DES"), TRIPLE_DES("DESede");
	;

	private String value;
	private static String name = EncryptionAlgorithm.class.getSimpleName();

	EncryptionAlgorithm(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

}
