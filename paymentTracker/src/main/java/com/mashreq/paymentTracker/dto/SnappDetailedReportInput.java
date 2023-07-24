package com.mashreq.paymentTracker.dto;

import com.mashreq.paymentTracker.service.ReportInput;

public class SnappDetailedReportInput implements ReportInput {
	private ReportComponentDTO component;
	private FederatedReportPromptDTO referenceNumPrompt;

	public ReportComponentDTO getComponent() {
		return component;
	}

	public void setComponent(ReportComponentDTO component) {
		this.component = component;
	}

	public FederatedReportPromptDTO getReferenceNumPrompt() {
		return referenceNumPrompt;
	}

	public void setReferenceNumPrompt(FederatedReportPromptDTO referenceNumPrompt) {
		this.referenceNumPrompt = referenceNumPrompt;
	}

	public SnappDetailedReportInput() {
	}

	public SnappDetailedReportInput(ReportComponentDTO component, FederatedReportPromptDTO referenceNumPrompt) {
		super();
		this.component = component;
		this.referenceNumPrompt = referenceNumPrompt;
	}

	@Override
	public String toString() {
		return "SnappDetailedReportInput [component=" + component + ", referenceNumPrompt=" + referenceNumPrompt + "]";
	}

}