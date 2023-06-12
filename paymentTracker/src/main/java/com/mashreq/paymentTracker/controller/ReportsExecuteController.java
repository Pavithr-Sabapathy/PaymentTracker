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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mashreq.paymentTracker.dto.APIResponse;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.ReportExecutionRequest;
import com.mashreq.paymentTracker.exception.ReportException;
import com.mashreq.paymentTracker.service.ReportHandlerService;

@RestController
@Component
@RequestMapping("/report")
public class ReportsExecuteController {

	@Autowired
	ReportHandlerService reportHandlerService;

	private static final Logger log = LoggerFactory.getLogger(ReportsExecuteController.class);
	private static final String FILENAME = "ReportsExecuteController";

	
	@PostMapping("/{reportName}/execute")
	@ResponseBody
	public ResponseEntity<APIResponse> executeReport(@PathVariable String reportName,
			@RequestBody ReportExecutionRequest reportExecutionRequest) throws ReportException {
		APIResponse reportExecutionApiResponse = new APIResponse();
		try {
			ReportExecuteResponseData reportExecuteResponseList = reportHandlerService.executeReport(reportName,
					reportExecutionRequest);
			reportExecutionApiResponse = reportHandlerService.populateSuccessAPIRespone(reportExecuteResponseList);

		} catch (Exception exception) {
			log.error(FILENAME + "[Exception Occured]" + exception.getMessage());
			return new ResponseEntity<APIResponse>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<APIResponse>(reportExecutionApiResponse, HttpStatus.CREATED);
	}

}