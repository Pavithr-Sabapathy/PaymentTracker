package com.mashreq.paymentTracker.dto;

import com.mashreq.paymentTracker.service.ReportInput;

public class FlexReportContext implements ReportInput {

	private PaymentInvestigationReportOutput fttbContract;
	private PaymentInvestigationReportOutput accountingData;
	private PaymentInvestigationReportOutput contractedInitiatedData;
	private PaymentInvestigationReportOutput contractedBookedData;
	private PaymentInvestigationReportOutput contractedLiquidatedData;
	private PaymentInvestigationReportOutput messageOutData;
	private PaymentInvestigationReportOutput messageInData;

	public PaymentInvestigationReportOutput getFttbContract() {
		return fttbContract;
	}

	public void setFttbContract(PaymentInvestigationReportOutput fttbContract) {
		this.fttbContract = fttbContract;
	}

	public PaymentInvestigationReportOutput getAccountingData() {
		return accountingData;
	}

	public void setAccountingData(PaymentInvestigationReportOutput accountingData) {
		this.accountingData = accountingData;
	}

	public PaymentInvestigationReportOutput getContractedInitiatedData() {
		return contractedInitiatedData;
	}

	public void setContractedInitiatedData(PaymentInvestigationReportOutput contractedInitiatedData) {
		this.contractedInitiatedData = contractedInitiatedData;
	}

	public PaymentInvestigationReportOutput getContractedBookedData() {
		return contractedBookedData;
	}

	public void setContractedBookedData(PaymentInvestigationReportOutput contractedBookedData) {
		this.contractedBookedData = contractedBookedData;
	}

	public PaymentInvestigationReportOutput getContractedLiquidatedData() {
		return contractedLiquidatedData;
	}

	public void setContractedLiquidatedData(PaymentInvestigationReportOutput contractedLiquidatedData) {
		this.contractedLiquidatedData = contractedLiquidatedData;
	}

	public PaymentInvestigationReportOutput getMessageOutData() {
		return messageOutData;
	}

	public void setMessageOutData(PaymentInvestigationReportOutput messageOutData) {
		this.messageOutData = messageOutData;
	}

	public PaymentInvestigationReportOutput getMessageInData() {
		return messageInData;
	}

	public void setMessageInData(PaymentInvestigationReportOutput messageInData) {
		this.messageInData = messageInData;
	}

}
