package com.mashreq.paymentTracker.dto;

import java.util.List;

import com.mashreq.paymentTracker.type.PromptValueType;

public class FederatedReportPromptDTO {
	private String promptKey;
	private String promptValue;
	private List<String> valueList;
	private PromptValueType valueType;

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

	public PromptValueType getValueType() {
		return valueType;
	}

	public void setValueType(PromptValueType valueType) {
		this.valueType = valueType;
	}

	@Override
	public String toString() {
		return "FederatedReportPromptDTO [promptKey=" + promptKey + ", promptValue=" + promptValue + ", valueList="
				+ valueList + ", valueType=" + valueType + "]";
	}

}
