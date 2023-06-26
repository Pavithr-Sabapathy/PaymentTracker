package com.mashreq.paymentTracker.dto;

import java.io.Serializable;

public class ModuleResponseDTO implements Serializable{


	private static final long serialVersionUID = 1L;

	private String name;

	private String displayName;

	private String description;

	private String active;

	private String valid;
	
	private String errorMessage;
	
	private String warningMessage;
	

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getWarningMessage() {
		return warningMessage;
	}

	public void setWarningMessage(String warningMessage) {
		this.warningMessage = warningMessage;
	}

	@Override
	public String toString() {
		return "ModuleResponseDTO [name=" + name + ", displayName=" + displayName + ", description=" + description
				+ ", active=" + active + ", valid=" + valid + ", errorMessage=" + errorMessage + ", warningMessage="
				+ warningMessage + "]";
	}


}
