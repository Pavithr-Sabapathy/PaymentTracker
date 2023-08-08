package com.mashreq.paymentTracker.dto;

import java.util.List;

public class UAEFTSReportContext {

	
	   private List<UAEFTSReportDataContext> incomingEnquiries;
	   private List<UAEFTSReportDataContext> outgoingEnquiries;
	   private UAEFTSReportDataContext       incomingMessage;
	   private UAEFTSReportDataContext       outgoingMessage;
	   // deduced from enquiries
	   private String                        referenceNum;
	   // temp data
	   private UAEFTSReportDataContext           currentData;
	public List<UAEFTSReportDataContext> getIncomingEnquiries() {
		return incomingEnquiries;
	}
	public void setIncomingEnquiries(List<UAEFTSReportDataContext> incomingEnquiries) {
		this.incomingEnquiries = incomingEnquiries;
	}
	public List<UAEFTSReportDataContext> getOutgoingEnquiries() {
		return outgoingEnquiries;
	}
	public void setOutgoingEnquiries(List<UAEFTSReportDataContext> outgoingEnquiries) {
		this.outgoingEnquiries = outgoingEnquiries;
	}
	public UAEFTSReportDataContext getIncomingMessage() {
		return incomingMessage;
	}
	public void setIncomingMessage(UAEFTSReportDataContext incomingMessage) {
		this.incomingMessage = incomingMessage;
	}
	public UAEFTSReportDataContext getOutgoingMessage() {
		return outgoingMessage;
	}
	public void setOutgoingMessage(UAEFTSReportDataContext outgoingMessage) {
		this.outgoingMessage = outgoingMessage;
	}
	public String getReferenceNum() {
		return referenceNum;
	}
	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}
	public UAEFTSReportDataContext getCurrentData() {
		return currentData;
	}
	public void setCurrentData(UAEFTSReportDataContext currentData) {
		this.currentData = currentData;
	}
	   
	   

}
