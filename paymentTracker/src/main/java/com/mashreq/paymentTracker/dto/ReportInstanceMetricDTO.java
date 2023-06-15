package com.mashreq.paymentTracker.dto;

import java.io.Serializable;

import com.mashreq.paymentTracker.utility.CheckType;

public class ReportInstanceMetricDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6378890162704234789L;
	private Long id;
	private Long metricId;
	private String metricKey;
	private Integer order;
	private CheckType displayable = CheckType.NO;
	private CheckType displayLimit = CheckType.NO;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getMetricId() {
		return metricId;
	}
	public void setMetricId(Long metricId) {
		this.metricId = metricId;
	}
	public String getMetricKey() {
		return metricKey;
	}
	public void setMetricKey(String metricKey) {
		this.metricKey = metricKey;
	}
	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}
	public CheckType getDisplayable() {
		return displayable;
	}
	public void setDisplayable(CheckType displayable) {
		this.displayable = displayable;
	}
	public CheckType getDisplayLimit() {
		return displayLimit;
	}
	public void setDisplayLimit(CheckType displayLimit) {
		this.displayLimit = displayLimit;
	}
	
}
