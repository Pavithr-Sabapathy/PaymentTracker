package com.mashreq.paymentTracker.service;

import java.io.ByteArrayOutputStream;
import java.util.List;

import com.mashreq.paymentTracker.dto.ReportDTO;
import com.mashreq.paymentTracker.dto.ReportDTORequest;
import com.mashreq.paymentTracker.model.Report;

public interface ReportConfigurationService {

	ReportDTO saveReport(ReportDTORequest reportDTORequest);

	Report fetchReportByName(String reportName);
	
	void deleteReportById(long reportId);

	List<Report> fetchReportsAsExcel();

	ReportDTO updateReportById(ReportDTORequest reportUpdateRequest, long reportId);
	
	ByteArrayOutputStream generateReportPDF();

	List<ReportDTO> fetchReportsByModule(String moduleName);

	List<ReportDTO> fetchReportsByModuleId(Long moduleId);

	ReportDTO fetchReportById(Long reportId);


}
