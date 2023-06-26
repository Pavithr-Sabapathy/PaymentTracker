package com.mashreq.paymentTracker.dto;

import java.io.Serializable;
import java.math.BigInteger;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class MetricsDTO implements Serializable {
	private static final long serialVersionUID = -2260273551352016731L;

	@NotNull(message = "Display name should not be empty")
	private String displayName;
	private BigInteger metricsOrder;
	@NotNull(message = "Display should not be empty")
	private String display;
	@NotNull(message = "Report Id should not be empty")
	private long reportId;
	private BigInteger entityId;

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

	public MetricsDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MetricsDTO(@NotEmpty String displayName, BigInteger metricsOrder, @NotEmpty String display,
			@NotEmpty long reportId, BigInteger entityId) {
		super();
		this.displayName = displayName;
		this.metricsOrder = metricsOrder;
		this.display = display;
		this.reportId = reportId;
		this.entityId = entityId;
	}

}
