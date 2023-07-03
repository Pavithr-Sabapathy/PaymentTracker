package com.mashreq.paymentTracker.dto;

import java.io.Serializable;
import java.math.BigInteger;

public class PromptDTO implements Serializable {

	private static final long serialVersionUID = 6394517087984821262L;
	private long promptId;
	private String promptKey;
	private String displayName;
	private BigInteger promptOrder;
	private String promptRequired;
	private long reportId;
	private BigInteger entityId;

	public long getPromptId() {
		return promptId;
	}

	public void setPromptId(long promptId) {
		this.promptId = promptId;
	}

	public String getPromptKey() {
		return promptKey;
	}

	public void setPromptKey(String promptKey) {
		this.promptKey = promptKey;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public BigInteger getPromptOrder() {
		return promptOrder;
	}

	public void setPromptOrder(BigInteger promptOrder) {
		this.promptOrder = promptOrder;
	}

	public String getPromptRequired() {
		return promptRequired;
	}

	public void setPromptRequired(String promptRequired) {
		this.promptRequired = promptRequired;
	}

	public long getReportId() {
		return reportId;
	}

	public void setReportId(long reportId) {
		this.reportId = reportId;
	}

	public BigInteger getEntityId() {
		return entityId;
	}

	public void setEntityId(BigInteger entityId) {
		this.entityId = entityId;
	}

	@Override
	public String toString() {
		return "PromptDTO [promptId=" + promptId + ", promptKey=" + promptKey + ", displayName=" + displayName
				+ ", promptOrder=" + promptOrder + ", promptRequired=" + promptRequired + ", reportId=" + reportId
				+ ", entityId=" + entityId + "]";
	}

}