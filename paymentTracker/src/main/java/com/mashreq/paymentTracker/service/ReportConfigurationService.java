package com.mashreq.paymentTracker.service;

import java.util.List;

import com.mashreq.paymentTracker.dto.ReportDTO;
import com.mashreq.paymentTracker.dto.ReportDTORequest;
import com.mashreq.paymentTracker.model.Report;

public interface ReportConfigurationService {

	ReportDTO saveReport(ReportDTORequest reportDTORequest);

	Report fetchReportByName(String reportName);
	
	void deleteReportById(long reportId);

	ReportDTO updateReportById(ReportDTORequest reportUpdateRequest, long reportId);
	
	List<ReportDTO> fetchReportsByModule(String moduleName);

	List<ReportDTO> fetchReportsByModuleId(Long moduleId);

	ReportDTO fetchReportById(Long reportId);


}
