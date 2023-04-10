package com.mashreq.paymentTracker.service;

import java.util.List;

import com.mashreq.paymentTracker.dto.ReportDTO;
import com.mashreq.paymentTracker.model.Reports;

public interface ReportConfigurationService {

	Reports saveReportConfiguration(ReportDTO reportDTORequest);

	List<Reports> fetchAllReports();

	void updateReportById(ReportDTO reportUpdateRequest, long reportId);

	void deleteReportById(long reportId);

	List<Reports> fetchReportsAsExcel();

}
