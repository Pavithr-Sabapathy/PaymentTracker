package com.mashreq.paymentTracker.service;

import java.util.List;

import com.mashreq.paymentTracker.dto.MetricsRequestDTO;
import com.mashreq.paymentTracker.dto.MetricsResponse;
import com.mashreq.paymentTracker.dto.MetricsResponseDTO;

public interface MetricsService {
	
	void deleteMetricsById(long metricsId);

	void updateMetricsById(MetricsRequestDTO metricsDTORequest, long metricsId);

	List<MetricsResponse> fetchAllMetrics();

	List<MetricsResponseDTO> fetchMetricsByReportId(long reportId);

	void saveMetrics(MetricsRequestDTO metricsRequest);

}
