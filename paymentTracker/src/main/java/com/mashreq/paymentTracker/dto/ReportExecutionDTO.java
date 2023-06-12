package com.mashreq.paymentTracker.dto;

import java.io.Serializable;
import java.util.Date;

import com.mashreq.paymentTracker.type.ExecutionStatusType;

public class ReportExecutionDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7339044532197675307L;
	private Long id;
	private Long reportId;
	private Long reportInstanceId;
	private ExecutionStatusType status;
	private Long userId;
	private Long roleId;
	private String data;// Json equivalent of report output
	private Date startDate;// Date and time
	private Date endDate;// Date and time
	private Long moduleId;
	private String failureCase;
	private Boolean linkExecution = false;
	private String userName;
	private String roleName;
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
	public Long getReportInstanceId() {
		return reportInstanceId;
	}
	public void setReportInstanceId(Long reportInstanceId) {
		this.reportInstanceId = reportInstanceId;
	}
	public ExecutionStatusType getStatus() {
		return status;
	}
	public void setStatus(ExecutionStatusType status) {
		this.status = status;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getRoleId() {
		return roleId;
	}
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Long getModuleId() {
		return moduleId;
	}
	public void setModuleId(Long moduleId) {
		this.moduleId = moduleId;
	}
	public String getFailureCase() {
		return failureCase;
	}
	public void setFailureCase(String failureCase) {
		this.failureCase = failureCase;
	}
	public Boolean getLinkExecution() {
		return linkExecution;
	}
	public void setLinkExecution(Boolean linkExecution) {
		this.linkExecution = linkExecution;
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
	@Override
	public String toString() {
		return "ReportExecutionDTO [id=" + id + ", reportId=" + reportId + ", reportInstanceId=" + reportInstanceId
				+ ", status=" + status + ", userId=" + userId + ", roleId=" + roleId + ", data=" + data + ", startDate="
				+ startDate + ", endDate=" + endDate + ", moduleId=" + moduleId + ", failureCase=" + failureCase
				+ ", linkExecution=" + linkExecution + ", userName=" + userName + ", roleName=" + roleName + "]";
	}
	
}
