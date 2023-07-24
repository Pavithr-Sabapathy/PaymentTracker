package com.mashreq.paymentTracker.dto;

import com.mashreq.paymentTracker.service.ReportInput;

public class FlexDetailedReportInput implements ReportInput {

	private ReportComponentDTO component;
	private FederatedReportPromptDTO referenceNumPrompt;
	private FederatedReportPromptDTO accountingSourcePrompt;
	private FederatedReportPromptDTO debitAccountPrompt;
	
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
	public FederatedReportPromptDTO getAccountingSourcePrompt() {
		return accountingSourcePrompt;
	}
	public void setAccountingSourcePrompt(FederatedReportPromptDTO accountingSourcePrompt) {
		this.accountingSourcePrompt = accountingSourcePrompt;
	}
	public FederatedReportPromptDTO getDebitAccountPrompt() {
		return debitAccountPrompt;
	}
	public void setDebitAccountPrompt(FederatedReportPromptDTO debitAccountPrompt) {
		this.debitAccountPrompt = debitAccountPrompt;
	}
	@Override
	public String toString() {
		return "FlexAccountingDetailedFederatedReportInput [component=" + component + ", referenceNumPrompt="
				+ referenceNumPrompt + ", accountingSourcePrompt=" + accountingSourcePrompt + ", debitAccountPrompt="
				+ debitAccountPrompt + "]";
	}
	
}