package com.mashreq.paymentTracker.controller;

import java.util.List;

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
import com.mashreq.paymentTracker.dto.ReportDTO;
import com.mashreq.paymentTracker.dto.ReportDTORequest;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.service.ReportConfigurationService;

import jakarta.validation.Valid;

@RestController
@Component
@RequestMapping("/report")
public class ReportsController {

	@Autowired
	ReportConfigurationService reportConfigurationService;

	@GetMapping(value ="/{id}",produces = "application/json")
	public ResponseEntity<ReportDTO> fetchReportById(@PathVariable("id") Long id) {
		ReportDTO reportResponse = reportConfigurationService.fetchReportById(id);
		return new ResponseEntity<ReportDTO>(reportResponse, HttpStatus.OK);
	}

	@GetMapping(value = "moduleId/{moduleId}",produces = "application/json")	
	public ResponseEntity<List<ReportDTO>> fetchReportsByModuleId(@PathVariable("moduleId") Long moduleId) {
		List<ReportDTO> reportListResponse = reportConfigurationService.fetchReportsByModuleId(moduleId);
		return new ResponseEntity<List<ReportDTO>>(reportListResponse, HttpStatus.OK);
	}

	@GetMapping(value = "moduleName/{moduleName}",produces = "application/json")
	public ResponseEntity<List<ReportDTO>> fetchReportsByModuleName(@PathVariable("moduleName") String moduleName) {
		List<ReportDTO> reportListResponse = reportConfigurationService.fetchReportsByModule(moduleName);
		return new ResponseEntity<List<ReportDTO>>(reportListResponse, HttpStatus.OK);
	}

	@GetMapping(value = "name/{reportname}",produces = "application/json")
	public ResponseEntity<Report> fetchReportByName(@PathVariable("reportname") String reportName) {
		Report reportResponse = reportConfigurationService.fetchReportByName(reportName);
		return new ResponseEntity<Report>(reportResponse, HttpStatus.OK);
	}

	@PostMapping("/save")
	public ResponseEntity<ReportDTO> saveReportConfiguration(@Valid @RequestBody ReportDTORequest reportDTORequest) {
		ReportDTO reportRespone = reportConfigurationService.saveReport(reportDTORequest);
		return new ResponseEntity<ReportDTO>(reportRespone, HttpStatus.CREATED);
	}

	@PutMapping("/{reportId}")
	public ResponseEntity<ReportDTO> updateReport(@Valid @RequestBody ReportDTORequest reportUpdateRequest,
			@PathVariable long reportId) {
		ReportDTO reportDTO = new ReportDTO();
		reportDTO = reportConfigurationService.updateReportById(reportUpdateRequest, reportId);
		return new ResponseEntity<ReportDTO>(reportDTO, HttpStatus.ACCEPTED);
	}

	@DeleteMapping("id/{reportId}")
	public ResponseEntity<String> deleteReport(@PathVariable long reportId) {
		reportConfigurationService.deleteReportById(reportId);
		return new ResponseEntity<String>(ApplicationConstants.REPORT_DELETION_MSG, HttpStatus.ACCEPTED);
	}

}