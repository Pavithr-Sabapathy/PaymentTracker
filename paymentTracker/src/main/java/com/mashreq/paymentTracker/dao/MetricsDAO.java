package com.mashreq.paymentTracker.dao;

import java.math.BigInteger;
import java.util.List;

import com.mashreq.paymentTracker.model.Metrics;

public interface MetricsDAO {

	BigInteger findMetricsOrderByReportId(long reportId);

	Metrics save(Metrics metric);

	void deleteById(long metricsId);

	List<Metrics> findAll();

	List<Metrics> getMetricsByReportId(Long reportId);

	Metrics getMetricsById(Long metricsId);

	Metrics update(Metrics metric);

}