package com.mashreq.paymentTracker.dto;

import java.io.Serializable;

public class ReportPromptsInstanceDTO implements Serializable {
	
	private static final long serialVersionUID = 6276638738231877310L;
	private Long id;
	private Long reportId;
	private Long reportInstanceId;
	private PromptInstance prompt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getReportId() {
		return reportId;
	}

	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}

	public Long getReportInstanceId() {
		return reportInstanceId;
	}

	public void setReportInstanceId(Long reportInstanceId) {
		this.reportInstanceId = reportInstanceId;
	}

	public PromptInstance getPrompt() {
		return prompt;
	}

	public void setPrompt(PromptInstance prompt) {
		this.prompt = prompt;
	}

}
