package com.mashreq.paymentTracker.dto;

import java.io.Serializable;
import java.util.Set;

public class CannedReportInstance implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1792951568969291871L;
	private Long id;
	private Long reportId;
	private Long roleId;
	private String roleName;
	private String reportName;
	private String reportDescription;
	private Set<CannedReportInstancePrompt> reportInstancePrompts;
	private Set<CannedReportInstanceMetric> reportInstanceMetrics;
	private Set<CannedReportInstanceComponent> reportInstanceComponents;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getReportId() {
		return reportId;
	}

	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getReportDescription() {
		return reportDescription;
	}

	public void setReportDescription(String reportDescription) {
		this.reportDescription = reportDescription;
	}

	public Set<CannedReportInstancePrompt> getReportInstancePrompts() {
		return reportInstancePrompts;
	}

	public void setReportInstancePrompts(Set<CannedReportInstancePrompt> reportInstancePrompts) {
		this.reportInstancePrompts = reportInstancePrompts;
	}

	public Set<CannedReportInstanceMetric> getReportInstanceMetrics() {
		return reportInstanceMetrics;
	}

	public void setReportInstanceMetrics(Set<CannedReportInstanceMetric> reportInstanceMetrics) {
		this.reportInstanceMetrics = reportInstanceMetrics;
	}

	public Set<CannedReportInstanceComponent> getReportInstanceComponents() {
		return reportInstanceComponents;
	}

	public void setReportInstanceComponents(Set<CannedReportInstanceComponent> reportInstanceComponents) {
		this.reportInstanceComponents = reportInstanceComponents;
	}

}