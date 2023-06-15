package com.mashreq.paymentTracker.dto;

import java.io.Serializable;

public class CannedReportInstanceComponentDetail implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5741657453157015322L;

	private Long id;

	private Long cannedReportComponentDetailId;

	private String query;

	private String queryKey;

	private ReportInstanceComponentDTO cannedReportInstanceComponent;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCannedReportComponentDetailId() {
		return cannedReportComponentDetailId;
	}

	public void setCannedReportComponentDetailId(Long cannedReportComponentDetailId) {
		this.cannedReportComponentDetailId = cannedReportComponentDetailId;
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

	public ReportInstanceComponentDTO getCannedReportInstanceComponent() {
		return cannedReportInstanceComponent;
	}

	public void setCannedReportInstanceComponent(ReportInstanceComponentDTO cannedReportInstanceComponent) {
		this.cannedReportInstanceComponent = cannedReportInstanceComponent;
	}

}
