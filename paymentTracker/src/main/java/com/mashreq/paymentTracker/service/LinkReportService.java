package com.mashreq.paymentTracker.service;

import com.mashreq.paymentTracker.dto.LinkedReportRequestDTO;
import com.mashreq.paymentTracker.dto.LinkedReportResponseDTO;

public interface LinkReportService {

	void saveOrUpdateLinkedReport(LinkedReportRequestDTO linkedReportRequestDTO);

	LinkedReportResponseDTO fetchLinkedReportById(long linkedReportId);

}
