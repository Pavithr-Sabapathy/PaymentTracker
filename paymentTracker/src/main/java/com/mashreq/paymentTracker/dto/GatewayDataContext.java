package com.mashreq.paymentTracker.dto;

import java.util.List;
import java.util.Map;

import com.mashreq.paymentTracker.service.ReportInput;

public class GatewayDataContext implements ReportInput {

	private Map<String, GatewayDataMessageContext> incomingEnquiries;
	private Map<String, GatewayDataMessageContext> outgoingEnquiries;
	private GatewayDataMessageContext incomingMessage;
	private GatewayDataMessageContext outgoingMessage;
	private List<GatewayDataMessageContext> incomingTrchStatusMessages;
	private List<GatewayDataMessageContext> outgoingTrchStatusMessages;
	private List<GatewayDataMessageContext> incomingIpalaStatusMessages;
	private List<GatewayDataMessageContext> outgoingIpalaStatusMessages;
	private boolean swiftDataFound = false;
	private boolean gatewayDataFound = false;
	public Map<String, GatewayDataMessageContext> getIncomingEnquiries() {
		return incomingEnquiries;
	}
	public void setIncomingEnquiries(Map<String, GatewayDataMessageContext> incomingEnquiries) {
		this.incomingEnquiries = incomingEnquiries;
	}
	public Map<String, GatewayDataMessageContext> getOutgoingEnquiries() {
		return outgoingEnquiries;
	}
	public void setOutgoingEnquiries(Map<String, GatewayDataMessageContext> outgoingEnquiries) {
		this.outgoingEnquiries = outgoingEnquiries;
	}
	public GatewayDataMessageContext getIncomingMessage() {
		return incomingMessage;
	}
	public void setIncomingMessage(GatewayDataMessageContext incomingMessage) {
		this.incomingMessage = incomingMessage;
	}
	public GatewayDataMessageContext getOutgoingMessage() {
		return outgoingMessage;
	}
	public void setOutgoingMessage(GatewayDataMessageContext outgoingMessage) {
		this.outgoingMessage = outgoingMessage;
	}
	public List<GatewayDataMessageContext> getIncomingTrchStatusMessages() {
		return incomingTrchStatusMessages;
	}
	public void setIncomingTrchStatusMessages(List<GatewayDataMessageContext> incomingTrchStatusMessages) {
		this.incomingTrchStatusMessages = incomingTrchStatusMessages;
	}
	public List<GatewayDataMessageContext> getOutgoingTrchStatusMessages() {
		return outgoingTrchStatusMessages;
	}
	public void setOutgoingTrchStatusMessages(List<GatewayDataMessageContext> outgoingTrchStatusMessages) {
		this.outgoingTrchStatusMessages = outgoingTrchStatusMessages;
	}
	public List<GatewayDataMessageContext> getIncomingIpalaStatusMessages() {
		return incomingIpalaStatusMessages;
	}
	public void setIncomingIpalaStatusMessages(List<GatewayDataMessageContext> incomingIpalaStatusMessages) {
		this.incomingIpalaStatusMessages = incomingIpalaStatusMessages;
	}
	public List<GatewayDataMessageContext> getOutgoingIpalaStatusMessages() {
		return outgoingIpalaStatusMessages;
	}
	public void setOutgoingIpalaStatusMessages(List<GatewayDataMessageContext> outgoingIpalaStatusMessages) {
		this.outgoingIpalaStatusMessages = outgoingIpalaStatusMessages;
	}
	public boolean isSwiftDataFound() {
		return swiftDataFound;
	}
	public void setSwiftDataFound(boolean swiftDataFound) {
		this.swiftDataFound = swiftDataFound;
	}
	public boolean isGatewayDataFound() {
		return gatewayDataFound;
	}
	public void setGatewayDataFound(boolean gatewayDataFound) {
		this.gatewayDataFound = gatewayDataFound;
	}
	@Override
	public String toString() {
		return "GatewayDataContext [incomingEnquiries=" + incomingEnquiries + ", outgoingEnquiries=" + outgoingEnquiries
				+ ", incomingMessage=" + incomingMessage + ", outgoingMessage=" + outgoingMessage
				+ ", incomingTrchStatusMessages=" + incomingTrchStatusMessages + ", outgoingTrchStatusMessages="
				+ outgoingTrchStatusMessages + ", incomingIpalaStatusMessages=" + incomingIpalaStatusMessages
				+ ", outgoingIpalaStatusMessages=" + outgoingIpalaStatusMessages + ", swiftDataFound=" + swiftDataFound
				+ ", gatewayDataFound=" + gatewayDataFound + "]";
	}
	
}
