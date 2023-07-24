package com.mashreq.paymentTracker.model;

import java.io.Serializable;

import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@DynamicUpdate
@Table(name = "conf_comp_country")
@NamedQueries({
		@NamedQuery(name = "componentsCountry.findBycomponentsId", query = "Select country from ComponentsCountry country join Components component on country.components =component.id where component.id =: reportComponentId ") })
public class ComponentsCountry implements Serializable {
	private static final long serialVersionUID = -4971580442326027196L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotNull(message = "Country should not be empty")
	@Column(name = "country")
	private String country;

	@ManyToOne(targetEntity = DataSource.class)
	@JoinColumn(name = "data_source_id")
	private DataSource dataSourceConfig;

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

	public DataSource getDataSourceConfig() {
		return dataSourceConfig;
	}

	public void setDataSourceConfig(DataSource dataSourceConfig) {
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
			DataSource dataSourceConfig, Components components) {
		super();
		this.id = id;
		this.country = country;
		this.dataSourceConfig = dataSourceConfig;
		this.components = components;
	}

}