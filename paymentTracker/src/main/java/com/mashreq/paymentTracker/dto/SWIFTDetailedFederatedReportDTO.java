package com.mashreq.paymentTracker.dto;

import com.mashreq.paymentTracker.service.ReportInput;

public class SWIFTDetailedFederatedReportDTO implements ReportInput {
	private ReportComponentDTO component;
	private FederatedReportPromptDTO aidPrompt;
	private FederatedReportPromptDTO umidlPrompt;
	private FederatedReportPromptDTO umidhPrompt;
	private FederatedReportPromptDTO referenceNumPrompt;
	private FederatedReportPromptDTO messageSubFormatPrompt;
	private FederatedReportPromptDTO messageTypePrompt;
	private FederatedReportPromptDTO detailedType;

	public ReportComponentDTO getComponent() {
		return component;
	}

	public void setComponent(ReportComponentDTO component) {
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

	@Override
	public String toString() {
		return "SWIFTDetailedFederatedReportDTO [component=" + component + ", aidPrompt=" + aidPrompt + ", umidlPrompt="
				+ umidlPrompt + ", umidhPrompt=" + umidhPrompt + ", referenceNumPrompt=" + referenceNumPrompt
				+ ", messageSubFormatPrompt=" + messageSubFormatPrompt + ", messageTypePrompt=" + messageTypePrompt
				+ ", detailedType=" + detailedType + "]";
	}

}
