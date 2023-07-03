package com.mashreq.paymentTracker.dto;

import java.io.Serializable;
import java.math.BigInteger;

public class MetricsResponseDTO implements Serializable {

	private static final long serialVersionUID = 8270862890305652440L;
	private long metricsId;
	private String displayName;
	private BigInteger metricsOrder;
	private String display;
	private long reportId;
	private BigInteger entityId;

	public long getMetricsId() {
		return metricsId;
	}

	public void setMetricsId(long metricsId) {
		this.metricsId = metricsId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public BigInteger getMetricsOrder() {
		return metricsOrder;
	}

	public void setMetricsOrder(BigInteger metricsOrder) {
		this.metricsOrder = metricsOrder;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public long getReportId() {
		return reportId;
	}

	public void setReportId(long reportId) {
		this.reportId = reportId;
	}

	public BigInteger getEntityId() {
		return entityId;
	}

	public void setEntityId(BigInteger entityId) {
		this.entityId = entityId;
	}

	@Override
	public String toString() {
		return "MetricsDTO [displayName=" + displayName + ", metricsOrder=" + metricsOrder + ", display=" + display
				+ ", reportId=" + reportId + ", entityId=" + entityId + "]";
	}

	public MetricsResponseDTO() {
		super();
	}

	public MetricsResponseDTO(long metricsId, String displayName, BigInteger metricsOrder, String display,
			long reportId, BigInteger entityId) {
		super();
		this.metricsId = metricsId;
		this.displayName = displayName;
		this.metricsOrder = metricsOrder;
		this.display = display;
		this.reportId = reportId;
		this.entityId = entityId;
	}
}
