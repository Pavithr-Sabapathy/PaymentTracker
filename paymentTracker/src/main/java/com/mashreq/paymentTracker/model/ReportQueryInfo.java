package com.mashreq.paymentTracker.model;

import java.io.Serializable;
import java.util.Date;

import com.mashreq.paymentTracker.utility.CheckType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ops_rpt_query_info")

public class ReportQueryInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "data_source_name")
	private String dataSourceName;
	@Column(name = "query_key")
	private String queryKey;
	@Column(name = "executed_query")
	private String executedQuery;
	@Column(name = "query_execution_time")
	private Long queryExecutionTime;
	@Column(name = "start_time")
	private Date startTime;
	@Column(name = "end_time")
	private Date endTime;
	@Column(name = "data_found")
	private CheckType dataFound = CheckType.NO;;
	@Column(name = "failure_cause")
	private String failureCause;

	@ManyToOne
	@JoinColumn(name = "execution_id")
	private ReportExecution execution;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	public String getQueryKey() {
		return queryKey;
	}

	public void setQueryKey(String queryKey) {
		this.queryKey = queryKey;
	}

	public String getExecutedQuery() {
		return executedQuery;
	}

	public void setExecutedQuery(String executedQuery) {
		this.executedQuery = executedQuery;
	}

	public Long getQueryExecutionTime() {
		return queryExecutionTime;
	}

	public void setQueryExecutionTime(Long queryExecutionTime) {
		this.queryExecutionTime = queryExecutionTime;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public CheckType getDataFound() {
		return dataFound;
	}

	public void setDataFound(CheckType dataFound) {
		this.dataFound = dataFound;
	}

	public String getFailureCause() {
		return failureCause;
	}

	public void setFailureCause(String failureCause) {
		this.failureCause = failureCause;
	}

	public ReportExecution getExecution() {
		return execution;
	}

	public void setExecution(ReportExecution execution) {
		this.execution = execution;
	}

	@Override
	public String toString() {
		return "ReportQueryInfo [id=" + id + ", dataSourceName=" + dataSourceName + ", queryKey=" + queryKey
				+ ", executedQuery=" + executedQuery + ", queryExecutionTime=" + queryExecutionTime + ", startTime="
				+ startTime + ", endTime=" + endTime + ", dataFound=" + dataFound + ", failureCause=" + failureCause
				+ ", execution=" + execution + "]";
	}

}
