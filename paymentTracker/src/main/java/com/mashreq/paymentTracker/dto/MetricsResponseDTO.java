package com.mashreq.paymentTracker.dto;

import java.io.Serializable;
import java.util.List;

import com.mashreq.paymentTracker.model.Reports;

public class MetricsResponseDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4335098696343647066L;
	private Reports reports;
	private List<MetricsDTO> metricsList;

	public Reports getReports() {
		return reports;
	}

	public void setReports(Reports reports) {
		this.reports = reports;
	}

	public List<MetricsDTO> getMetricsList() {
		return metricsList;
	}

	public void setMetricsList(List<MetricsDTO> metricsList) {
		this.metricsList = metricsList;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "MetricsResponseDTO [reports=" + reports + ", metricsList=" + metricsList + "]";
	}

	

}