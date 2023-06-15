package com.mashreq.paymentTracker.dto;

import java.io.Serializable;

public class ReportComponentDetailDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -696704907424810167L;
	private Long id;
	private Long reportComponentId;
	private String query;
	private String queryKey;
	private ReportComponentDTO reportComponent;

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

	public Long getReportComponentId() {
		return reportComponentId;
	}

	public void setReportComponentId(Long reportComponentId) {
		this.reportComponentId = reportComponentId;
	}

	public ReportComponentDTO getReportComponent() {
		return reportComponent;
	}

	public void setReportComponent(ReportComponentDTO reportComponent) {
		this.reportComponent = reportComponent;
	}

	@Override
	public String toString() {
		return "ReportComponentDetailDTO [id=" + id + ", reportComponentId=" + reportComponentId + ", query=" + query
				+ ", queryKey=" + queryKey + ", reportComponent=" + reportComponent + "]";
	}

}
