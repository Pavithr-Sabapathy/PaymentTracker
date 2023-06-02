package com.mashreq.paymentTracker.dto;

import java.io.Serializable;

public class SWIFTMessageDetailsFederatedReportOutput implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4814161003030894908L;
	private String key;
	private String value;
	private Long componentDetailId;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Long getComponentDetailId() {
		return componentDetailId;
	}

	public void setComponentDetailId(Long componentDetailId) {
		this.componentDetailId = componentDetailId;
	}

}
