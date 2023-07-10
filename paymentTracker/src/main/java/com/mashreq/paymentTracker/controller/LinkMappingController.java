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

import com.mashreq.paymentTracker.dto.LinkMappingResponseDTO;
import com.mashreq.paymentTracker.dto.LinkedReportMappingRequestDTO;
import com.mashreq.paymentTracker.model.LinkedReportDetails;
import com.mashreq.paymentTracker.service.LinkMappingService;

@RestController
@Component
@RequestMapping("/linkMapping")
public class LinkMappingController {

	private static final Logger log = LoggerFactory.getLogger(LinkMappingController.class);
	private static final String FILENAME = "LinkMappingController";

	@Autowired
	LinkMappingService linkMappingService;

	@PostMapping
	public ResponseEntity<LinkedReportDetails> saveOrUpdateLinkMapping(
			@RequestBody LinkedReportMappingRequestDTO linkedReportMappingRequestDTO) {
		LinkedReportDetails linkMappingResponse = linkMappingService
				.saveOrUpdateLinkMapping(linkedReportMappingRequestDTO);
		return new ResponseEntity<LinkedReportDetails>(linkMappingResponse, HttpStatus.CREATED);
	}

	@GetMapping("/{promptId}")
	public ResponseEntity<LinkMappingResponseDTO> fetchLinkMapping(@PathVariable long promptId) {
		LinkMappingResponseDTO linkedReportResponse = linkMappingService.fetchLinkMappingById(promptId);
		log.info(FILENAME + "[fetchLinkedReport Response]--->" + linkedReportResponse.toString());
		return ResponseEntity.ok(linkedReportResponse);
	}

}