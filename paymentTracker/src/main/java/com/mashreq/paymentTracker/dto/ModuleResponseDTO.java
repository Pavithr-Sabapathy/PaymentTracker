package com.mashreq.paymentTracker.dto;

import java.io.Serializable;

public class ModuleResponseDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	
	private String moduleName;

	private String displayName;

	private String moduleDescription;

	private String active;

	private String valid;

	private String error;

	private String warning;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getWarning() {
		return warning;
	}

	public void setWarning(String warning) {
		this.warning = warning;
	}

	@Override
	public String toString() {
		return "ModuleResponseDTO [id=" + id + ", moduleName=" + moduleName + ", displayName=" + displayName
				+ ", moduleDescription=" + moduleDescription + ", active=" + active + ", valid=" + valid + ", error="
				+ error + ", warning=" + warning + "]";
	}

}
