package com.mashreq.paymentTracker.controller;

import java.util.List;

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
import com.mashreq.paymentTracker.dto.LinkMappingResponseDTO;
import com.mashreq.paymentTracker.dto.LinkedReportMappingRequestDTO;
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
	public ResponseEntity<String> saveOrUpdateLinkMapping(@RequestBody LinkedReportMappingRequestDTO linkedReportMappingRequestDTO) {
		try {
			linkMappingService.saveOrUpdateLinkMapping(linkedReportMappingRequestDTO);
			return new ResponseEntity<String>(ApplicationConstants.LINK_MAPPING_REPORT_CREATION_MSG, HttpStatus.CREATED);
		} catch (Exception e) {
			log.error(FILENAME + "[Exception Occured]" + e.getMessage());
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	

	@GetMapping("/{linkReportId}")
	public ResponseEntity<List<LinkMappingResponseDTO>> fetchLinkMapping(@PathVariable long linkReportId) {
		 List<LinkMappingResponseDTO> linkedReportResponse = linkMappingService.fetchLinkMappingById(linkReportId);
		log.info(FILENAME + "[fetchLinkedReport Response]--->" + linkedReportResponse.toString());
		return ResponseEntity.ok(linkedReportResponse);
	}

	
}