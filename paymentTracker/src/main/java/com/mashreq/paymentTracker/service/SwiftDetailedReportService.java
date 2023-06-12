package com.mashreq.paymentTracker.service;

import com.mashreq.paymentTracker.dto.APIResponse;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.ReportExecutionRequest;


public interface SwiftDetailedReportService {
	
	ReportExecuteResponseData processSwiftDetailReport(String reportName,
			ReportExecutionRequest reportProcessingRequest);

	APIResponse populateSuccessAPIRespone(ReportExecuteResponseData flexList);


}
