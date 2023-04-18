package com.mashreq.paymentTracker.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.dto.ReportDTORequest;
import com.mashreq.paymentTracker.model.Reports;
import com.mashreq.paymentTracker.service.ReportConfigurationService;

import jakarta.validation.Valid;

@RestController
@Component
@RequestMapping("/reports")
public class ReportsController {

	private static final Logger log = LoggerFactory.getLogger(ReportsController.class);
	private static final String FILENAME = "ReportsController";

	@Autowired
	ReportConfigurationService reportConfigurationService;

	@GetMapping
	public ResponseEntity<List<Reports>> fetchReports() {
		List<Reports> reportListResponse = reportConfigurationService.fetchAllReports();
		return ResponseEntity.ok(reportListResponse);
	}

	
	@GetMapping("/{reportname}/execute")
	public ResponseEntity<Reports> fetchReportByName(@PathVariable("reportname") String reportName) {
		Reports reportResponse = reportConfigurationService.fetchReportByName(reportName);
		return ResponseEntity.ok(reportResponse);
	}

	@PostMapping("/saveReport")
	public ResponseEntity<String> saveReportConfiguration(@Valid @RequestBody ReportDTORequest reportDTORequest) {
		try {
			reportConfigurationService.saveReportConfiguration(reportDTORequest);
			return new ResponseEntity<String>(ApplicationConstants.REPORT_CREATION_MSG, HttpStatus.CREATED);
		} catch (Exception e) {
			log.error(FILENAME + "[Exception Occured]" + e.getMessage());
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/updateReport/{reportId}")
	public ResponseEntity<String> updateReport(@Valid @RequestBody ReportDTORequest reportUpdateRequest,
			@PathVariable long reportId) {
		reportConfigurationService.updateReportById(reportUpdateRequest, reportId);
		return new ResponseEntity<String>(ApplicationConstants.REPORT_UPDATE_MSG, HttpStatus.ACCEPTED);
	}

	@DeleteMapping("deleteReport/{reportId}")
	public ResponseEntity<String> deleteReport(@PathVariable long reportId) {
		reportConfigurationService.deleteReportById(reportId);
		return new ResponseEntity<String>(ApplicationConstants.REPORT_DELETION_MSG, HttpStatus.ACCEPTED);

	}

}