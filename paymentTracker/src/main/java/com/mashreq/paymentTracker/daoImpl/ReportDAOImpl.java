package com.mashreq.paymentTracker.daoImpl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mashreq.paymentTracker.dao.ReportDAO;
import com.mashreq.paymentTracker.model.Prompts;
import com.mashreq.paymentTracker.model.Report;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

@Repository
@Transactional
public class ReportDAOImpl extends BaseDAOImpl implements ReportDAO {

	@PersistenceContext
	private EntityManager entityManager;

	private static final Logger logger = LoggerFactory.getLogger(ReportDAOImpl.class);

	@Override
	public Report getReportById(Long id) {
		logger.info("ReportDAOImpl.getReportById id:" + id);
		return super.findById(Report.class, id);
	}

	@Override
	public Report saveReport(Report report) {
		logger.info("ReportDAOImpl.saveReport report:" + report.toString());
		return super.create(report);
	}

	@Override
	public Report updateReport(Report report) {
		logger.info("ReportDAOImpl.saveReport report:" + report.toString());
		return super.update(report);
	}

	@Override
	public Report findByReportName(String reportName) {
		Query query = entityManager.createNamedQuery("report.findByReportName");
		query.setParameter("reportName", reportName);
		Report report = (Report) query.getSingleResult();
		logger.info("ReportDAOImpl.getReportByReportName reportName:" + reportName);
		return report;
	}

	@Override
	public List<Report> getAllActiveReports() {
		TypedQuery<Report> query = entityManager.createNamedQuery("report.getAllActiveReports", Report.class);
		List<Report> activeReports = query.getResultList();
		return activeReports;
	}

	@Override
	public List<Prompts> getPromptsByReportName(String reportName) {
		TypedQuery<Prompts> query = entityManager.createNamedQuery("prompt.findPromptsByReportName", Prompts.class);
		query.setParameter("reportName", reportName);
		List<Prompts> prompts = (List<Prompts>) query.getResultList();
		logger.info("ReportDAOImpl.getPromptsByReportName reportName:" + reportName);
		return prompts;
	}

	@Override
	public void deleteReport(Long id) {
		Report report = super.findById(Report.class, id);
		entityManager.remove(report);
		logger.info("ReportDAOImpl.deleteReport id:" + id);
	}

	@Override
	public List<Report> findByModuleId(Long moduleId) {
		TypedQuery<Report> query = entityManager.createNamedQuery("report.findByModuleId", Report.class);
		query.setParameter("moduleId", moduleId);
		List<Report> report = (List<Report>) query.getResultList();
		logger.info("ReportDAOImpl.findByModuleId moduleId:" + moduleId);
		return report;
	}

	@Override
	public List<Report> findReportByModule(String moduleName) {
		TypedQuery<Report> query = entityManager.createNamedQuery("report.findReportByModule", Report.class);
		query.setParameter("moduleName", moduleName);
		List<Report> report = (List<Report>) query.getResultList();
		logger.info("ReportDAOImpl.findReportByModule moduleName:" + moduleName);
		return report;
	}
}