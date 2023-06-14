package com.mashreq.paymentTracker.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import com.mashreq.paymentTracker.utility.CheckType;

public class ReportQueryInfoDTO implements Serializable {

	private static final long serialVersionUID = -8291813106120523477L;
	private Long id;
	private String dataSourceName;
	private String queryKey;
	private String executedQuery;
	private Long queryExecutionTime;
	private Date startTime;
	private Date endTime;
	private CheckType dataFound;
	private String failureCause;
	private Long executionId;
	private LinkedHashMap<String, List<String>> promptKeyValueMap;

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

	public Long getExecutionId() {
		return executionId;
	}

	public void setExecutionId(Long executionId) {
		this.executionId = executionId;
	}

	public LinkedHashMap<String, List<String>> getPromptKeyValueMap() {
		return promptKeyValueMap;
	}

	public void setPromptKeyValueMap(LinkedHashMap<String, List<String>> promptKeyValueMap) {
		this.promptKeyValueMap = promptKeyValueMap;
	}

	@Override
	public String toString() {
		return "QueryInfoVO [id=" + id + ", dataSourceName=" + dataSourceName + ", queryKey=" + queryKey
				+ ", executedQuery=" + executedQuery + ", queryExecutionTime=" + queryExecutionTime + ", startTime="
				+ startTime + ", endTime=" + endTime + ", dataFound=" + dataFound + ", failureCause=" + failureCause
				+ ", executionId=" + executionId + "]";
	}

}
