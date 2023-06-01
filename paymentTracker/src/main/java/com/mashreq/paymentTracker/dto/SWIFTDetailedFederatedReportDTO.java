package com.mashreq.paymentTracker.dto;

import java.io.Serializable;

public class SWIFTDetailedFederatedReportDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5247030860616631609L;
	private CannedReportInstanceComponent component;
	private FederatedReportPromptDTO aidPrompt;
	private FederatedReportPromptDTO umidlPrompt;
	private FederatedReportPromptDTO umidhPrompt;
	private FederatedReportPromptDTO referenceNumPrompt;
	private FederatedReportPromptDTO messageSubFormatPrompt;
	private FederatedReportPromptDTO messageTypePrompt;
	private FederatedReportPromptDTO detailedType;

	public CannedReportInstanceComponent getComponent() {
		return component;
	}

	public void setComponent(CannedReportInstanceComponent component) {
		this.component = component;
	}

	public FederatedReportPromptDTO getAidPrompt() {
		return aidPrompt;
	}

	public void setAidPrompt(FederatedReportPromptDTO aidPrompt) {
		this.aidPrompt = aidPrompt;
	}

	public FederatedReportPromptDTO getUmidlPrompt() {
		return umidlPrompt;
	}

	public void setUmidlPrompt(FederatedReportPromptDTO umidlPrompt) {
		this.umidlPrompt = umidlPrompt;
	}

	public FederatedReportPromptDTO getUmidhPrompt() {
		return umidhPrompt;
	}

	public void setUmidhPrompt(FederatedReportPromptDTO umidhPrompt) {
		this.umidhPrompt = umidhPrompt;
	}

	public FederatedReportPromptDTO getReferenceNumPrompt() {
		return referenceNumPrompt;
	}

	public void setReferenceNumPrompt(FederatedReportPromptDTO referenceNumPrompt) {
		this.referenceNumPrompt = referenceNumPrompt;
	}

	public FederatedReportPromptDTO getMessageSubFormatPrompt() {
		return messageSubFormatPrompt;
	}

	public void setMessageSubFormatPrompt(FederatedReportPromptDTO messageSubFormatPrompt) {
		this.messageSubFormatPrompt = messageSubFormatPrompt;
	}

	public FederatedReportPromptDTO getMessageTypePrompt() {
		return messageTypePrompt;
	}

	public void setMessageTypePrompt(FederatedReportPromptDTO messageTypePrompt) {
		this.messageTypePrompt = messageTypePrompt;
	}

	public FederatedReportPromptDTO getDetailedType() {
		return detailedType;
	}

	public void setDetailedType(FederatedReportPromptDTO detailedType) {
		this.detailedType = detailedType;
	}

}