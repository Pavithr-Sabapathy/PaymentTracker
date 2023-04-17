package com.mashreq.paymentTracker.service;

import java.util.List;

import com.mashreq.paymentTracker.dto.ReportDTORequest;
import com.mashreq.paymentTracker.model.Reports;

public interface ReportConfigurationService {

	Reports saveReportConfiguration(ReportDTORequest reportDTORequest);

	List<Reports> fetchAllReports();

	List<Reports> fetchAllReportsByName(String reportName);
	
	void deleteReportById(long reportId);

	List<Reports> fetchReportsAsExcel();

	void updateReportById(ReportDTORequest reportUpdateRequest, long reportId);

}
