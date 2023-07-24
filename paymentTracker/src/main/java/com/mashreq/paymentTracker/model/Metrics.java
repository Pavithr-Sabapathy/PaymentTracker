package com.mashreq.paymentTracker.model;

import java.io.Serializable;
import java.math.BigInteger;

import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@DynamicUpdate
@Table(name = "conf_Metric")
@NamedQueries({ @NamedQuery(name = "metrics.findAll", query = "FROM Metrics"),
		@NamedQuery(name = "metric.findMetricsByReportId", query = "select metric from Metrics metric join Report report on metric.report = report.id where report.id =: reportId"),
		@NamedQuery(name = "metrics.findMetricsOrderByReportId", query = "select max(metrics.metricsOrder)  from Metrics metrics join Report report on metrics.report = report.id where report.id =:reportId") })
public class Metrics implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotNull(message = "Display name should not be empty")
	@Column(name = "display_name")
	private String displayName;

	@NotNull(message = "Prompt Order should not be empty")
	@Column(name = "Metrics_Order")
	private BigInteger metricsOrder;

	@ManyToOne(targetEntity = Report.class)
	@JsonBackReference
	@JoinColumn(name = "report_id")
	private Report report;

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

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
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
			@NotNull(message = "Prompt Order should not be empty") BigInteger metricsOrder, Report report,
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
