package com.mashreq.paymentTracker.dto;

import java.io.Serializable;
import java.util.List;
public class MetricsResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4335098696343647066L;
	private ReportDTO reports;
	private List<MetricsResponseDTO> metricsList;

	public ReportDTO getReports() {
		return reports;
	}

	public void setReports(ReportDTO reportResponseDto) {
		this.reports = reportResponseDto;
	}

	public List<MetricsResponseDTO> getMetricsList() {
		return metricsList;
	}

	public void setMetricsList(List<MetricsResponseDTO> metricsList) {
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