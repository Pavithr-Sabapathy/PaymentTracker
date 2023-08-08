package com.mashreq.paymentTracker.dto;

import java.util.List;

import com.mashreq.paymentTracker.service.ReportInput;

public class FircosoftReportContext implements ReportInput {

	private PaymentInvestigationReportOutput fofaRecord;
	private List<PaymentInvestigationReportOutput> complianceRecords;
	private String systemId;
	private String mesgType;
	private String referenceNum;

	public PaymentInvestigationReportOutput getLatestComplianceRecord() {
		PaymentInvestigationReportOutput compMessage = null;
		if (!complianceRecords.isEmpty()) {
			compMessage = getComplianceRecords().get(getComplianceRecords().size() - 1);
		}
		return compMessage;
	}

	public PaymentInvestigationReportOutput getFofaRecord() {
		return fofaRecord;
	}

	public void setFofaRecord(PaymentInvestigationReportOutput fofaRecord) {
		this.fofaRecord = fofaRecord;
	}

	public List<PaymentInvestigationReportOutput> getComplianceRecords() {
		return complianceRecords;
	}

	public void setComplianceRecords(List<PaymentInvestigationReportOutput> complianceRecords) {
		this.complianceRecords = complianceRecords;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public String getMesgType() {
		return mesgType;
	}

	public void setMesgType(String mesgType) {
		this.mesgType = mesgType;
	}

	public String getReferenceNum() {
		return referenceNum;
	}

	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}

	@Override
	public String toString() {
		return "FircosoftReportContext [fofaRecord=" + fofaRecord + ", complianceRecords=" + complianceRecords
				+ ", systemId=" + systemId + ", mesgType=" + mesgType + ", referenceNum=" + referenceNum + "]";
	}

}
