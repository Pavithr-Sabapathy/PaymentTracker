package com.mashreq.paymentTracker.dto;

import com.mashreq.paymentTracker.constants.MashreqFederatedReportConstants;
import com.mashreq.paymentTracker.type.MessageType;

public class SWIFTReportContext {

	private PaymentInvestigationReportOutput rMessageRecord;
	private PaymentInvestigationReportOutput rintvRecord;
	private String aid;
	private String sUmidl;
	private String sUmidh;
	private String mesgType;
	private String mesgFormat;
	private MessageType messageType;
	private String referenceNum;
	private String detectionId;
	private String relatedReferenceNum;
	private String paymentStatus;
	private String sender;

	public PaymentInvestigationReportOutput getrMessageRecord() {
		return rMessageRecord;
	}

	public void setrMessageRecord(PaymentInvestigationReportOutput rMessageRecord) {
		this.rMessageRecord = rMessageRecord;
	}

	public PaymentInvestigationReportOutput getRintvRecord() {
		return rintvRecord;
	}

	public void setRintvRecord(PaymentInvestigationReportOutput rintvRecord) {
		this.rintvRecord = rintvRecord;
	}

	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}

	public String getsUmidl() {
		return sUmidl;
	}

	public void setsUmidl(String sUmidl) {
		this.sUmidl = sUmidl;
	}

	public String getsUmidh() {
		return sUmidh;
	}

	public void setsUmidh(String sUmidh) {
		this.sUmidh = sUmidh;
	}

	public String getMesgFormat() {
		return mesgFormat;
	}

	public void setMesgFormat(String mesgFormat) {
		this.mesgFormat = mesgFormat;
	}

	public String getReferenceNum() {
		return referenceNum;
	}

	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}

	public String getDetectionId() {
		return detectionId;
	}

	public void setDetectionId(String detectionId) {
		this.detectionId = detectionId;
	}

	public String getRelatedReferenceNum() {
		return relatedReferenceNum;
	}

	public void setRelatedReferenceNum(String relatedReferenceNum) {
		this.relatedReferenceNum = relatedReferenceNum;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getMesgType() {
		if ((!mesgType.startsWith("I")) && (!mesgType.startsWith("O"))) {
			if (mesgFormat != null
					&& mesgFormat.equalsIgnoreCase(MashreqFederatedReportConstants.MESSAGE_INPUT_SUB_FORMAT)) {
				mesgType = MashreqFederatedReportConstants.MESSAGE_INPUT_SUB_FORMAT_INITIAL + " " + mesgType;
			} else if (mesgFormat != null
					&& mesgFormat.equalsIgnoreCase(MashreqFederatedReportConstants.MESSAGE_OUTPUT_SUB_FORMAT)) {
				mesgType = MashreqFederatedReportConstants.MESSAGE_OUTPUT_SUB_FORMAT_INITIAL + " " + mesgType;
			}
		}
		return mesgType;
	}

	public MessageType getMessageType() {
		messageType = null;
		if (getMesgType() != null) {
			if (MashreqFederatedReportConstants.INCOMING_PAYMENT_CODES_LIST.contains(getMesgType())) {
				messageType = MessageType.INCOMING;
			} else if (MashreqFederatedReportConstants.OUTGOING_PAYMENT_CODES_LIST.contains(getMesgType())) {
				messageType = MessageType.OUTGOING;
			} else {
				if (MashreqFederatedReportConstants.MESSAGE_INPUT_SUB_FORMAT.equalsIgnoreCase(mesgFormat)) {
					messageType = MessageType.OUTGOING_ENQUIRY;
				} else if (MashreqFederatedReportConstants.MESSAGE_OUTPUT_SUB_FORMAT.equalsIgnoreCase(mesgFormat)) {
					messageType = MessageType.INCOMING_ENQUIRY;
				}
			}
		}
		return messageType;
	}

	public void setMesgType(String mesgType) {
		this.mesgType = mesgType;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}

}
