package com.mashreq.paymentTracker.service;

import com.mashreq.paymentTracker.dto.APIResponse;
import com.mashreq.paymentTracker.dto.ReportProcessingRequest;
import com.mashreq.paymentTracker.dto.SwiftDetailedReportExecuteResponseData;


public interface SwiftDetailedReportService {
	
	SwiftDetailedReportExecuteResponseData processSwiftDetailReport(String reportName,
			ReportProcessingRequest reportProcessingRequest);

	APIResponse populateSuccessAPIRespone(SwiftDetailedReportExecuteResponseData flexList);

}
