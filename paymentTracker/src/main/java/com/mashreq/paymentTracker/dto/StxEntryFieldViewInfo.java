package com.mashreq.paymentTracker.dto;

import java.io.Serializable;

public class StxEntryFieldViewInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3381216725411081151L;
	private String code;
	private String option;
	private String expression;
	private Integer version;

	public StxEntryFieldViewInfo(String code, String option, String expression, Integer version) {
		super();
		this.code = code;
		this.option = option;
		this.expression = expression;
		this.version = version;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

}
