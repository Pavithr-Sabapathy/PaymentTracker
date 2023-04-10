package com.mashreq.paymentTracker.dto;

import java.io.Serializable;
import java.util.List;

import com.mashreq.paymentTracker.model.Reports;

public class PromptResponseDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4335098696343647066L;
	private Reports reports;
	private List<PromptDTO> promptsList;

	public Reports getReports() {
		return reports;
	}

	public void setReports(Reports reports) {
		this.reports = reports;
	}

	public List<PromptDTO> getPromptsList() {
		return promptsList;
	}

	public void setPromptsList(List<PromptDTO> promptsList) {
		this.promptsList = promptsList;
	}

	@Override
	public String toString() {
		return "PromptResponseDTO [reports=" + reports + ", promptsList=" + promptsList + "]";
	}

}