package com.mashreq.paymentTracker.dto;

import java.io.Serializable;

public class ReportDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private String reportName;
	private String displayName;
	private String reportDescription;
	private String reportCategory;
	private String active;
	private String valid;
	private long moduleId;
	private String connectorKey;

	public ReportDTO() {

	}
	
	public ReportDTO(long id, String reportName, String displayName, String reportDescription, String reportCategory,
			String active, String valid, long moduleId, String connectorKey) {
		super();
		this.id = id;
		this.reportName = reportName;
		this.displayName = displayName;
		this.reportDescription = reportDescription;
		this.reportCategory = reportCategory;
		this.active = active;
		this.valid = valid;
		this.moduleId = moduleId;
		this.connectorKey = connectorKey;
	}



	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

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

	public String getConnectorKey() {
		return connectorKey;
	}

	public void setConnectorKey(String connectorKey) {
		this.connectorKey = connectorKey;
	}

	@Override
	public String toString() {
		return "ReportDTO [id=" + id + ", reportName=" + reportName + ", displayName=" + displayName
				+ ", reportDescription=" + reportDescription + ", reportCategory=" + reportCategory + ", active="
				+ active + ", valid=" + valid + ", moduleId=" + moduleId + ", connectorKey=" + connectorKey + "]";
	}

}
