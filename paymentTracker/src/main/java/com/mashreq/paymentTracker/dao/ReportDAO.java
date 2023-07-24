package com.mashreq.paymentTracker.dao;

import java.util.List;

import com.mashreq.paymentTracker.model.Prompts;
import com.mashreq.paymentTracker.model.Report;

public interface ReportDAO {

	public Report getReportById(Long id);

	public Report saveReport(Report report);

	public Report findByReportName(String reportName);

	public List<Report> getAllActiveReports();

	public List<Prompts> getPromptsByReportName(String reportName);

	void deleteReport(Long id);

	public List<Report> findByModuleId(Long moduleId);

	public List<Report> findReportByModule(String moduleName);

	Report updateReport(Report report);;
}