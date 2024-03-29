package com.mashreq.paymentTracker.controller;

import java.util.List;

import javax.ws.rs.Produces;

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
import com.mashreq.paymentTracker.dto.PromptDTO;
import com.mashreq.paymentTracker.dto.PromptRequestDTO;
import com.mashreq.paymentTracker.dto.PromptResponseDTO;
import com.mashreq.paymentTracker.service.promptService;

import jakarta.validation.Valid;

@RestController
@Component
@RequestMapping("/prompt")
public class PromptsController {

	private static final Logger log = LoggerFactory.getLogger(PromptsController.class);
	private static final String FILENAME = "PromptsController";

	@Autowired
	promptService promptService;

	@GetMapping
	@Produces("application/json")
	public ResponseEntity<List<PromptResponseDTO>> fetchAllPrompts() {
		List<PromptResponseDTO> reportListResponse = promptService.fetchAllPrompts();
		return ResponseEntity.ok(reportListResponse);
	}

	@GetMapping("/{reportId}")
	@Produces("application/json")
	public ResponseEntity<List<PromptDTO>> fetchPromptsByReportId(@PathVariable("reportId") long reportId) {
		List<PromptDTO> reportListResponse = promptService.fetchPromptsByReportId(reportId);
		return ResponseEntity.ok(reportListResponse);
	}

	@PostMapping("/savePrompt")
	@Produces("application/json")
	public ResponseEntity<PromptDTO> savePrompt(@Valid @RequestBody PromptRequestDTO promptRequest) {
		PromptDTO promptDTORepsonse = promptService.savePrompt(promptRequest);
		return new ResponseEntity<PromptDTO>(promptDTORepsonse, HttpStatus.CREATED);
	}

	@DeleteMapping("/{promptId}")
	public ResponseEntity<String> deletePrompt(@PathVariable long promptId) {
		log.info(FILENAME + "[deletePrompt Request]--->" + promptId);
		promptService.deletePromptById(promptId);
		log.info(
				FILENAME + "[deletePrompt Request]--->" + ApplicationConstants.PROMPTS_DELETION_MSG + "-->" + promptId);
		return new ResponseEntity<String>(ApplicationConstants.PROMPTS_DELETION_MSG, HttpStatus.ACCEPTED);

	}

	@PutMapping("/{promptId}")
	@Produces("application/json")
	public ResponseEntity<PromptDTO> updatePrompt(@Valid @RequestBody PromptRequestDTO promptRequest,
			@PathVariable long promptId) {
		log.info(FILENAME + "[updatePrompt Request prompt Id]--->" + promptId);
		log.info(FILENAME + "[updatePrompt RequestBody]--->" + promptRequest.toString());
		PromptDTO promptResponse = promptService.updatePromptById(promptRequest, promptId);
		log.info(FILENAME + "[updatePrompt Response]--->" + ApplicationConstants.PROMPTS_UPDATE_MSG + "-->" + promptId);
		return new ResponseEntity<PromptDTO>(promptResponse, HttpStatus.ACCEPTED);

	}

}