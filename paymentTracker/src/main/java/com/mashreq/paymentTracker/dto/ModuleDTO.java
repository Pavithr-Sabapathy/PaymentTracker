package com.mashreq.paymentTracker.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotNull;

public class ModuleDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotNull(message = "Name should not be empty")
	private String name;

	@NotNull(message = "Display Name should not be empty")
	private String displayName;

	@NotNull(message = "Description should not be empty")
	private String moduleDescription;

	@NotNull(message = "active should not be empty")
	private String active;

	@NotNull(message = "Valid should not be empty")
	private String valid;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
		return "ModuleRequestDTO [name=" + name + ", displayName=" + displayName + ", description=" + moduleDescription
				+ ", active=" + active + ", valid=" + valid + "]";
	}

}