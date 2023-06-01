package com.mashreq.paymentTracker.dto;

import java.io.Serializable;
import java.util.Set;

import com.mashreq.paymentTracker.utility.CheckType;

public class CannedReportInstanceComponent implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4490298695954701243L;

	private Long id;

	private Long cannedReportComponentId;

	private Long dataSourceId;

	private Long appId;

	private String componentKey;

	private String componentName;

	private CheckType active = CheckType.YES;

	private CannedReportInstance cannedReportInstance;

	private Set<CannedReportInstanceComponentDetail> cannedReportInstanceComponentDetails;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCannedReportComponentId() {
		return cannedReportComponentId;
	}

	public void setCannedReportComponentId(Long cannedReportComponentId) {
		this.cannedReportComponentId = cannedReportComponentId;
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

	public CannedReportInstance getCannedReportInstance() {
		return cannedReportInstance;
	}

	public void setCannedReportInstance(CannedReportInstance cannedReportInstance) {
		this.cannedReportInstance = cannedReportInstance;
	}

	public Set<CannedReportInstanceComponentDetail> getCannedReportInstanceComponentDetails() {
		return cannedReportInstanceComponentDetails;
	}

	public void setCannedReportInstanceComponentDetails(
			Set<CannedReportInstanceComponentDetail> cannedReportInstanceComponentDetails) {
		this.cannedReportInstanceComponentDetails = cannedReportInstanceComponentDetails;
	}

}
