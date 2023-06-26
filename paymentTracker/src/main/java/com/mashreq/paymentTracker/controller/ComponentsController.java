package com.mashreq.paymentTracker.controller;

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
import com.mashreq.paymentTracker.dto.ComponentDetailsRequestDTO;
import com.mashreq.paymentTracker.dto.ComponentsRequestDTO;
import com.mashreq.paymentTracker.service.ComponentsService;

@RestController
@Component
@RequestMapping("/Components")
public class ComponentsController {

	private static final Logger log = LoggerFactory.getLogger(ComponentsController.class);
	private static final String FILENAME = "ComponentsController";

	@Autowired
	private ComponentsService componentService;
	
	@GetMapping
	public ResponseEntity fetchComponentByReportId() {
		return null;
		
	}
	
	@PostMapping("/saveComponents")
	public ResponseEntity<String> saveComponents(@RequestBody ComponentsRequestDTO componentsRequest) {
		try {
			log.info(FILENAME + "[saveMetrics Request]--->" + componentsRequest.toString());
			componentService.saveComponents(componentsRequest);
			return new ResponseEntity<String>(ApplicationConstants.COMPONENT_CREATION_MSG, HttpStatus.CREATED);
		} catch (Exception e) {
			log.error(FILENAME + "[Exception Occured]" + e.getMessage());
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/{componentId}")
	public ResponseEntity<String> deleteComponents(@PathVariable long componentId) {
		log.info(FILENAME + "[deleteComponents for componentId]--->" + componentId);
		componentService.deleteComponentById(componentId);
		log.info(FILENAME + "[deleteComponents deleted for this ID]--->" + componentId);
		return new ResponseEntity<String>(ApplicationConstants.COMPONENT_DELETION_MSG, HttpStatus.ACCEPTED);
	}
	
	@PostMapping("/saveDetails")
	public ResponseEntity<String> saveComponentsDetails(@RequestBody ComponentDetailsRequestDTO componentDetailsRequest) {
		try {
			log.info(FILENAME + "[saveComponentsDetails Request]--->" + componentDetailsRequest.toString());
			componentService.saveComponentsDetails(componentDetailsRequest);
			return new ResponseEntity<String>(ApplicationConstants.COMPONENT_DETAILS_CREATION_MSG, HttpStatus.CREATED);
		} catch (Exception e) {
			log.error(FILENAME + "[Exception Occured]" + e.getMessage());
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("/componentDetail/{componentDetailId}")
	public ResponseEntity<String> deleteComponentDetails(@PathVariable long componentDetailId) {
		log.info(FILENAME + "[deleteComponentDetails for componentId]--->" + componentDetailId);
		componentService.deleteComponentDetailsById(componentDetailId);
		log.info(FILENAME + "[deleteComponentDetails deleted for this ID]--->" + componentDetailId);
		return new ResponseEntity<String>(ApplicationConstants.COMPONENT_DETAILS_DELETION_MSG, HttpStatus.ACCEPTED);

	}
}