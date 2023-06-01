package com.mashreq.paymentTracker.dto;

import com.mashreq.paymentTracker.utility.CheckType;

public class CannedReportMetric {

	private Long id;
	private String metricKey;
	private String label;
	private String statType;
	private Integer order;
	private CheckType displayLimit = CheckType.NO;
	private CheckType displayable = CheckType.YES;
	private CannedReport cannedReport;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getMetricKey() {
		return metricKey;
	}
	public void setMetricKey(String metricKey) {
		this.metricKey = metricKey;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getStatType() {
		return statType;
	}
	public void setStatType(String statType) {
		this.statType = statType;
	}
	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}
	public CheckType getDisplayLimit() {
		return displayLimit;
	}
	public void setDisplayLimit(CheckType displayLimit) {
		this.displayLimit = displayLimit;
	}
	public CheckType getDisplayable() {
		return displayable;
	}
	public void setDisplayable(CheckType displayable) {
		this.displayable = displayable;
	}
	public CannedReport getCannedReport() {
		return cannedReport;
	}
	public void setCannedReport(CannedReport cannedReport) {
		this.cannedReport = cannedReport;
	}
	
}
