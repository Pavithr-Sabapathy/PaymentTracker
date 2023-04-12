package com.mashreq.paymentTracker.dto;

import java.io.Serializable;

public class ReportDTORequest implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6531572042923050288L;
	private String reportName;
	private String displayName;
	private String reportDescription;
	private String reportCategory;
	private String active;
	private String valid;
	private long moduleId;

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

	public long getModuleId() {
		return moduleId;
	}

	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}

	public ReportDTORequest() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ReportDTORequest(String reportName, String displayName, String reportDescription, String reportCategory,
			String active, String valid, long moduleId) {
		super();
		this.reportName = reportName;
		this.displayName = displayName;
		this.reportDescription = reportDescription;
		this.reportCategory = reportCategory;
		this.active = active;
		this.valid = valid;
		this.moduleId = moduleId;
	}

	@Override
	public String toString() {
		return "ReportDTORequest [reportName=" + reportName + ", displayName=" + displayName + ", reportDescription="
				+ reportDescription + ", reportCategory=" + reportCategory + ", active=" + active + ", valid=" + valid
				+ ", moduleId=" + moduleId + "]";
	}

}