package com.mashreq.paymentTracker.dto;

import java.io.Serializable;

public class ComponentDetailDTO implements Serializable {

	private static final long serialVersionUID = -6427126449077229758L;
	private Long id;
	private String query;
	private String queryKey;

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

	@Override
	public String toString() {
		return "ComponentDetailDTO [id=" + id + ", query=" + query + ", queryKey=" + queryKey + "]";
	}

}