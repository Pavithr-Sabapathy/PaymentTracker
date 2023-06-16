package com.mashreq.paymentTracker.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdvanceSearchReportInput implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3961694452431306972L;
	private FederatedReportPromptDTO accountNumPrompt;
	private FederatedReportPromptDTO fromDatePrompt;
	private FederatedReportPromptDTO toDatePrompt;
	private FederatedReportPromptDTO currencyPrompt;
	private FederatedReportPromptDTO amountBetweenPrompt;
	private FederatedReportPromptDTO amountToPrompt;
	private FederatedReportPromptDTO transactionStatus;
	private FederatedReportPromptDTO transactionRefNum;

	private ReportComponentDTO flexComponent;
	private ReportComponentDTO matrixComponent;
	private ReportComponentDTO edmsComponent;
	private ReportComponentDTO uaeftsComponent;
	private Map<String, AdvanceSearchReportOutput> flexMatrixBasedUaeftsTransactions;
	private List<AdvanceSearchReportOutput> failedSystemOutputs = new ArrayList<AdvanceSearchReportOutput>();
	private Map<String, AdvanceSearchReportOutput> otherTransactions;

	public FederatedReportPromptDTO getAccountNumPrompt() {
		return accountNumPrompt;
	}

	public void setAccountNumPrompt(FederatedReportPromptDTO accountNumPrompt) {
		this.accountNumPrompt = accountNumPrompt;
	}

	public FederatedReportPromptDTO getFromDatePrompt() {
		return fromDatePrompt;
	}

	public void setFromDatePrompt(FederatedReportPromptDTO fromDatePrompt) {
		this.fromDatePrompt = fromDatePrompt;
	}

	public FederatedReportPromptDTO getToDatePrompt() {
		return toDatePrompt;
	}

	public void setToDatePrompt(FederatedReportPromptDTO toDatePrompt) {
		this.toDatePrompt = toDatePrompt;
	}

	public FederatedReportPromptDTO getCurrencyPrompt() {
		return currencyPrompt;
	}

	public void setCurrencyPrompt(FederatedReportPromptDTO currencyPrompt) {
		this.currencyPrompt = currencyPrompt;
	}

	public FederatedReportPromptDTO getAmountBetweenPrompt() {
		return amountBetweenPrompt;
	}

	public void setAmountBetweenPrompt(FederatedReportPromptDTO amountBetweenPrompt) {
		this.amountBetweenPrompt = amountBetweenPrompt;
	}

	public FederatedReportPromptDTO getAmountToPrompt() {
		return amountToPrompt;
	}

	public void setAmountToPrompt(FederatedReportPromptDTO amountToPrompt) {
		this.amountToPrompt = amountToPrompt;
	}

	public FederatedReportPromptDTO getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(FederatedReportPromptDTO transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public FederatedReportPromptDTO getTransactionRefNum() {
		return transactionRefNum;
	}

	public void setTransactionRefNum(FederatedReportPromptDTO transactionRefNum) {
		this.transactionRefNum = transactionRefNum;
	}

	public ReportComponentDTO getFlexComponent() {
		return flexComponent;
	}

	public void setFlexComponent(ReportComponentDTO flexComponent) {
		this.flexComponent = flexComponent;
	}

	public ReportComponentDTO getMatrixComponent() {
		return matrixComponent;
	}

	public void setMatrixComponent(ReportComponentDTO matrixComponent) {
		this.matrixComponent = matrixComponent;
	}

	public ReportComponentDTO getEdmsComponent() {
		return edmsComponent;
	}

	public void setEdmsComponent(ReportComponentDTO edmsComponent) {
		this.edmsComponent = edmsComponent;
	}

	public ReportComponentDTO getUaeftsComponent() {
		return uaeftsComponent;
	}

	public void setUaeftsComponent(ReportComponentDTO uaeftsComponent) {
		this.uaeftsComponent = uaeftsComponent;
	}

	public Map<String, AdvanceSearchReportOutput> getFlexMatrixBasedUaeftsTransactions() {
		return flexMatrixBasedUaeftsTransactions;
	}

	public void setFlexMatrixBasedUaeftsTransactions(
			Map<String, AdvanceSearchReportOutput> flexMatrixBasedUaeftsTransactions) {
		this.flexMatrixBasedUaeftsTransactions = flexMatrixBasedUaeftsTransactions;
	}

	public List<AdvanceSearchReportOutput> getFailedSystemOutputs() {
		return failedSystemOutputs;
	}

	public void setFailedSystemOutputs(List<AdvanceSearchReportOutput> failedSystemOutputs) {
		this.failedSystemOutputs = failedSystemOutputs;
	}

	public Map<String, AdvanceSearchReportOutput> getOtherTransactions() {
		return otherTransactions;
	}

	public void setOtherTransactions(Map<String, AdvanceSearchReportOutput> otherTransactions) {
		this.otherTransactions = otherTransactions;
	}

	@Override
	public String toString() {
		return "AdvanceSearchReportInput [accountNumPrompt=" + accountNumPrompt + ", fromDatePrompt=" + fromDatePrompt
				+ ", toDatePrompt=" + toDatePrompt + ", currencyPrompt=" + currencyPrompt + ", amountBetweenPrompt="
				+ amountBetweenPrompt + ", amountToPrompt=" + amountToPrompt + ", transactionStatus="
				+ transactionStatus + ", transactionRefNum=" + transactionRefNum + ", flexComponent=" + flexComponent
				+ ", matrixComponent=" + matrixComponent + ", edmsComponent=" + edmsComponent + ", uaeftsComponent="
				+ uaeftsComponent + ", flexMatrixBasedUaeftsTransactions=" + flexMatrixBasedUaeftsTransactions
				+ ", failedSystemOutputs=" + failedSystemOutputs + ", otherTransactions=" + otherTransactions + "]";
	}

}
