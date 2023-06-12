package com.mashreq.paymentTracker.dto;

import java.io.Serializable;
import java.util.List;

import com.mashreq.paymentTracker.model.Report;

public class PromptResponseDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4335098696343647066L;
	private Report reports;
	private List<PromptDTO> promptsList;

	public Report getReports() {
		return reports;
	}

	public void setReports(Report reports) {
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