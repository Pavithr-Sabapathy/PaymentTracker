package com.mashreq.paymentTracker.dto;

import java.io.Serializable;

public class ReportExecuteResponseColumnDefDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8196217614093116771L;
	private String field;
	private boolean linkExists;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public boolean isLinkExists() {
		return linkExists;
	}

	public void setLinkExists(boolean linkExists) {
		this.linkExists = linkExists;
	}

	@Override
	public String toString() {
		return "FlexReportExecuteResponseColumnDef [field=" + field + ", linkExists=" + linkExists + "]";
	}

}
