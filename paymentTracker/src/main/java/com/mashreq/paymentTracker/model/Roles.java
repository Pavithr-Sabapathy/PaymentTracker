package com.mashreq.paymentTracker.model;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "conf_security_roles")
public class Roles implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1895000311865675865L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "name")
	private String roleName;
	@Column(name = "description")
	private String description;
	@Column(name = "status")
	private String status;
	@Column(name = "date_created")
	private Date roleCreatedDate;
	@Column(name = "date_modified")
	private Date roleModifiedDate;
	@Column(name = "internal")
	private Boolean internal;
	@Column(name = "read_only")
	private Boolean readOnly;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getRoleCreatedDate() {
		return roleCreatedDate;
	}

	public void setRoleCreatedDate(Date roleCreatedDate) {
		this.roleCreatedDate = roleCreatedDate;
	}

	public Date getRoleModifiedDate() {
		return roleModifiedDate;
	}

	public void setRoleModifiedDate(Date roleModifiedDate) {
		this.roleModifiedDate = roleModifiedDate;
	}

	public Boolean getInternal() {
		return internal;
	}

	public void setInternal(Boolean internal) {
		this.internal = internal;
	}

	public Boolean getReadOnly() {
		return readOnly;
	}

	public void setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;
	}

	@Override
	public String toString() {
		return "Roles [id=" + id + ", roleName=" + roleName + ", description=" + description + ", status=" + status
				+ ", roleCreatedDate=" + roleCreatedDate + ", roleModifiedDate=" + roleModifiedDate + ", internal="
				+ internal + ", readOnly=" + readOnly + "]";
	}

}
