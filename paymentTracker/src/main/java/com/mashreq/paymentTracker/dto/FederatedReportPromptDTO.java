package com.mashreq.paymentTracker.dto;

import java.util.List;

public class FederatedReportPromptDTO {
	private String promptKey;
	private String promptValue;
	private List<String> valueList;
	
	public String getPromptKey() {
		return promptKey;
	}

	public void setPromptKey(String promptKey) {
		this.promptKey = promptKey;
	}

	public String getPromptValue() {
		return promptValue;
	}

	public void setPromptValue(String promptValue) {
		this.promptValue = promptValue;
	}

	public List<String> getValueList() {
		return valueList;
	}

	public void setValueList(List<String> valueList) {
		this.valueList = valueList;
	}

	@Override
	public String toString() {
		return "FederatedReportPromptDTO [promptKey=" + promptKey + ", promptValue=" + promptValue + ", valueList="
				+ valueList + "]";
	}

}
