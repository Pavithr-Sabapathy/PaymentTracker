package com.mashreq.paymentTracker.dto;

import java.io.Serializable;
import java.util.List;

public class SwiftDetailsReportObjectDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9061122179390774744L;
	private boolean messageFound = false;
	private Long componentDetailId;
	private String aid;
	private String sumidl;
	private String sumidh;
	private String description;
	private String deliveryStatus;
	private String priority;
	private String reference;
	private String swiftIput;
	private String sender;
	private String receiver;
	private String senderDetails;
	private String receiverDetails;
	private List<MessageField> messageFields;
	private String swiftInput;
	public boolean isMessageFound() {
		return messageFound;
	}

	public void setMessageFound(boolean messageFound) {
		this.messageFound = messageFound;
	}

	public Long getComponentDetailId() {
		return componentDetailId;
	}

	public void setComponentDetailId(Long componentDetailId) {
		this.componentDetailId = componentDetailId;
	}

	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}

	public String getSumidl() {
		return sumidl;
	}

	public void setSumidl(String sumidl) {
		this.sumidl = sumidl;
	}

	public String getSumidh() {
		return sumidh;
	}

	public void setSumidh(String sumidh) {
		this.sumidh = sumidh;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDeliveryStatus() {
		return deliveryStatus;
	}

	public void setDeliveryStatus(String deliveryStatus) {
		this.deliveryStatus = deliveryStatus;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getSwiftIput() {
		return swiftIput;
	}

	public void setSwiftIput(String swiftIput) {
		this.swiftIput = swiftIput;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getSenderDetails() {
		return senderDetails;
	}

	public void setSenderDetails(String senderDetails) {
		this.senderDetails = senderDetails;
	}

	public String getReceiverDetails() {
		return receiverDetails;
	}

	public void setReceiverDetails(String receiverDetails) {
		this.receiverDetails = receiverDetails;
	}

	public List<MessageField> getMessageFields() {
		return messageFields;
	}

	public void setMessageFields(List<MessageField> messageFields) {
		this.messageFields = messageFields;
	}

	public String getSwiftInput() {
		return swiftInput;
	}

	public void setSwiftInput(String swiftInput) {
		this.swiftInput = swiftInput;
	}

}