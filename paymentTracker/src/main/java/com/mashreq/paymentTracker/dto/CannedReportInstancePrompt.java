package com.mashreq.paymentTracker.dto;

import com.mashreq.paymentTracker.utility.CheckType;

public class CannedReportInstancePrompt {

	private Long id;
	private Long promptId;
	private String promptKey;
	private String promptKeyDisplay;
	private String value;
	private String valueDisplay;
	private Integer order;
	private CheckType required;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPromptId() {
		return promptId;
	}

	public void setPromptId(Long promptId) {
		this.promptId = promptId;
	}

	public String getPromptKey() {
		return promptKey;
	}

	public void setPromptKey(String promptKey) {
		this.promptKey = promptKey;
	}

	public String getPromptKeyDisplay() {
		return promptKeyDisplay;
	}

	public void setPromptKeyDisplay(String promptKeyDisplay) {
		this.promptKeyDisplay = promptKeyDisplay;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValueDisplay() {
		return valueDisplay;
	}

	public void setValueDisplay(String valueDisplay) {
		this.valueDisplay = valueDisplay;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public CheckType getRequired() {
		return required;
	}

	public void setRequired(CheckType required) {
		this.required = required;
	}

}
