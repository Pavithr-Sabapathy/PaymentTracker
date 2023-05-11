package com.mashreq.paymentTracker.service;

import java.util.List;

import com.mashreq.paymentTracker.dto.MetricsDTO;
import com.mashreq.paymentTracker.dto.MetricsResponseDTO;
import com.mashreq.paymentTracker.model.Metrics;

public interface MetricsService {

	void saveMetrics(MetricsDTO metricsRequest);
	
	void deleteMetricsById(long metricsId);

	void updateMetricsById(MetricsDTO metricsDTORequest, long metricsId);

	List<MetricsResponseDTO> fetchAllMetrics();

	List<MetricsDTO> fetchMetricsByReportId(long reportId);

}
