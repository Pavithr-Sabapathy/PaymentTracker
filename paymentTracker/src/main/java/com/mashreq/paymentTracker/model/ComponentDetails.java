package com.mashreq.paymentTracker.model;

import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@DynamicUpdate
@Table(name = "conf_rpt_comp_det")
public class ComponentDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotNull
	@Column(name = "query")
	private String query;

	@NotNull
	@Column(name = "query_Key")
	private String queryKey;

	@NotNull
	@ManyToOne(targetEntity = Components.class)
	@JoinColumn(name = "report_comp_id")
	private Components components;

	public ComponentDetails() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ComponentDetails(long id, @NotNull String query, @NotNull String queryKey, @NotNull Components components) {
		super();
		this.id = id;
		this.query = query;
		this.queryKey = queryKey;
		this.components = components;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
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

	public Components getComponents() {
		return components;
	}

	public void setComponents(Components components) {
		this.components = components;
	}

	@Override
	public String toString() {
		return "ComponentDetails [id=" + id + ", query=" + query + ", queryKey=" + queryKey + ", components="
				+ components + "]";
	}

}