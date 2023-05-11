package com.mashreq.paymentTracker.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotEmpty;

public class ComponentDetailsRequestDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2526378933880844413L;

	@NotEmpty
	private String query;
	@NotEmpty
	private String queryKey;
	@NotEmpty
	private long compReportId;

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

	public long getCompReportId() {
		return compReportId;
	}

	public void setCompReportId(long compReportId) {
		this.compReportId = compReportId;
	}

	public ComponentDetailsRequestDTO(){

	}
	
	public ComponentDetailsRequestDTO(@NotEmpty String query, @NotEmpty String queryKey, @NotEmpty long compReportId) {
		super();
		this.query = query;
		this.queryKey = queryKey;
		this.compReportId = compReportId;
	}

	@Override
	public String toString() {
		return "ComponentDetailsRequestDTO [query=" + query + ", queryKey=" + queryKey + ", compReportId="
				+ compReportId + "]";
	}

}