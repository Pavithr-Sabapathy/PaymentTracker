package com.mashreq.paymentTracker.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "conf_report")
@NamedQueries({ @NamedQuery(name = "report.findByReportName", query = "FROM Report rep where reportName = :reportName"),
		@NamedQuery(name = "report.getAllActiveReports", query = "From Report rep where active='Y'"),
		@NamedQuery(name = "report.findByModuleId",query = "From Report where moduleId = :moduleId"),
		@NamedQuery(name = "report.findReportByModule",query = "select report from Report report join ApplicationModule module on report.moduleId = module.id where module.moduleName =:moduleName")})
public class Report implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "Report name should not be empty")
	@Column(name = "report_name")
	private String reportName;

	@NotNull(message = "Display name should not be empty")
	@Column(name = "display_name")
	private String displayName;

	@Column(name = "rep_description")
	private String reportDescription;

	@NotNull(message = "Category should not be empty")
	@Column(name = "category")
	private String reportCategory;

	@NotNull(message = "active status should not be empty")
	@Column(name = "active")
	private String active;

	@NotNull(message = "valid should not be empty")
	@Column(name = "valid")
	private String valid;

	@Column(name = "module_id")
	private long moduleId;

	@Column(name = "connector_key")
	private String connectorKey;

	@OneToMany(mappedBy = "report", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	List<Prompts> promptList = new ArrayList<Prompts>();

	@OneToMany(mappedBy = "report", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	List<Metrics> metricsList = new ArrayList<Metrics>();

	public Report() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Report(Long id, @NotNull(message = "Report name should not be empty") String reportName,
			@NotNull(message = "Display name should not be empty") String displayName, String reportDescription,
			@NotNull(message = "Category should not be empty") String reportCategory,
			@NotNull(message = "active status should not be empty") String active,
			@NotNull(message = "valid should not be empty") String valid, List<Prompts> promptList,
			List<Metrics> metricsList) {
		super();
		this.id = id;
		this.reportName = reportName;
		this.displayName = displayName;
		this.reportDescription = reportDescription;
		this.reportCategory = reportCategory;
		this.active = active;
		this.valid = valid;
		this.promptList = promptList;
		this.metricsList = metricsList;
	}

	public Report(@NotNull(message = "Display name should not be empty") String displayName) {
		super();
		this.displayName = displayName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getReportDescription() {
		return reportDescription;
	}

	public void setReportDescription(String reportDescription) {
		this.reportDescription = reportDescription;
	}

	public String getReportCategory() {
		return reportCategory;
	}

	public void setReportCategory(String reportCategory) {
		this.reportCategory = reportCategory;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getValid() {
		return valid;
	}

	public void setValid(String valid) {
		this.valid = valid;
	}

	public List<Prompts> getPromptList() {
		return promptList;
	}

	public void setPromptList(List<Prompts> promptList) {
		this.promptList = promptList;
	}

	public List<Metrics> getMetricsList() {
		return metricsList;
	}

	public void setMetricsList(List<Metrics> metricsList) {
		this.metricsList = metricsList;
	}

	public long getModuleId() {
		return moduleId;
	}

	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}

	public String getConnectorKey() {
		return connectorKey;
	}

	public void setConnectorKey(String connectorKey) {
		this.connectorKey = connectorKey;
	}

	@Override
	public String toString() {
		return "Report [id=" + id + ", reportName=" + reportName + ", displayName=" + displayName
				+ ", reportDescription=" + reportDescription + ", reportCategory=" + reportCategory + ", active="
				+ active + ", valid=" + valid + ", moduleId=" + moduleId + ", connectorKey=" + connectorKey
				+ ", promptList=" + promptList + ", metricsList=" + metricsList + ", getId()=" + getId()
				+ ", getReportName()=" + getReportName() + ", getDisplayName()=" + getDisplayName()
				+ ", getReportDescription()=" + getReportDescription() + ", getReportCategory()=" + getReportCategory()
				+ ", getActive()=" + getActive() + ", getValid()=" + getValid() + ", getPromptList()=" + getPromptList()
				+ ", getMetricsList()=" + getMetricsList() + ", getModuleId()=" + getModuleId() + ", getConnectorKey()="
				+ getConnectorKey() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}

}