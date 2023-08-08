package com.mashreq.paymentTracker.dto;

import java.io.Serializable;
import java.util.List;

public class SafeWatchReportContext implements Serializable {

	private static final long serialVersionUID = -5468055197090823001L;
	private PaymentInvestigationReportOutput detectionRecord;
	private List<PaymentInvestigationReportOutput> complianceRecords;
	private String detectionId;
	private String mesgType;
	private String referenceNum;

	public PaymentInvestigationReportOutput getLatestComplianceRecord() {
		PaymentInvestigationReportOutput compMessage = null;
		if (!complianceRecords.isEmpty()) {
			compMessage = getComplianceRecords().get(getComplianceRecords().size() - 1);
		}
		return compMessage;
	}

	public PaymentInvestigationReportOutput getDetectionRecord() {
		return detectionRecord;
	}

	public void setDetectionRecord(PaymentInvestigationReportOutput detectionRecord) {
		this.detectionRecord = detectionRecord;
	}

	public List<PaymentInvestigationReportOutput> getComplianceRecords() {
		return complianceRecords;
	}

	public void setComplianceRecords(List<PaymentInvestigationReportOutput> complianceRecords) {
		this.complianceRecords = complianceRecords;
	}

	public String getDetectionId() {
		return detectionId;
	}

	public void setDetectionId(String detectionId) {
		this.detectionId = detectionId;
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

}
