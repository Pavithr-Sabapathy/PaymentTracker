package com.mashreq.paymentTracker.dto;

import java.io.Serializable;

public class ReportDataDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1854746393704157010L;
	private Long id;
	private Long reportExecutionId;
	private String reportData;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getReportExecutionId() {
		return reportExecutionId;
	}
	public void setReportExecutionId(Long reportExecutionId) {
		this.reportExecutionId = reportExecutionId;
	}
	public String getReportData() {
		return reportData;
	}
	public void setReportData(String reportData) {
		this.reportData = reportData;
	}
	@Override
	public String toString() {
		return "ReportDataDTO [id=" + id + ", reportExecutionId=" + reportExecutionId + ", reportData=" + reportData
				+ "]";
	}
	
}
