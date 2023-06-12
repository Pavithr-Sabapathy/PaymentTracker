package com.mashreq.paymentTracker.dto;

import java.io.Serializable;

import com.mashreq.paymentTracker.type.CountryType;

public class ReportContext implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -27345285646508102L;
	private Long reportId;
	private Long userId;
	private Boolean linkedReport = false;
	private ReportInstanceDTO reportInstance;
	private Long executionId;
	private Long roleId;
	private Long moduleId;
	private String reportName;
	private String userName;
	private String roleName;
	private Long linkInstanceId;
	private CountryType country = CountryType.UAE;
	private String linkReference;
	public Long getReportId() {
		return reportId;
	}
	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Boolean getLinkedReport() {
		return linkedReport;
	}
	public void setLinkedReport(Boolean linkedReport) {
		this.linkedReport = linkedReport;
	}
	public ReportInstanceDTO getReportInstance() {
		return reportInstance;
	}
	public void setReportInstance(ReportInstanceDTO reportInstance) {
		this.reportInstance = reportInstance;
	}
	public Long getExecutionId() {
		return executionId;
	}
	public void setExecutionId(Long executionId) {
		this.executionId = executionId;
	}
	public Long getRoleId() {
		return roleId;
	}
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
	public Long getModuleId() {
		return moduleId;
	}
	public void setModuleId(Long moduleId) {
		this.moduleId = moduleId;
	}
	public String getReportName() {
		return reportName;
	}
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public Long getLinkInstanceId() {
		return linkInstanceId;
	}
	public void setLinkInstanceId(Long linkInstanceId) {
		this.linkInstanceId = linkInstanceId;
	}
	public CountryType getCountry() {
		return country;
	}
	public void setCountry(CountryType country) {
		this.country = country;
	}
	public String getLinkReference() {
		return linkReference;
	}
	public void setLinkReference(String linkReference) {
		this.linkReference = linkReference;
	}

}
