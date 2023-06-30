package com.mashreq.paymentTracker.dto;

import java.io.Serializable;

public class ComponentDTO implements Serializable {

	private static final long serialVersionUID = 5196563155697135890L;
	private Long componentId;
	private Long reportId;
	private Long dataSourceId;
	private String componentKey;
	private String componentName;
	private String active;

	public Long getComponentId() {
		return componentId;
	}

	public void setComponentId(Long componentId) {
		this.componentId = componentId;
	}

	public Long getReportId() {
		return reportId;
	}

	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}

	public Long getDataSourceId() {
		return dataSourceId;
	}

	public void setDataSourceId(Long dataSourceId) {
		this.dataSourceId = dataSourceId;
	}

	public String getComponentKey() {
		return componentKey;
	}

	public void setComponentKey(String componentKey) {
		this.componentKey = componentKey;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return "ComponentDTO [componentId=" + componentId + ", reportId=" + reportId + ", dataSourceId=" + dataSourceId
				+ ", componentKey=" + componentKey + ", componentName=" + componentName + ", active=" + active + "]";
	}

}