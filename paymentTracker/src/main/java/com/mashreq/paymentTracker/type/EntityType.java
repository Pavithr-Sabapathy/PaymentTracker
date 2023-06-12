package com.mashreq.paymentTracker.type;

public enum EntityType {
	DATE("DATE"), NUMBER("NUMBER"), STRING("STRING");

	private String value;

	EntityType(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	public static EntityType getEntityType(String value) {
		if (EntityType.DATE.getValue().equals(value)) {
			return EntityType.DATE;
		} else if (EntityType.NUMBER.getValue().equals(value)) {
			return EntityType.NUMBER;
		} else if (EntityType.STRING.getValue().equals(value)) {
			return EntityType.STRING;
		}
		return null;
	}
}
