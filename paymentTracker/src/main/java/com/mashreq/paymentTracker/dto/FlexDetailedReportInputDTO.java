package com.mashreq.paymentTracker.dto;

import java.io.Serializable;

public class FlexDetailedReportInputDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1307810069865839003L;
	private FlexReportInputDTO referenceNumPrompt;
	private FlexReportInputDTO referenceTillPrompt;
	private FlexReportInputDTO accountingSourcePrompt;

	public FlexReportInputDTO getReferenceNumPrompt() {
		return referenceNumPrompt;
	}

	public void setReferenceNumPrompt(FlexReportInputDTO referenceNumPrompt) {
		this.referenceNumPrompt = referenceNumPrompt;
	}

	public FlexReportInputDTO getReferenceTillPrompt() {
		return referenceTillPrompt;
	}

	public void setReferenceTillPrompt(FlexReportInputDTO referenceTillPrompt) {
		this.referenceTillPrompt = referenceTillPrompt;
	}

	public FlexReportInputDTO getAccountingSourcePrompt() {
		return accountingSourcePrompt;
	}

	public void setAccountingSourcePrompt(FlexReportInputDTO accountingSourcePrompt) {
		this.accountingSourcePrompt = accountingSourcePrompt;
	}

}