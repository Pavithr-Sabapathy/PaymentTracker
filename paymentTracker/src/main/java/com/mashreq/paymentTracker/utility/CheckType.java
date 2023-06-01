package com.mashreq.paymentTracker.utility;

public enum CheckType {
	NO('N'), YES('Y');

	private Character value;

	CheckType(Character value) {
		this.value = value;
	}

	public Character getValue() {
		return this.value;
	}

}
