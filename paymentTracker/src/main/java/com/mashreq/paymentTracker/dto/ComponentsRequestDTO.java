package com.mashreq.paymentTracker.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotEmpty;

public class ComponentsRequestDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2573666728782818423L;
	private Long componentId;
	@NotEmpty
	private String componentName;
	@NotEmpty
	private String componentKey;
	@NotEmpty
	private String active;
	@NotEmpty
	private long dataSourceId;
	@NotEmpty
	private long reportId;
	public ComponentsRequestDTO(){
		
	}
	
	public ComponentsRequestDTO(Long componentId, @NotEmpty String componentName, @NotEmpty String componentKey,
			@NotEmpty String active, @NotEmpty long dataSourceId, @NotEmpty long reportId) {
		super();
		this.componentId = componentId;
		this.componentName = componentName;
		this.componentKey = componentKey;
		this.active = active;
		this.dataSourceId = dataSourceId;
		this.reportId = reportId;
	}

	public Long getComponentId() {
		return componentId;
	}

	public void setComponentId(Long componentId) {
		this.componentId = componentId;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public String getComponentKey() {
		return componentKey;
	}

	public void setComponentKey(String componentKey) {
		this.componentKey = componentKey;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public long getDataSourceId() {
		return dataSourceId;
	}

	public void setDataSourceId(long dataSourceId) {
		this.dataSourceId = dataSourceId;
	}

	public long getReportId() {
		return reportId;
	}

	public void setReportId(long reportId) {
		this.reportId = reportId;
	}

	@Override
	public String toString() {
		return "ComponentsRequestDTO [componentId=" + componentId + ", componentName=" + componentName
				+ ", componentKey=" + componentKey + ", active=" + active + ", dataSourceId=" + dataSourceId
				+ ", reportId=" + reportId + "]";
	}

}