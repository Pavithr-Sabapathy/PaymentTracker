package com.mashreq.paymentTracker.service;

import java.util.List;

import com.mashreq.paymentTracker.dto.LinkedReportRequestDTO;
import com.mashreq.paymentTracker.dto.LinkedReportResponseDTO;

public interface LinkReportService {

	void saveOrUpdateLinkedReport(LinkedReportRequestDTO linkedReportRequestDTO);

	LinkedReportResponseDTO fetchLinkedReportById(long linkedReportId);

	List<LinkedReportResponseDTO> fetchLinkedReportByReportId(long reportId);	
	
}
