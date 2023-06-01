package com.mashreq.paymentTracker.dto;

import java.io.Serializable;

public class MessageDetailsFederatedReportInput implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7204088691118757836L;
	private FederatedReportPromptDTO referenceNumPrompt;
	private FederatedReportPromptDTO messageTypePrompt;
	private FederatedReportPromptDTO messageThroughPrompt;
	private FederatedReportPromptDTO messageSubFormatPrompt;

	public FederatedReportPromptDTO getReferenceNumPrompt() {
		return referenceNumPrompt;
	}

	public void setReferenceNumPrompt(FederatedReportPromptDTO referenceNumPrompt) {
		this.referenceNumPrompt = referenceNumPrompt;
	}

	public FederatedReportPromptDTO getMessageTypePrompt() {
		return messageTypePrompt;
	}

	public void setMessageTypePrompt(FederatedReportPromptDTO messageTypePrompt) {
		this.messageTypePrompt = messageTypePrompt;
	}

	public FederatedReportPromptDTO getMessageThroughPrompt() {
		return messageThroughPrompt;
	}

	public void setMessageThroughPrompt(FederatedReportPromptDTO messageThroughPrompt) {
		this.messageThroughPrompt = messageThroughPrompt;
	}

	public FederatedReportPromptDTO getMessageSubFormatPrompt() {
		return messageSubFormatPrompt;
	}

	public void setMessageSubFormatPrompt(FederatedReportPromptDTO messageSubFormatPrompt) {
		this.messageSubFormatPrompt = messageSubFormatPrompt;
	}

}