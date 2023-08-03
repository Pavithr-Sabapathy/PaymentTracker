package com.mashreq.paymentTracker.dto;

import java.sql.Timestamp;

public class AdvanceSearchReportOutput extends ReportBaseOutput {

	private String transactionReference;
	private String beneficiaryDetails;
	private String valueDate;
	private String currency;
	private String amount;
	private String status;
	private String messageType;
	private String initationSource;
	private String messageThrough;
	private String accountNum;
	private String relatedAccount;
	private String instrumentCode;
	private String externalRefNum;
	private String coreReferenceNum;
	private Timestamp transactionDate;
	private String processName;
	private String activityName;
	private String messageSubFormat = "INPUT";

	public String getTransactionReference() {
		return transactionReference;
	}

	public void setTransactionReference(String transactionReference) {
		this.transactionReference = transactionReference;
	}

	public String getBeneficiaryDetails() {
		return beneficiaryDetails;
	}

	public void setBeneficiaryDetails(String beneficiaryDetails) {
		this.beneficiaryDetails = beneficiaryDetails;
	}

	public String getValueDate() {
		return valueDate;
	}

	public void setValueDate(String valueDate) {
		this.valueDate = valueDate;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getInitationSource() {
		return initationSource;
	}

	public void setInitationSource(String initationSource) {
		this.initationSource = initationSource;
	}

	public String getMessageThrough() {
		return messageThrough;
	}

	public void setMessageThrough(String messageThrough) {
		this.messageThrough = messageThrough;
	}

	public String getAccountNum() {
		return accountNum;
	}

	public void setAccountNum(String accountNum) {
		this.accountNum = accountNum;
	}

	public String getRelatedAccount() {
		return relatedAccount;
	}

	public void setRelatedAccount(String relatedAccount) {
		this.relatedAccount = relatedAccount;
	}

	public String getInstrumentCode() {
		return instrumentCode;
	}

	public void setInstrumentCode(String instrumentCode) {
		this.instrumentCode = instrumentCode;
	}

	public String getExternalRefNum() {
		return externalRefNum;
	}

	public void setExternalRefNum(String externalRefNum) {
		this.externalRefNum = externalRefNum;
	}

	public String getCoreReferenceNum() {
		return coreReferenceNum;
	}

	public void setCoreReferenceNum(String coreReferenceNum) {
		this.coreReferenceNum = coreReferenceNum;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public Timestamp getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Timestamp transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getMessageSubFormat() {
		return messageSubFormat;
	}

	public void setMessageSubFormat(String messageSubFormat) {
		this.messageSubFormat = messageSubFormat;
	}

	@Override
	public String toString() {
		return "AdvanceSearchReportOutput [transactionReference=" + transactionReference + ", beneficiaryDetails="
				+ beneficiaryDetails + ", valueDate=" + valueDate + ", currency=" + currency + ", amount=" + amount
				+ ", status=" + status + ", messageType=" + messageType + ", initationSource=" + initationSource
				+ ", messageThrough=" + messageThrough + ", accountNum=" + accountNum + ", relatedAccount="
				+ relatedAccount + ", instrumentCode=" + instrumentCode + ", externalRefNum=" + externalRefNum
				+ ", coreReferenceNum=" + coreReferenceNum + ", transactionDate=" + transactionDate + ", processName="
				+ processName + ", activityName=" + activityName + ", messageSubFormat=" + messageSubFormat + "]";
	}

}
