package com.mashreq.paymentTracker.dto;

import com.mashreq.paymentTracker.model.Metrics;

public class LinkedReportResponseDTO {
	private long id;
	private String linkName;
	private String linkDescription;
	private String reportName;
	private long linkedReportID;
	private long reportId;
	private String linkedReportName;
	private String sourceMetricName;
	private Metrics sourceMetrics;
	private String active;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLinkName() {
		return linkName;
	}

	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}

	public String getLinkDescription() {
		return linkDescription;
	}

	public void setLinkDescription(String linkDescription) {
		this.linkDescription = linkDescription;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getLinkedReportName() {
		return linkedReportName;
	}

	public void setLinkedReportName(String linkedReportName) {
		this.linkedReportName = linkedReportName;
	}

	public String getSourceMetricName() {
		return sourceMetricName;
	}

	public void setSourceMetricName(String sourceMetricName) {
		this.sourceMetricName = sourceMetricName;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public long getLinkedReportID() {
		return linkedReportID;
	}

	public void setLinkedReportID(long linkedReportID) {
		this.linkedReportID = linkedReportID;
	}

	public Metrics getSourceMetrics() {
		return sourceMetrics;
	}

	public void setSourceMetrics(Metrics sourceMetrics) {
		this.sourceMetrics = sourceMetrics;
	}

	public long getReportId() {
		return reportId;
	}

	public void setReportId(long reportId) {
		this.reportId = reportId;
	}

	@Override
	public String toString() {
		return "LinkedReportResponseDTO [id=" + id + ", linkName=" + linkName + ", linkDescription=" + linkDescription
				+ ", reportName=" + reportName + ", linkedReportName=" + linkedReportName + ", sourceMetricName="
				+ sourceMetricName + ", active=" + active + "]";
	}

	public LinkedReportResponseDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LinkedReportResponseDTO(long id, String linkName, String linkDescription, String reportName,
			String linkedReportName, String sourceMetricName, String active) {
		super();
		this.id = id;
		this.linkName = linkName;
		this.linkDescription = linkDescription;
		this.reportName = reportName;
		this.linkedReportName = linkedReportName;
		this.sourceMetricName = sourceMetricName;
		this.active = active;
	}

}