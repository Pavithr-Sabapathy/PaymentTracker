package com.mashreq.paymentTracker.dto;

import java.io.Serializable;

public class MOLDetailedFederatedReportInput implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8822362397030006622L;

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

	public MOLDetailedFederatedReportInput(ReportComponentDTO component,
			FederatedReportPromptDTO referenceNumPrompt) {
		super();
		this.component = component;
		this.referenceNumPrompt = referenceNumPrompt;
	}

	
	public MOLDetailedFederatedReportInput() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "MOLDetailedFederatedReportInput [component=" + component + ", referenceNumPrompt=" + referenceNumPrompt
				+ "]";
	}

}
