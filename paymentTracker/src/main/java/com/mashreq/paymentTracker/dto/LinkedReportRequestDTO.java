package com.mashreq.paymentTracker.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotEmpty;

public class LinkedReportRequestDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2652424058908910840L;

	@NotEmpty
	private long id;

	@NotEmpty
	private String linkName;

	@NotEmpty
	private String linkDescription;

	@NotEmpty
	private long reportId;

	@NotEmpty
	private long linkedReportId;

	@NotEmpty
	private long sourceMetricId;

	@NotEmpty
	private String active;

	@NotEmpty
	private long componentId;

	@NotEmpty
	private long componentDetailId;

	@NotEmpty
	private long moduleId;

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

	public long getReportId() {
		return reportId;
	}

	public void setReportId(long reportId) {
		this.reportId = reportId;
	}

	public long getLinkedReportId() {
		return linkedReportId;
	}

	public void setLinkedReportId(long linkedReportId) {
		this.linkedReportId = linkedReportId;
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

	public long getModuleId() {
		return moduleId;
	}

	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}

	public LinkedReportRequestDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LinkedReportRequestDTO(@NotEmpty String linkName, @NotEmpty String linkDescription, @NotEmpty long reportId,
			@NotEmpty long linkedReportId, @NotEmpty long sourceMetricId, @NotEmpty String active,
			@NotEmpty long componentId, @NotEmpty long componentDetailId) {
		super();
		this.linkName = linkName;
		this.linkDescription = linkDescription;
		this.reportId = reportId;
		this.linkedReportId = linkedReportId;
		this.sourceMetricId = sourceMetricId;
		this.active = active;
		this.componentId = componentId;
		this.componentDetailId = componentDetailId;
	}

	@Override
	public String toString() {
		return "LinkedReportRequestDTO [linkName=" + linkName + ", linkDescription=" + linkDescription + ", reportId="
				+ reportId + ", linkedReportId=" + linkedReportId + ", sourceMetricId=" + sourceMetricId + ", active="
				+ active + ", componentId=" + componentId + ", componentDetailId=" + componentDetailId + "]";
	}

}
