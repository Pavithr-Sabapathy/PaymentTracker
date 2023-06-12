package com.mashreq.paymentTracker.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ops_report_inst")
public class ReportInstance implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "report_name")
	private String reportName;
	@Column(name = "report_description")
	private String reportDesc;

	@Column(name = "create_date")
	private Date createDate;
	@OneToOne
	@JoinColumn(name = "user_id")
	private Users user;

	@OneToOne
	@JoinColumn(name = "report_id")
	private Report report;

	@OneToOne
	@JoinColumn(name = "role_id")
	private Roles role;

	@ManyToOne
	@JoinColumn(name = "module_id")
	private ApplicationModule module;

	@OneToMany(mappedBy = "reportInstance", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ReportInstancePrompt> reportInstancePrompts;

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

	public String getReportDesc() {
		return reportDesc;
	}

	public void setReportDesc(String reportDesc) {
		this.reportDesc = reportDesc;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public Roles getRole() {
		return role;
	}

	public void setRole(Roles role) {
		this.role = role;
	}
	public ApplicationModule getModule() {
		return module;
	}

	public void setModule(ApplicationModule module) {
		this.module = module;
	}

	public List<ReportInstancePrompt> getReportInstancePrompts() {
		return reportInstancePrompts;
	}

	public void setReportInstancePrompts(List<ReportInstancePrompt> reportInstancePrompts) {
		this.reportInstancePrompts = reportInstancePrompts;
	}

}
