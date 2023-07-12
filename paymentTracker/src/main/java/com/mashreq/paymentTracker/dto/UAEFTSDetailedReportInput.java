package com.mashreq.paymentTracker.dto;

import java.io.Serializable;

public class UAEFTSDetailedReportInput implements Serializable {
	private static final long serialVersionUID = -106491101338402603L;
	private ReportComponentDTO component;
	private FederatedReportPromptDTO referenceNumPrompt;
	private FederatedReportPromptDTO mesgTypePrompt;

	public UAEFTSDetailedReportInput() {
	}

	public UAEFTSDetailedReportInput(ReportComponentDTO component, FederatedReportPromptDTO referenceNumPrompt,
			FederatedReportPromptDTO mesgTypePrompt) {
		super();
		this.component = component;
		this.referenceNumPrompt = referenceNumPrompt;
		this.mesgTypePrompt = mesgTypePrompt;
	}

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

	public FederatedReportPromptDTO getMesgTypePrompt() {
		return mesgTypePrompt;
	}

	public void setMesgTypePrompt(FederatedReportPromptDTO mesgTypePrompt) {
		this.mesgTypePrompt = mesgTypePrompt;
	}

	@Override
	public String toString() {
		return "UAEFTSDetailedReportInput [component=" + component + ", referenceNumPrompt=" + referenceNumPrompt
				+ ", mesgTypePrompt=" + mesgTypePrompt + "]";
	}

}