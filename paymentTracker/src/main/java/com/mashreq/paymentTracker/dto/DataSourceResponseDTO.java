package com.mashreq.paymentTracker.dto;

import java.io.Serializable;

public class DataSourceResponseDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	private long dataSourceId;
	private String name;
	private String description;
	private Long provider;
	private String userName;
	private String password;
	private String encryptedPassword;
	private String serverIP;
	private Long port;
	private String schemaName;
	private String owner;
	private String active;

	public DataSourceResponseDTO() {
	}

	public DataSourceResponseDTO(long dataSourceId, String name, String description, Long provider, String userName,
			String password, String encryptedPassword, String serverIP, Long port, String schemaName, String owner,
			String active) {
		super();
		this.dataSourceId = dataSourceId;
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
	}

	public long getDataSourceId() {
		return dataSourceId;
	}

	public void setDataSourceId(long dataSourceId) {
		this.dataSourceId = dataSourceId;
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

	public Long getProvider() {
		return provider;
	}

	public void setProvider(Long provider) {
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

	public Long getPort() {
		return port;
	}

	public void setPort(Long port) {
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

	@Override
	public String toString() {
		return "DataSourceDTO [ name=" + name + ", description=" + description + ", provider=" + provider
				+ ", userName=" + userName + ", password=" + password + ", encryptedPassword=" + encryptedPassword
				+ ", serverIP=" + serverIP + ", port=" + port + ", schemaName=" + schemaName + ", owner=" + owner
				+ ", active=" + active + "]";
	}

}
