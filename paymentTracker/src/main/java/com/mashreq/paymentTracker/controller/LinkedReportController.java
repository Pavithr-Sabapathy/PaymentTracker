package com.mashreq.paymentTracker.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
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
	public ResponseEntity<String> saveOrUpdateLinkedReport(@RequestBody LinkedReportRequestDTO linkedReportRequestDTO) {
		try {
			linkReportService.saveOrUpdateLinkedReport(linkedReportRequestDTO);
			return new ResponseEntity<String>(ApplicationConstants.LINK_REPORT_CREATION_MSG, HttpStatus.CREATED);
		} catch (Exception e) {
			log.error(FILENAME + "[Exception Occured]" + e.getMessage());
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/{linkedReportId}")
	public ResponseEntity<LinkedReportResponseDTO> fetchLinkedReport(@PathVariable long linkedReportId) {
		LinkedReportResponseDTO linkedReportResponse = linkReportService.fetchLinkedReportById(linkedReportId);
		log.info(FILENAME + "[fetchLinkedReport Response]--->" + linkedReportResponse.toString());
		return ResponseEntity.ok(linkedReportResponse);
	}

}