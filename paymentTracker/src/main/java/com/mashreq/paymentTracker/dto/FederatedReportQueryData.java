package com.mashreq.paymentTracker.dto;
import java.util.ArrayList;
import java.util.List;

public class FederatedReportQueryData {
	
	   private Long cannedReportInstanceId;
	   private Long                               queryExecutionTime;
	   private List<ReportOutput> queryData = new ArrayList<ReportOutput>();
	   private List<Integer>                      filteredMetricIndexes;
	   
	   
	public Long getCannedReportInstanceId() {
		return cannedReportInstanceId;
	}
	public void setCannedReportInstanceId(Long cannedReportInstanceId) {
		this.cannedReportInstanceId = cannedReportInstanceId;
	}
	public Long getQueryExecutionTime() {
		return queryExecutionTime;
	}
	public void setQueryExecutionTime(Long queryExecutionTime) {
		this.queryExecutionTime = queryExecutionTime;
	}
	public List<ReportOutput> getQueryData() {
		return queryData;
	}
	public void setQueryData(List<ReportOutput> queryData) {
		this.queryData = queryData;
	}
	public List<Integer> getFilteredMetricIndexes() {
		return filteredMetricIndexes;
	}
	public void setFilteredMetricIndexes(List<Integer> filteredMetricIndexes) {
		this.filteredMetricIndexes = filteredMetricIndexes;
	}
	   
	   
	   
	   


}
