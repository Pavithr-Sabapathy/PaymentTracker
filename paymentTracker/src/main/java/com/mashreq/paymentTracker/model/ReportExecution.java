package com.mashreq.paymentTracker.model;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ops_rpt_exec")
public class ReportExecution implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 620054458987101101L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "user_name")
	private String userName;
	@Column(name = "role_name")
	private String roleName;
	@Column(name = "start_date")
	private Date startDate;
	@Column(name = "end_date")
	private Date endDate;
	@Column(name = "failure_cause", columnDefinition = "TEXT")
	private String failureCause;
	@Column(name = "link_execution")
	private String linkExecution;

	@Column(name = "execution_status")
	private String executionStatus;

	@Column(name = "execution_time")
	private Long executionTime;

	@OneToOne
	@JoinColumn(name = "report_inst_id")
	private ReportInstance reportInstance;
	@OneToOne
	@JoinColumn(name = "report_id")
	private Report report;
	@ManyToOne
	@JoinColumn(name = "module_id")
	private ApplicationModule module;
	@OneToOne
	@JoinColumn(name = "user_id")
	private Users user;
	@OneToOne
	@JoinColumn(name = "role_id")
	private Roles role;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
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

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public ApplicationModule getModule() {
		return module;
	}

	public void setModule(ApplicationModule module) {
		this.module = module;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public Roles getRole() {
		return role;
	}

	public void setRole(Roles role) {
		this.role = role;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setReportInstance(ReportInstance reportInstance) {
		this.reportInstance = reportInstance;
	}

	public ReportInstance getReportInstance() {
		return reportInstance;
	}

	public String getExecutionStatus() {
		return executionStatus;
	}

	public void setExecutionStatus(String executionStatus) {
		this.executionStatus = executionStatus;
	}

	public String getLinkExecution() {
		return linkExecution;
	}

	public void setLinkExecution(String linkExecution) {
		this.linkExecution = linkExecution;
	}

	public String getFailureCause() {
		return failureCause;
	}

	public void setFailureCause(String failureCause) {
		this.failureCause = failureCause;
	}

	public Long getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(Long executionTime) {
		this.executionTime = executionTime;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}


}
