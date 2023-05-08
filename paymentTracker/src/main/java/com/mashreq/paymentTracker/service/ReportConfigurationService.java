package com.mashreq.paymentTracker.service;

import java.io.ByteArrayOutputStream;
import java.util.List;

import com.mashreq.paymentTracker.dto.ReportDTORequest;
import com.mashreq.paymentTracker.model.Reports;

public interface ReportConfigurationService {

	Reports saveReportConfiguration(ReportDTORequest reportDTORequest);

	List<Reports> fetchAllReports();

	Reports fetchReportByName(String reportName);
	
	void deleteReportById(long reportId);

	List<Reports> fetchReportsAsExcel();

	void updateReportById(ReportDTORequest reportUpdateRequest, long reportId);
	
	ByteArrayOutputStream generateReportPDF();

}
