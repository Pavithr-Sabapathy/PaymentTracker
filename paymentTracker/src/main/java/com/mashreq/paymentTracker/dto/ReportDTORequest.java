package com.mashreq.paymentTracker.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotNull;

public class ReportDTORequest implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6531572042923050288L;
	@NotNull(message = "Report name should not be empty")
	private String reportName;
	@NotNull(message = "Display name should not be empty")
	private String displayName;
	private String reportDescription;
	@NotNull(message = "Category should not be empty")
	private String reportCategory;
	@NotNull(message = "active status should not be empty")
	private String active;
	@NotNull(message = "valid should not be empty")
	private String valid;
	@NotNull(message = "Module Id should not be empty")
	private Long moduleId;
	private String connectorKey;

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getReportDescription() {
		return reportDescription;
	}

	public void setReportDescription(String reportDescription) {
		this.reportDescription = reportDescription;
	}

	public String getReportCategory() {
		return reportCategory;
	}

	public void setReportCategory(String reportCategory) {
		this.reportCategory = reportCategory;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getValid() {
		return valid;
	}

	public void setValid(String valid) {
		this.valid = valid;
	}

	public Long getModuleId() {
		return moduleId;
	}

	public void setModuleId(Long moduleId) {
		this.moduleId = moduleId;
	}

	public ReportDTORequest() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getConnectorKey() {
		return connectorKey;
	}

	public void setConnectorKey(String connectorKey) {
		this.connectorKey = connectorKey;
	}

	@Override
	public String toString() {
		return "ReportDTORequest [reportName=" + reportName + ", displayName=" + displayName + ", reportDescription="
				+ reportDescription + ", reportCategory=" + reportCategory + ", active=" + active + ", valid=" + valid
				+ ", moduleId=" + moduleId + ", connectorKey=" + connectorKey + "]";
	}


}