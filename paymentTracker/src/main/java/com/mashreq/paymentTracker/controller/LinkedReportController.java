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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.dto.LinkedReportRequestDTO;
import com.mashreq.paymentTracker.dto.LinkedReportResponseDTO;
import com.mashreq.paymentTracker.service.LinkReportService;

@RestController
@Component
@RequestMapping("/linkReport")
public class LinkedReportController {

	private static final Logger log = LoggerFactory.getLogger(LinkedReportController.class);
	private static final String FILENAME = "LinkedReportController";

	@Autowired
	LinkReportService linkReportService;

	@PostMapping
	public ResponseEntity<LinkedReportResponseDTO> saveOrUpdateLinkedReport(
			@RequestBody LinkedReportRequestDTO linkedReportRequestDTO) {
		LinkedReportResponseDTO linkReportResponse = linkReportService.saveOrUpdateLinkedReport(linkedReportRequestDTO);
		return new ResponseEntity<LinkedReportResponseDTO>(linkReportResponse, HttpStatus.CREATED);
	}

	@GetMapping("/{linkedReportId}")
	public ResponseEntity<LinkedReportResponseDTO> fetchLinkedReport(@PathVariable long linkedReportId) {
		LinkedReportResponseDTO linkedReportResponse = linkReportService.fetchLinkedReportById(linkedReportId);
		log.info(FILENAME + "[fetchLinkedReport Response]--->" + linkedReportResponse.toString());
		return ResponseEntity.ok(linkedReportResponse);
	}

	@GetMapping("module/{moduleId}")
	public ResponseEntity<List<LinkedReportResponseDTO>> fetchLinkedReportByModuleId(@PathVariable long moduleId) {
		List<LinkedReportResponseDTO> linkedReportResponseList = linkReportService
				.fetchLinkedReportByModuleId(moduleId);
		log.info(FILENAME + "[fetchLinkedReport Response]--->" + linkedReportResponseList.toString());
		return ResponseEntity.ok(linkedReportResponseList);
	}
	@DeleteMapping("/{linkedReportId}")
	public ResponseEntity<String> deletelinkedReport(@PathVariable long linkedReportId) {
		log.info(FILENAME + "[deleteLinkedReports for linkedReportId]--->" + linkedReportId);
		linkReportService.deletelinkedReportById(linkedReportId);
		log.info(FILENAME + "[deleteLinkedReports deleted for this ID]--->" + linkedReportId);
		return new ResponseEntity<String>(ApplicationConstants.LINK_REPORT_DELETION_MSG, HttpStatus.ACCEPTED);
	}
	}