package com.mashreq.paymentTracker.daoImpl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.mashreq.paymentTracker.dao.ReportExecutionDAO;
import com.mashreq.paymentTracker.model.ReportExecution;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class ReportExecutionDAOImpl extends BaseDAOImpl implements ReportExecutionDAO {

	private static final Logger logger = LoggerFactory.getLogger(ReportExecutionDAOImpl.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public ReportExecution save(ReportExecution reportExecution) {
		return super.create(reportExecution);
	}

	@Override
	public void updateStatusById(Long executionId, String status, Date endTime) {
		Query query = entityManager.createNamedQuery("ReportExecution.updateStatusById");
		query.setParameter("executionId", executionId);
		query.setParameter("status", status);
		query.setParameter("endDate", endTime);
		int res = query.executeUpdate();
		if (res != 0)
			logger.info("Report Execution Status updated with status:" + status + " by excution ID:" + executionId);
		else
			logger.error("Report Execution Status failed to update with status:" + status + " by excution ID:"
					+ executionId);
	}

	@Override
	public void updateReportExecutionTimeByExecutionId(Long executionTime, Long executionId) {
		Query query = entityManager.createNamedQuery("ReportExecution.updateExecutionTimeByExecutionId");
		query.setParameter("executionId", executionId);
		query.setParameter("executionTime", executionTime);
		int res = query.executeUpdate();
		if (res != 0)
			logger.info("Report Execution  Time updated with execution time :" + executionTime + " by excution ID:"
					+ executionId);
		else
			logger.error("Report Execution Time failed to update with execution time:" + executionTime
					+ " by excution ID:" + executionId);
	}

}