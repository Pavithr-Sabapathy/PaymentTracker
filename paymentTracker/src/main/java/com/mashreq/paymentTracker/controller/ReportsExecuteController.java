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
import com.mashreq.paymentTracker.dto.FlexReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.ReportProcessingRequest;
import com.mashreq.paymentTracker.exception.ReportException;
import com.mashreq.paymentTracker.service.ReportsExecuteService;

@RestController
@Component
@RequestMapping("/report")
public class ReportsExecuteController {

	@Autowired
	ReportsExecuteService reporsExecuteService;

	private static final Logger log = LoggerFactory.getLogger(ReportsExecuteController.class);
	private static final String FILENAME = "ReportsExecuteController";

	@PostMapping("/{reportName}/execute")
	public ResponseEntity<APIResponse> executeReport(@PathVariable String reportName,
			@RequestBody ReportProcessingRequest reportProcessingRequest) throws ReportException {
		APIResponse reportExecutionApiResponse = new APIResponse();
		try {
			FlexReportExecuteResponseData flexList = reporsExecuteService.executeReport(reportName,
					reportProcessingRequest);
			reportExecutionApiResponse = reporsExecuteService.populateSuccessAPIRespone(flexList);

		} catch (Exception exception) {
			log.error(FILENAME + "[Exception Occured]" + exception.getMessage());
			return new ResponseEntity<APIResponse>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<APIResponse>(reportExecutionApiResponse, HttpStatus.CREATED);
	}

}