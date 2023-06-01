package com.mashreq.paymentTracker.dto;

import java.io.Serializable;
import java.util.Set;

import com.mashreq.paymentTracker.utility.CheckType;

public class CannedReport implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8316748452358921679L;
	private Long id;
	private String name;
	private String displayName;
	private Long appId;
	private CheckType active = CheckType.NO;
	private CheckType valid = CheckType.NO;
	private CheckType deleted = CheckType.NO;
	private Set<CannedReportPrompt> cannedReportPrompts;
	private Set<CannedReportMetric> cannedReportMetrics;
	private Set<CannedReportComponent> cannedReportComponents;

	public Long getAppId() {
		return appId;
	}

	public void setAppId(Long appId) {
		this.appId = appId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public CheckType getActive() {
		return active;
	}

	public void setActive(CheckType active) {
		this.active = active;
	}

	public CheckType getValid() {
		return valid;
	}

	public void setValid(CheckType valid) {
		this.valid = valid;
	}

	public CheckType getDeleted() {
		return deleted;
	}

	public void setDeleted(CheckType deleted) {
		this.deleted = deleted;
	}

	public Set<CannedReportPrompt> getCannedReportPrompts() {
		return cannedReportPrompts;
	}

	public void setCannedReportPrompts(Set<CannedReportPrompt> cannedReportPrompts) {
		this.cannedReportPrompts = cannedReportPrompts;
	}

	public Set<CannedReportMetric> getCannedReportMetrics() {
		return cannedReportMetrics;
	}

	public void setCannedReportMetrics(Set<CannedReportMetric> cannedReportMetrics) {
		this.cannedReportMetrics = cannedReportMetrics;
	}

	public Set<CannedReportComponent> getCannedReportComponents() {
		return cannedReportComponents;
	}

	public void setCannedReportComponents(Set<CannedReportComponent> cannedReportComponents) {
		this.cannedReportComponents = cannedReportComponents;
	}

}
