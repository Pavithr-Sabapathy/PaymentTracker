package com.mashreq.paymentTracker.dto;

import java.io.Serializable;

public class LinkedReportResponseDTO implements Serializable {

	private static final long serialVersionUID = 2999855065270443273L;
	private long id;
	private String linkName;
	private String linkDescription;
	private String reportName;
	private long linkedReportId;
	private long reportId;
	private String linkedReportName;
	private String sourceMetricName;
	private long sourceMetricId;
	private String active;
	private String component;
	private String componentDetail;
	private long componentId;
	private long componentDetailId;
	private String moduleName;
	private long moduleId;

	public LinkedReportResponseDTO() {
		super();
	}

	public LinkedReportResponseDTO(long id, String linkName, String linkDescription, String reportName,
			long linkedReportId, long reportId, String linkedReportName, String sourceMetricName, long sourceMetricId,
			String active, String component, String componentDetail, long componentId, long componentDetailId,
			String moduleName, long moduleId) {
		super();
		this.id = id;
		this.linkName = linkName;
		this.linkDescription = linkDescription;
		this.reportName = reportName;
		this.linkedReportId = linkedReportId;
		this.reportId = reportId;
		this.linkedReportName = linkedReportName;
		this.sourceMetricName = sourceMetricName;
		this.sourceMetricId = sourceMetricId;
		this.active = active;
		this.component = component;
		this.componentDetail = componentDetail;
		this.componentId = componentId;
		this.componentDetailId = componentDetailId;
		this.moduleName = moduleName;
		this.moduleId = moduleId;
	}



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

	public long getLinkedReportId() {
		return linkedReportId;
	}

	public void setLinkedReportId(long linkedReportId) {
		this.linkedReportId = linkedReportId;
	}

	public long getReportId() {
		return reportId;
	}

	public void setReportId(long reportId) {
		this.reportId = reportId;
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

	public long getSourceMetricId() {
		return sourceMetricId;
	}

	public void setSourceMetricId(long sourceMetricId) {
		this.sourceMetricId = sourceMetricId;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public String getComponentDetail() {
		return componentDetail;
	}

	public void setComponentDetail(String componentDetail) {
		this.componentDetail = componentDetail;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public LinkedReportResponseDTO(String moduleName) {
		super();
		this.moduleName = moduleName;
	}

	public long getModuleId() {
		return moduleId;
	}

	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}

	public long getComponentId() {
		return componentId;
	}

	public void setComponentId(long componentId) {
		this.componentId = componentId;
	}

	public long getComponentDetailId() {
		return componentDetailId;
	}

	public void setComponentDetailId(long componentDetailId) {
		this.componentDetailId = componentDetailId;
	}

}