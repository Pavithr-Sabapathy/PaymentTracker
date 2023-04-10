package com.mashreq.paymentTracker.model;

import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@DynamicUpdate
@Table(name = "conf_comp_country")
public class ComponentsCountry {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotNull(message = "Display name should not be empty")
	@Column(name = "country")
	private String country;

	@ManyToOne(targetEntity = DataSourceConfig.class)
	@JoinColumn(name = "data_source_id")
	private DataSourceConfig dataSourceConfig;

	@OneToOne(targetEntity = Components.class)
	@JoinColumn(name = "rept_comp_id")
	private Components components;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public DataSourceConfig getDataSourceConfig() {
		return dataSourceConfig;
	}

	public void setDataSourceConfig(DataSourceConfig dataSourceConfig) {
		this.dataSourceConfig = dataSourceConfig;
	}

	public Components getComponents() {
		return components;
	}

	public void setComponents(Components components) {
		this.components = components;
	}

	@Override
	public String toString() {
		return "ComponentsCountry [id=" + id + ", country=" + country + ", dataSourceConfig=" + dataSourceConfig
				+ ", components=" + components + "]";
	}

	public ComponentsCountry() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ComponentsCountry(long id, @NotNull(message = "Display name should not be empty") String country,
			DataSourceConfig dataSourceConfig, Components components) {
		super();
		this.id = id;
		this.country = country;
		this.dataSourceConfig = dataSourceConfig;
		this.components = components;
	}

}