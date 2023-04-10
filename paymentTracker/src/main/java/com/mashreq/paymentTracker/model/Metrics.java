package com.mashreq.paymentTracker.model;

import java.math.BigInteger;

import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@DynamicUpdate
@Table(name = "conf_Metric")
public class Metrics {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotNull(message = "Display name should not be empty")
	@Column(name = "display_name")
	private String displayName;

	@NotNull(message = "Prompt Order should not be empty")
	@Column(name = "Metrics_Order")
	private BigInteger metricsOrder;

	@ManyToOne(targetEntity = Reports.class)
	@JoinColumn(name = "report_id")
	private Reports report;

	// TODO -- @NotNull(message = "Entity Id should not be empty")
	@Column(name = "ent_id")
	private BigInteger entityId;

	@NotNull(message = "Prompt Required should not be empty")
	@Column(name = "display")
	private String display;

	public Metrics() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public Reports getReport() {
		return report;
	}

	public void setReport(Reports report) {
		this.report = report;
	}

	public BigInteger getEntityId() {
		return entityId;
	}

	public void setEntityId(BigInteger entityId) {
		this.entityId = entityId;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	@Override
	public String toString() {
		return "Metrics [id=" + id + ", displayName=" + displayName + ", metricsOrder=" + metricsOrder + ", report="
				+ report + ", entityId=" + entityId + ", display=" + display + "]";
	}

	public Metrics(long id, @NotNull(message = "Display name should not be empty") String displayName,
			@NotNull(message = "Prompt Order should not be empty") BigInteger metricsOrder, Reports report,
			BigInteger entityId, @NotNull(message = "Prompt Required should not be empty") String display) {
		super();
		this.id = id;
		this.displayName = displayName;
		this.metricsOrder = metricsOrder;
		this.report = report;
		this.entityId = entityId;
		this.display = display;
	}

}
