package com.mashreq.paymentTracker.model;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.geo.Metric;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@DynamicUpdate
@Table(name = "conf_linked_report_info")
public class LinkedReportInfo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "link_name")
	private String linkName;

	@Column(name = "link_Description")
	private String linkDescription;

	@Column(name = "report_id")
	private long reportId;

	@Column(name = "linked_report_id")
	private long linkedReportId;

	@OneToOne
	@JoinColumn(name = "source_metric_id")
	private Metrics sourceMetrics;

	@Column(name = "active")
	private String active;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getReportId() {
		return reportId;
	}

	public void setReportId(long reportId) {
		this.reportId = reportId;
	}

	public long getLinkedReportId() {
		return linkedReportId;
	}

	public void setLinkedReportId(long linkedReportId) {
		this.linkedReportId = linkedReportId;
	}

	public Metrics getSourceMetrics() {
		return sourceMetrics;
	}

	public void setSourceMetrics(Metrics sourceMetrics) {
		this.sourceMetrics = sourceMetrics;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getLinkName() {
		return linkName;
	}

	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}

	public String getLinkDescription() {
		return linkDescription;
	}

	public void setLinkDescription(String linkDescription) {
		this.linkDescription = linkDescription;
	}

	@Override
	public String toString() {
		return "LinkedReportInfo [id=" + id + ", linkName=" + linkName + ", linkDescription=" + linkDescription
				+ ", reportId=" + reportId + ", linkedReportId=" + linkedReportId + ", sourceMetrics=" + sourceMetrics
				+ ", active=" + active + "]";
	}

	public LinkedReportInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LinkedReportInfo(long id, String linkName, String linkDescription, long reportId, long linkedReportId,
			Metrics sourceMetrics, String active) {
		super();
		this.id = id;
		this.linkName = linkName;
		this.linkDescription = linkDescription;
		this.reportId = reportId;
		this.linkedReportId = linkedReportId;
		this.sourceMetrics = sourceMetrics;
		this.active = active;
	}

}