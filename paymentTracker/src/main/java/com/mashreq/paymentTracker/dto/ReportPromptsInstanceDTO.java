package com.mashreq.paymentTracker.dto;

import java.util.List;

public class ReportPromptsInstanceDTO {
	private long id;
	private String key;
	private List<String> promptsValueList;
	private String promptValue;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public List<String> getPromptsValueList() {
		return promptsValueList;
	}
	public void setPromptsValueList(List<String> promptsValueList) {
		this.promptsValueList = promptsValueList;
	}
	public String getPromptValue() {
		return promptValue;
	}
	public void setPromptValue(String promptValue) {
		this.promptValue = promptValue;
	}
	
}
