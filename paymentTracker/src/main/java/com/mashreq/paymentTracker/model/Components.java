package com.mashreq.paymentTracker.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@DynamicUpdate
@Table(name = "conf_rpt_comp")
public class Components {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotNull(message = "Display name should not be empty")
	@Column(name = "component_name")
	private String componentName;

	@NotNull(message = "Display name should not be empty")
	@Column(name = "component_key")
	private String componentKey;

	@NotNull(message = "Display name should not be empty")
	@Column(name = "active")
	private String active;

	@ManyToOne(targetEntity = Report.class)
	@JoinColumn(name = "report_id")
	private Report report;

	@OneToOne(mappedBy = "components", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private ComponentsCountry componentsCountry;

	@OneToMany(mappedBy = "components", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ComponentDetails> componentDetailsList = new ArrayList<ComponentDetails>();

	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public String getComponentKey() {
		return componentKey;
	}

	public void setComponentKey(String componentKey) {
		this.componentKey = componentKey;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public ComponentsCountry getComponentsCountry() {
		return componentsCountry;
	}

	public void setComponentsCountry(ComponentsCountry componentsCountry) {
		this.componentsCountry = componentsCountry;
	}

	public List<ComponentDetails> getComponentDetailsList() {
		return componentDetailsList;
	}

	public void setComponentDetailsList(List<ComponentDetails> componentDetailsList) {
		this.componentDetailsList = componentDetailsList;
	}

	public Components() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "Components [id=" + id + ", componentName=" + componentName + ", componentKey=" + componentKey
				+ ", active=" + active + ", report=" + report + ", componentsCountry=" + componentsCountry
				+ ", componentDetailsList=" + componentDetailsList + "]";
	}
	
	public Components(long id, @NotNull(message = "Display name should not be empty") String componentName,
			@NotNull(message = "Display name should not be empty") String componentKey,
			@NotNull(message = "Display name should not be empty") String active, Report report,
			ComponentsCountry componentsCountry, List<ComponentDetails> componentDetailsList) {
		super();
		this.id = id;
		this.componentName = componentName;
		this.componentKey = componentKey;
		this.active = active;
		this.report = report;
		this.componentsCountry = componentsCountry;
		this.componentDetailsList = componentDetailsList;
	}

}