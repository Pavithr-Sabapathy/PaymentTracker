package com.mashreq.paymentTracker.model;

import java.math.BigInteger;
import java.util.Objects;

import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@DynamicUpdate
@Table(name = "conf_data_source")
public class DataSourceConfig {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@NotNull(message = "DataSource name should not be empty")
	@Column(name = "ds_name")
	private String dataSourceName;
	
	@Column(name = "ds_description")
	private String dataSourceDescription;
	
	@NotNull(message = "DataSource provider should not be empty")
	@Column(name = "ds_provider")
	private Long dataSourceProvider;
	
	@NotNull(message = "DataSource username should not be empty")
	@Column(name = "ds_username")
	private String dataSourceUserName;
	
	@NotNull(message = "Password should not be empty")
	@Column(name = "ds_password")
	private String dataSourcePassword;
	
	@Column(name = "password_encrypted")
	private String encryptedPassword;

	@NotNull(message = "Server_ip should not be empty")
	@Column(name = "server_ip")
	private String serverIP;
	
	@NotNull(message = "Port name should not be empty")
	@Column(name = "ds_port")
	private BigInteger  port;
	
	@NotNull(message = "DataSource schema name should not be empty")
	@Column(name = "ds_schema_name")
	private String dataSourceSchemaName;
	
	@NotNull(message = "DataSource owner should not be empty")
	@Column(name = "ds_owner")
	private String dataSourceOwner;
	
	@NotNull(message = "DataSource active should not be empty")
	@Column(name = "active")
	private String active;
	
	@Column(name = "ds_country")
	private String country;
	
	@Override
	public int hashCode() {
		return Objects.hash(active, country, dataSourceDescription, dataSourceName, dataSourceOwner, dataSourcePassword,
				dataSourceProvider, dataSourceSchemaName, dataSourceUserName, encryptedPassword, id, port, serverIP);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataSourceConfig other = (DataSourceConfig) obj;
		return Objects.equals(active, other.active) && Objects.equals(country, other.country)
				&& Objects.equals(dataSourceDescription, other.dataSourceDescription)
				&& Objects.equals(dataSourceName, other.dataSourceName)
				&& Objects.equals(dataSourceOwner, other.dataSourceOwner)
				&& Objects.equals(dataSourcePassword, other.dataSourcePassword)
				&& Objects.equals(dataSourceProvider, other.dataSourceProvider)
				&& Objects.equals(dataSourceSchemaName, other.dataSourceSchemaName)
				&& Objects.equals(dataSourceUserName, other.dataSourceUserName)
				&& Objects.equals(encryptedPassword, other.encryptedPassword) && id == other.id
				&& Objects.equals(port, other.port) && Objects.equals(serverIP, other.serverIP);
	}

	@Override
	public String toString() {
		return "DataSourceConfig [id=" + id + ", dataSourceName=" + dataSourceName + ", dataSourceDescription="
				+ dataSourceDescription + ", dataSourceProvider=" + dataSourceProvider + ", dataSourceUserName="
				+ dataSourceUserName + ", dataSourcePassword=" + dataSourcePassword + ", encryptedPassword="
				+ encryptedPassword + ", serverIP=" + serverIP + ", port=" + port + ", dataSourceSchemaName="
				+ dataSourceSchemaName + ", dataSourceOwner=" + dataSourceOwner + ", active=" + active + ", country="
				+ country + "]";
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	public String getDataSourceDescription() {
		return dataSourceDescription;
	}

	public void setDataSourceDescription(String dataSourceDescription) {
		this.dataSourceDescription = dataSourceDescription;
	}

	public Long getDataSourceProvider() {
		return dataSourceProvider;
	}

	public void setDataSourceProvider(Long dataSourceProvider) {
		this.dataSourceProvider = dataSourceProvider;
	}

	public String getDataSourceUserName() {
		return dataSourceUserName;
	}

	public void setDataSourceUserName(String dataSourceUserName) {
		this.dataSourceUserName = dataSourceUserName;
	}

	public String getDataSourcePassword() {
		return dataSourcePassword;
	}

	public void setDataSourcePassword(String dataSourcePassword) {
		this.dataSourcePassword = dataSourcePassword;
	}

	public String getEncryptedPassword() {
		return encryptedPassword;
	}

	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}

	public String getServerIP() {
		return serverIP;
	}

	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}

	public BigInteger getPort() {
		return port;
	}

	public void setPort(BigInteger port) {
		this.port = port;
	}

	public String getDataSourceSchemaName() {
		return dataSourceSchemaName;
	}

	public void setDataSourceSchemaName(String dataSourceSchemaName) {
		this.dataSourceSchemaName = dataSourceSchemaName;
	}

	public String getDataSourceOwner() {
		return dataSourceOwner;
	}

	public void setDataSourceOwner(String dataSourceOwner) {
		this.dataSourceOwner = dataSourceOwner;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public DataSourceConfig() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DataSourceConfig(long id, @NotNull(message = "DataSource name should not be empty") String dataSourceName,
			String dataSourceDescription,
			@NotNull(message = "DataSource provider should not be empty") Long dataSourceProvider,
			@NotNull(message = "DataSource username should not be empty") String dataSourceUserName,
			@NotNull(message = "Password should not be empty") String dataSourcePassword, String encryptedPassword,
			@NotNull(message = "Server_ip should not be empty") String serverIP,
			@NotNull(message = "Port name should not be empty") BigInteger port,
			@NotNull(message = "DataSource schema name should not be empty") String dataSourceSchemaName,
			@NotNull(message = "DataSource owner should not be empty") String dataSourceOwner,
			@NotNull(message = "DataSource active should not be empty") String active, String country) {
		super();
		this.id = id;
		this.dataSourceName = dataSourceName;
		this.dataSourceDescription = dataSourceDescription;
		this.dataSourceProvider = dataSourceProvider;
		this.dataSourceUserName = dataSourceUserName;
		this.dataSourcePassword = dataSourcePassword;
		this.encryptedPassword = encryptedPassword;
		this.serverIP = serverIP;
		this.port = port;
		this.dataSourceSchemaName = dataSourceSchemaName;
		this.dataSourceOwner = dataSourceOwner;
		this.active = active;
		this.country = country;
	}
	
	
}
