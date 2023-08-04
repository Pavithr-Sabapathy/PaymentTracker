package com.mashreq.paymentTracker.dto;

import java.sql.Timestamp;
import java.util.List;

import com.mashreq.paymentTracker.utility.UAEFTSTableType;

public class UAEFTSReportDataContext {
	   private PaymentInvestigationReportOutput       inputMessage;
	   private List<PaymentInvestigationReportOutput> complianceMessages;
	   // this would pe populated only for outgoing payment
	   private PaymentInvestigationReportOutput       ccnMessage;
	   private Timestamp                                       amlTime;
	   private String                                          isAmlRequired;
	   private String                                          amlStatus;
	   private String                                          ftsStatus;
	   private Timestamp                                       fileCreatedOn;
	   private String                                          msgId;
	   private String                                          formatAction;
	   private UAEFTSTableType                                 tableType;
	   private String                                          referenceNum;
	   private String                                          relatedReferenceNum;
	   private String                                          mesgType;
	   private String                                          cbUniqueFileId;
	public PaymentInvestigationReportOutput getInputMessage() {
		return inputMessage;
	}
	public void setInputMessage(PaymentInvestigationReportOutput inputMessage) {
		this.inputMessage = inputMessage;
	}
	public List<PaymentInvestigationReportOutput> getComplianceMessages() {
		return complianceMessages;
	}
	public void setComplianceMessages(List<PaymentInvestigationReportOutput> complianceMessages) {
		this.complianceMessages = complianceMessages;
	}
	public PaymentInvestigationReportOutput getCcnMessage() {
		return ccnMessage;
	}
	public void setCcnMessage(PaymentInvestigationReportOutput ccnMessage) {
		this.ccnMessage = ccnMessage;
	}
	public Timestamp getAmlTime() {
		return amlTime;
	}
	public void setAmlTime(Timestamp amlTime) {
		this.amlTime = amlTime;
	}
	public String getIsAmlRequired() {
		return isAmlRequired;
	}
	public void setIsAmlRequired(String isAmlRequired) {
		this.isAmlRequired = isAmlRequired;
	}
	public String getAmlStatus() {
		return amlStatus;
	}
	public void setAmlStatus(String amlStatus) {
		this.amlStatus = amlStatus;
	}
	public String getFtsStatus() {
		return ftsStatus;
	}
	public void setFtsStatus(String ftsStatus) {
		this.ftsStatus = ftsStatus;
	}
	public Timestamp getFileCreatedOn() {
		return fileCreatedOn;
	}
	public void setFileCreatedOn(Timestamp fileCreatedOn) {
		this.fileCreatedOn = fileCreatedOn;
	}
	public String getMsgId() {
		return msgId;
	}
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	public String getFormatAction() {
		return formatAction;
	}
	public void setFormatAction(String formatAction) {
		this.formatAction = formatAction;
	}
	public String getReferenceNum() {
		return referenceNum;
	}
	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}
	public String getRelatedReferenceNum() {
		return relatedReferenceNum;
	}
	public void setRelatedReferenceNum(String relatedReferenceNum) {
		this.relatedReferenceNum = relatedReferenceNum;
	}
	public String getMesgType() {
		return mesgType;
	}
	public void setMesgType(String mesgType) {
		this.mesgType = mesgType;
	}
	public String getCbUniqueFileId() {
		return cbUniqueFileId;
	}
	public void setCbUniqueFileId(String cbUniqueFileId) {
		this.cbUniqueFileId = cbUniqueFileId;
	}
	public UAEFTSTableType getTableType() {
		return tableType;
	}
	public void setTableType(UAEFTSTableType tableType) {
		this.tableType = tableType;
	}
	
	   
	   

}
