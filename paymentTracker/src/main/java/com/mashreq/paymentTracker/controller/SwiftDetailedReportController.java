package com.mashreq.paymentTracker.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mashreq.paymentTracker.dto.APIResponse;
import com.mashreq.paymentTracker.dto.ReportProcessingRequest;
import com.mashreq.paymentTracker.dto.SwiftDetailedReportExecuteResponseData;
import com.mashreq.paymentTracker.exception.ReportException;
import com.mashreq.paymentTracker.service.SwiftDetailedReportService;

@RestController
@Component
@RequestMapping("/report")
public class SwiftDetailedReportController {
	
	@Autowired
	SwiftDetailedReportService swiftDetailedReportService;

	private static final Logger log = LoggerFactory.getLogger(SwiftDetailedReportController.class);
	private static final String FILENAME = "SwiftDetailedReportController";

	
	@PostMapping("/swift/{reportName}/execute")
	public ResponseEntity<APIResponse> executeSwiftDetailedReport(@PathVariable String reportName,
			@RequestBody ReportProcessingRequest reportProcessingRequest) throws ReportException {
		APIResponse swiftDetailedReportApiResponse = new APIResponse();
		try {
			SwiftDetailedReportExecuteResponseData swiftDetailedReport = swiftDetailedReportService.processSwiftDetailReport(reportName, reportProcessingRequest);
			swiftDetailedReportApiResponse = swiftDetailedReportService.populateSuccessAPIRespone(swiftDetailedReport);

		} catch (Exception exception) {
			log.error(FILENAME + "[Exception Occured]" + exception.getMessage());
			return new ResponseEntity<APIResponse>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<APIResponse>(swiftDetailedReportApiResponse, HttpStatus.CREATED);
	}
}
