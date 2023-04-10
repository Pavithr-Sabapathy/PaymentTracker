package com.mashreq.paymentTracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mashreq.paymentTracker.model.Metrics;

@Repository
public interface MetricsCustomizedRepository {

	@Query("select max(metrics.metricsOrder)  from Metrics metrics join Reports report on metrics.report = report.id where report.id =?1")
	Long findMetricsOrderByReportId(Long reportId);

	@Query("select metrics from Metrics metrics join Reports report on metrics.report = report.id where report.id =?1")
	List<Metrics> findMetricsByReportId(long reportId);
	
}