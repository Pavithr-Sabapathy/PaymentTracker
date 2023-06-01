package com.mashreq.paymentTracker.dto;

import com.mashreq.paymentTracker.utility.CheckType;

public class CannedReportPrompt {
	private static final long serialVersionUID = 1L;

	private Long id;
	private String promptKey;
	private String promptKeyDisplay;
	private String description;
	private String defaultValue;
	private Integer order;
	private CheckType required = CheckType.YES;
	private CannedReport cannedReport;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
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
	public CannedReport getCannedReport() {
		return cannedReport;
	}
	public void setCannedReport(CannedReport cannedReport) {
		this.cannedReport = cannedReport;
	}

}
