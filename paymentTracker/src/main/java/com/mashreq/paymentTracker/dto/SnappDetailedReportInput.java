package com.mashreq.paymentTracker.dto;

import java.io.Serializable;

public class SnappDetailedReportInput implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2126503157099389476L;
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