package com.mashreq.paymentTracker.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ReportInstanceDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1829584680351534223L;
	private long reportId;
	private String reportName;
	private long moduleId;
	private String roleName;
	private long roleId;
	private long userId;
	private String userName;
	private List<ReportPromptsInstanceDTO> promptsList;
	private Date creationDate;

	public long getReportId() {
		return reportId;
	}

	public void setReportId(long reportId) {
		this.reportId = reportId;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public long getModuleId() {
		return moduleId;
	}

	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public long getRoleId() {
		return roleId;
	}

	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public List<ReportPromptsInstanceDTO> getPromptsList() {
		return promptsList;
	}

	public void setPromptsList(List<ReportPromptsInstanceDTO> promptsList) {
		this.promptsList = promptsList;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	public String toString() {
		return "ReportInstanceDTO [reportId=" + reportId + ", reportName=" + reportName + ", moduleId=" + moduleId
				+ ", roleName=" + roleName + ", roleId=" + roleId + ", userId=" + userId + ", userName=" + userName
				+ ", promptsList=" + promptsList + "]";
	}

}