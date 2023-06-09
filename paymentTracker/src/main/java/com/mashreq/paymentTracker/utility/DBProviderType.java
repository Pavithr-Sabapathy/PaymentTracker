package com.mashreq.paymentTracker.utility;

public enum DBProviderType {

	Oracle(1L), MySql(2L), MSSql(3L), DB2(4L), POSTGRES(5L);

	private Long value;

	DBProviderType(Long value) {
		this.value = value;
	}

	public Long getValue() {
		return this.value;
	}

}
