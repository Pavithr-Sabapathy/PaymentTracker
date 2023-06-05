package com.mashreq.paymentTracker.dto;

import java.io.Serializable;
import java.util.List;

public class PromptsProcessingRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String key;
	private List<String> value;
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

	public List<String> getValue() {
		return value;
	}

	public void setValue(List<String> value) {
		this.value = value;
	}

	public String getPromptValue() {
		return promptValue;
	}

	public void setPromptValue(String promptValue) {
		this.promptValue = promptValue;
	}

	@Override
	public String toString() {
		return "PromptsProcessingRequest [id=" + id + ", key=" + key + ", value=" + value + ", promptValue="
				+ promptValue + "]";
	}

}
