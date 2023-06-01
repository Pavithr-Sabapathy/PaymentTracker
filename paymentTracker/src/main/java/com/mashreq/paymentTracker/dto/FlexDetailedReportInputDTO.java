package com.mashreq.paymentTracker.dto;

import java.io.Serializable;

public class FlexDetailedReportInputDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1307810069865839003L;
	private FederatedReportPromptDTO referenceNumPrompt;
	private FederatedReportPromptDTO referenceTillPrompt;
	private FederatedReportPromptDTO accountingSourcePrompt;
	public FederatedReportPromptDTO getReferenceNumPrompt() {
		return referenceNumPrompt;
	}
	public void setReferenceNumPrompt(FederatedReportPromptDTO referenceNumPrompt) {
		this.referenceNumPrompt = referenceNumPrompt;
	}

	public FederatedReportPromptDTO getReferenceTillPrompt() {
		return referenceTillPrompt;
	}
	public void setReferenceTillPrompt(FederatedReportPromptDTO referenceTillPrompt) {
		this.referenceTillPrompt = referenceTillPrompt;
	}
	public FederatedReportPromptDTO getAccountingSourcePrompt() {
		return accountingSourcePrompt;
	}
	public void setAccountingSourcePrompt(FederatedReportPromptDTO accountingSourcePrompt) {
		this.accountingSourcePrompt = accountingSourcePrompt;
	}

	
}