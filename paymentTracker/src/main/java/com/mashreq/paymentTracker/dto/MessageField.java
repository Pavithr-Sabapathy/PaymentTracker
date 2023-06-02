package com.mashreq.paymentTracker.dto;

public class MessageField {
	private String fieldCode;
	private String fieldOption;
	private String fieldValue;
	private String fieldExpression;

	public MessageField(String fieldCode, String fieldOption, String fieldValue) {
		super();
		this.fieldCode = fieldCode;
		this.fieldOption = fieldOption;
		this.fieldValue = fieldValue;
	}

	public String getFieldCode() {
		return fieldCode;
	}

	public void setFieldCode(String fieldCode) {
		this.fieldCode = fieldCode;
	}

	public String getFieldOption() {
		return fieldOption;
	}

	public void setFieldOption(String fieldOption) {
		this.fieldOption = fieldOption;
	}

	public String getFieldValue() {
		return fieldValue;
	}

	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}

	public String getFieldExpression() {
		return fieldExpression;
	}

	public void setFieldExpression(String fieldExpression) {
		this.fieldExpression = fieldExpression;
	}

}
