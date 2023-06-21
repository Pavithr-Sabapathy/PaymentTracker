package com.mashreq.paymentTracker.model;

import java.math.BigInteger;

import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@DynamicUpdate
@Table(name = "conf_data_source")
@NamedQueries ({@NamedQuery (name = "activeDataSource",
query = "select ds from DataSource ds where ds.active=:activeStatus")})
public class DataSource {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotNull(message = "DataSource name should not be empty")
	@Column(name = "ds_name")
	private String name;

	@Column(name = "ds_description")
	private String description;

	@NotNull(message = "DataSource provider should not be empty")
	@Column(name = "ds_provider")
	private BigInteger provider;

	@NotNull(message = "DataSource username should not be empty")
	@Column(name = "ds_username")
	private String userName;

	@NotNull(message = "Password should not be empty")
	@Column(name = "ds_password")
	private String password;

	@Column(name = "password_encrypted")
	private String encryptedPassword;

	@NotNull(message = "Server_ip should not be empty")
	@Column(name = "server_ip")
	private String serverIP;

	@NotNull(message = "Port name should not be empty")
	@Column(name = "ds_port")
	private BigInteger port;

	@NotNull(message = "DataSource schema name should not be empty")
	@Column(name = "ds_schema_name")
	private String schemaName;

	@NotNull(message = "DataSource owner should not be empty")
	@Column(name = "ds_owner")
	private String owner;

	@NotNull(message = "DataSource active should not be empty")
	@Column(name = "active")
	private String active;

	@Column(name = "ds_country")
	private String country;

	public DataSource() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DataSource(long id, @NotNull(message = "DataSource name should not be empty") String name,
			String description, @NotNull(message = "DataSource provider should not be empty") BigInteger provider,
			@NotNull(message = "DataSource username should not be empty") String userName,
			@NotNull(message = "Password should not be empty") String password, String encryptedPassword,
			@NotNull(message = "Server_ip should not be empty") String serverIP,
			@NotNull(message = "Port name should not be empty") BigInteger port,
			@NotNull(message = "DataSource schema name should not be empty") String schemaName,
			@NotNull(message = "DataSource owner should not be empty") String owner,
			@NotNull(message = "DataSource active should not be empty") String active, String country) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.provider = provider;
		this.userName = userName;
		this.password = password;
		this.encryptedPassword = encryptedPassword;
		this.serverIP = serverIP;
		this.port = port;
		this.schemaName = schemaName;
		this.owner = owner;
		this.active = active;
		this.country = country;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigInteger getProvider() {
		return provider;
	}

	public void setProvider(BigInteger provider) {
		this.provider = provider;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
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

	@Override
	public String toString() {
		return "DataSource [id=" + id + ", name=" + name + ", description=" + description + ", provider=" + provider
				+ ", userName=" + userName + ", password=" + password + ", encryptedPassword=" + encryptedPassword
				+ ", serverIP=" + serverIP + ", port=" + port + ", schemaName=" + schemaName + ", owner=" + owner
				+ ", active=" + active + ", country=" + country + "]";
	}

}
