package com.mashreq.paymentTracker.dto;

import java.util.List;

import com.mashreq.paymentTracker.service.ReportInput;

public class GatewayDataMessageContext implements ReportInput {
	private PaymentInvestigationReportOutput networkRecord;
	private PaymentInvestigationReportOutput networkNackRecord;
	private PaymentInvestigationReportOutput screeningRecord;
	private PaymentInvestigationReportOutput creditConfirmedRecord;
	private List<PaymentInvestigationReportOutput> screeningProcessedRecord;
	private String messageRef;
	private String detectionId;
	private String messageType;
	private String messageSubFormat;

	public GatewayDataMessageContext() {
		// TODO Auto-generated constructor stub
	}

	public GatewayDataMessageContext(PaymentInvestigationReportOutput networkRecord,
			PaymentInvestigationReportOutput networkNackRecord, PaymentInvestigationReportOutput screeningRecord,
			PaymentInvestigationReportOutput creditConfirmedRecord,
			List<PaymentInvestigationReportOutput> screeningProcessedRecord, String messageRef, String detectionId,
			String messageType, String messageSubFormat) {
		super();
		this.networkRecord = networkRecord;
		this.networkNackRecord = networkNackRecord;
		this.screeningRecord = screeningRecord;
		this.creditConfirmedRecord = creditConfirmedRecord;
		this.screeningProcessedRecord = screeningProcessedRecord;
		this.messageRef = messageRef;
		this.detectionId = detectionId;
		this.messageType = messageType;
		this.messageSubFormat = messageSubFormat;
	}

	public PaymentInvestigationReportOutput getLatestComplianceRecord() {
		PaymentInvestigationReportOutput compMessage = null;
		if (!screeningProcessedRecord.isEmpty()) {
			compMessage = getScreeningProcessedRecord().get(getScreeningProcessedRecord().size() - 1);
		}
		return compMessage;
	}

	public GatewayDataMessageContext(PaymentInvestigationReportOutput networkRecord, String messageRef,
			String messageType) {
		super();
		this.networkRecord = networkRecord;
		this.messageRef = messageRef;
		this.messageType = messageType;
	}

	public PaymentInvestigationReportOutput getNetworkRecord() {
		return networkRecord;
	}

	public void setNetworkRecord(PaymentInvestigationReportOutput networkRecord) {
		this.networkRecord = networkRecord;
	}

	public PaymentInvestigationReportOutput getNetworkNackRecord() {
		return networkNackRecord;
	}

	public void setNetworkNackRecord(PaymentInvestigationReportOutput networkNackRecord) {
		this.networkNackRecord = networkNackRecord;
	}

	public PaymentInvestigationReportOutput getScreeningRecord() {
		return screeningRecord;
	}

	public void setScreeningRecord(PaymentInvestigationReportOutput screeningRecord) {
		this.screeningRecord = screeningRecord;
	}

	public PaymentInvestigationReportOutput getCreditConfirmedRecord() {
		return creditConfirmedRecord;
	}

	public void setCreditConfirmedRecord(PaymentInvestigationReportOutput creditConfirmedRecord) {
		this.creditConfirmedRecord = creditConfirmedRecord;
	}

	public List<PaymentInvestigationReportOutput> getScreeningProcessedRecord() {
		return screeningProcessedRecord;
	}

	public void setScreeningProcessedRecord(List<PaymentInvestigationReportOutput> screeningProcessedRecord) {
		this.screeningProcessedRecord = screeningProcessedRecord;
	}

	public String getMessageRef() {
		return messageRef;
	}

	public void setMessageRef(String messageRef) {
		this.messageRef = messageRef;
	}

	public String getDetectionId() {
		return detectionId;
	}

	public void setDetectionId(String detectionId) {
		this.detectionId = detectionId;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getMessageSubFormat() {
		return messageSubFormat;
	}

	public void setMessageSubFormat(String messageSubFormat) {
		this.messageSubFormat = messageSubFormat;
	}

	@Override
	public String toString() {
		return "GatewayDataMessageContext [networkRecord=" + networkRecord + ", networkNackRecord=" + networkNackRecord
				+ ", screeningRecord=" + screeningRecord + ", creditConfirmedRecord=" + creditConfirmedRecord
				+ ", screeningProcessedRecord=" + screeningProcessedRecord + ", messageRef=" + messageRef
				+ ", detectionId=" + detectionId + ", messageType=" + messageType + ", messageSubFormat="
				+ messageSubFormat + "]";
	}

}
