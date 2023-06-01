package com.mashreq.paymentTracker.dto;

public class CannedReportComponentDetail {
	private Long id;

	private String query;

	private String queryKey;

	private CannedReportComponent cannedReportComponent;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getQueryKey() {
		return queryKey;
	}

	public void setQueryKey(String queryKey) {
		this.queryKey = queryKey;
	}

	public CannedReportComponent getCannedReportComponent() {
		return cannedReportComponent;
	}

	public void setCannedReportComponent(CannedReportComponent cannedReportComponent) {
		this.cannedReportComponent = cannedReportComponent;
	}

}
