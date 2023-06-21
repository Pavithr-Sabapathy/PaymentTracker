package com.mashreq.paymentTracker.model;

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
@Table(name = "conf_module")
public class ApplicationModule {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotNull(message = "Module Name should not be empty")
	@Column(name = "mod_name")
	private String moduleName;

	@NotNull(message = "Display Name should not be empty")
	@Column(name = "display_name")
	private String displayName;

	@NotNull(message = "Description should not be empty")
	@Column(name = "mod_description")
	private String moduleDescription;

	@NotNull(message = "active should not be empty")
	@Column(name = "active")
	private String active;
	
	@Column(name = "valid")
	private String valid;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getModuleDescription() {
		return moduleDescription;
	}

	public void setModuleDescription(String moduleDescription) {
		this.moduleDescription = moduleDescription;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getValid() {
		return valid;
	}

	public void setValid(String valid) {
		this.valid = valid;
	}

	@Override
	public String toString() {
		return "Module [id=" + id + ", moduleName=" + moduleName + ", displayName=" + displayName
				+ ", moduleDescription=" + moduleDescription + ", active=" + active + ", valid=" + valid + "]";
	}

	public ApplicationModule(long id, @NotNull(message = "Module Name should not be empty") String moduleName,
			@NotNull(message = "Display Name should not be empty") String displayName,
			@NotNull(message = "Description should not be empty") String moduleDescription,
			@NotNull(message = "active should not be empty") String active, String valid) {
		super();
		this.id = id;
		this.moduleName = moduleName;
		this.displayName = displayName;
		this.moduleDescription = moduleDescription;
		this.active = active;
		this.valid = valid;
	}

	public ApplicationModule() {
		super();
		// TODO Auto-generated constructor stub
	}

}