package com.mashreq.paymentTracker.model;

import java.io.Serializable;

import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@DynamicUpdate
@Table(name = "conf_linked_report_info")
@NamedQueries({
		@NamedQuery(name = "linkedReport.findAllByReportId", query = "Select linkedReport from LinkedReportInfo linkedReport join Report report on linkedReport.report = report.id where report.id =: reportId"),
		@NamedQuery(name = "linkedReport.findById", query = "Select linkedReport from LinkedReportInfo linkedReport where id = :linkedReportId"),
		@NamedQuery(name = "linkedReport.findByModuleId", query = "select linkedReport from LinkedReportInfo linkedReport join ApplicationModule module on linkedReport.module = module.id where module.id =: moduleId") })
public class LinkedReportInfo implements Serializable {
	private static final long serialVersionUID = -1145019047768806678L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "link_name")
	private String linkName;

	@Column(name = "link_Description")
	private String linkDescription;

	@ManyToOne
	@JoinColumn(name = "report_id")
	private Report report;

	@OneToOne
	@JoinColumn(name = "linked_report_id")
	private Report linkedReport;

	@OneToOne
	@JoinColumn(name = "source_metric_id")
	private Metrics sourceMetrics;

	@Column(name = "active")
	private String active;

	@OneToOne
	@JoinColumn(name = "comp_id")
	private Components componentId;

	@OneToOne
	@JoinColumn(name = "comp_det_id")
	private ComponentDetails componentDetailId;

	@OneToOne
	@JoinColumn(name = "module_id")
	private ApplicationModule module;

	public LinkedReportInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public Report getLinkedReport() {
		return linkedReport;
	}

	public void setLinkedReport(Report linkedReport) {
		this.linkedReport = linkedReport;
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

	public Components getComponentId() {
		return componentId;
	}

	public void setComponentId(Components componentId) {
		this.componentId = componentId;
	}

	public ComponentDetails getComponentDetailId() {
		return componentDetailId;
	}

	public void setComponentDetailId(ComponentDetails componentDetailId) {
		this.componentDetailId = componentDetailId;
	}

	public ApplicationModule getModule() {
		return module;
	}

	public void setModule(ApplicationModule module) {
		this.module = module;
	}

	public LinkedReportInfo(long id, String linkName, String linkDescription, Report report, Report linkedReport,
			Metrics sourceMetrics, String active, Components componentId, ComponentDetails componentDetailId,
			ApplicationModule module) {
		super();
		this.id = id;
		this.linkName = linkName;
		this.linkDescription = linkDescription;
		this.report = report;
		this.linkedReport = linkedReport;
		this.sourceMetrics = sourceMetrics;
		this.active = active;
		this.componentId = componentId;
		this.componentDetailId = componentDetailId;
		this.module = module;
	}

	@Override
	public String toString() {
		return "LinkedReportInfo [id=" + id + ", linkName=" + linkName + ", linkDescription=" + linkDescription
				+ ", report=" + report + ", linkedReport=" + linkedReport + ", sourceMetrics=" + sourceMetrics
				+ ", active=" + active + ", componentId=" + componentId + ", componentDetailId=" + componentDetailId
				+ ", module=" + module + "]";
	}

}