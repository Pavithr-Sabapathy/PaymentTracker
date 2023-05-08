package com.mashreq.paymentTracker.dto;

import java.io.Serializable;

public class FlexReportExecuteResponseMetaDTO implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 2161847798927073240L;
	private String startTime;
	private String endTime;
	private Long executionTime;
	private boolean totalExists;
	private String reportId;

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public Long getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(Long executionTime) {
		this.executionTime = executionTime;
	}

	public boolean isTotalExists() {
		return totalExists;
	}

	public void setTotalExists(boolean totalExists) {
		this.totalExists = totalExists;
	}

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	@Override
	public String toString() {
		return "FlexReportExecuteResponseMetaDTO [startTime=" + startTime + ", endTime=" + endTime + ", executionTime="
				+ executionTime + ", totalExists=" + totalExists + ", reportId=" + reportId + "]";
	}

}
