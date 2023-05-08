package com.mashreq.paymentTracker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mashreq.paymentTracker.dto.FlexReportDefaultOutput;
import com.mashreq.paymentTracker.dto.ReportProcessingRequest;
import com.mashreq.paymentTracker.exception.ReportException;
import com.mashreq.paymentTracker.service.ReportsExecuteService;

@RestController
@Component
@RequestMapping("/report")
public class ReportsExecuteController {

	@Autowired
	ReportsExecuteService reporsExecuteService;
	
	@PostMapping("/{reportName}/execute")
	public List<FlexReportDefaultOutput> executeReport(@PathVariable String reportName,
			@RequestBody ReportProcessingRequest reportProcessingRequest) throws ReportException {
		List<FlexReportDefaultOutput> flexList = reporsExecuteService.executeReport(reportName,reportProcessingRequest);
		return flexList;

	}

}