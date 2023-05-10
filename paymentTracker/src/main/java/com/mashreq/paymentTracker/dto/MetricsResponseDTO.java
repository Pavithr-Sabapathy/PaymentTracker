package com.mashreq.paymentTracker.dto;

import java.io.Serializable;
import java.util.List;
public class MetricsResponseDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4335098696343647066L;
	private ReportDTO reports;
	private List<MetricsDTO> metricsList;

	public ReportDTO getReports() {
		return reports;
	}

	public void setReports(ReportDTO reportResponseDto) {
		this.reports = reportResponseDto;
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