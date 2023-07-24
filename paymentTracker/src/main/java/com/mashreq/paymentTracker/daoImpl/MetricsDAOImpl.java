package com.mashreq.paymentTracker.daoImpl;

import java.math.BigInteger;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mashreq.paymentTracker.dao.MetricsDAO;
import com.mashreq.paymentTracker.model.Metrics;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

@Repository
@Transactional
public class MetricsDAOImpl extends BaseDAOImpl implements MetricsDAO {

	@PersistenceContext
	private EntityManager entityManager;

	private static final Logger logger = LoggerFactory.getLogger(ReportDAOImpl.class);

	@Override
	public BigInteger findMetricsOrderByReportId(long reportId) {
		Query query = entityManager.createNamedQuery("metrics.findMetricsOrderByReportId",Metrics.class);
		query.setParameter("reportId", reportId);
		return (BigInteger)query.getSingleResult();
	}

	@Override
	public Metrics save(Metrics metric) {
		//logger.info("MetricsDAOImpl.save metric:" + metric.toString());
		return super.create(metric);
	}

	@Override
	public Metrics update(Metrics metric) {
		//logger.info("MetricsDAOImpl.update metric:" + metric.toString());
		return super.update(metric);
	}

	@Override
	public void deleteById(long metricsId) {
		Metrics report = super.findById(Metrics.class, metricsId);
		entityManager.remove(report);
		logger.info("MetricsDAOImpl.deleteById id:" + metricsId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Metrics> findAll() {
		Query query = entityManager.createQuery("Select metric from Metrics metric");
		return (List<Metrics>) query.getResultList();
		
	}

	@Override
	public List<Metrics> getMetricsByReportId(Long reportId) {
		TypedQuery<Metrics> query = entityManager.createNamedQuery("metric.findMetricsByReportId", Metrics.class);
		query.setParameter("reportId", reportId);
		List<Metrics> metrics = query.getResultList();
		logger.info("MetricsDAOImpl.getMetricsByReportId reportId:" + reportId);
		return metrics;
	}

	@Override
	public Metrics getMetricsById(Long metricsId) {
		logger.info("MetricsDAOImpl.getMetricsById id:" + metricsId);
		return super.findById(Metrics.class, metricsId);
	}
}