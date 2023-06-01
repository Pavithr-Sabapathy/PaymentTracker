package com.mashreq.paymentTracker.dto;

import java.util.Set;

import com.mashreq.paymentTracker.utility.CheckType;

public class CannedReportComponent {
	private Long id;

	private Long cannedReportId;

	private Long dataSourceId;

	private Long appId;

	private String componentKey;

	private String componentName;

	private CheckType active = CheckType.YES;

	private Set<CannedReportComponentDetail> cannedReportComponentDetails;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCannedReportId() {
		return cannedReportId;
	}

	public void setCannedReportId(Long cannedReportId) {
		this.cannedReportId = cannedReportId;
	}

	public Long getDataSourceId() {
		return dataSourceId;
	}

	public void setDataSourceId(Long dataSourceId) {
		this.dataSourceId = dataSourceId;
	}

	public Long getAppId() {
		return appId;
	}

	public void setAppId(Long appId) {
		this.appId = appId;
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

	public CheckType getActive() {
		return active;
	}

	public void setActive(CheckType active) {
		this.active = active;
	}

	public Set<CannedReportComponentDetail> getCannedReportComponentDetails() {
		return cannedReportComponentDetails;
	}

	public void setCannedReportComponentDetails(Set<CannedReportComponentDetail> cannedReportComponentDetails) {
		this.cannedReportComponentDetails = cannedReportComponentDetails;
	}

}
