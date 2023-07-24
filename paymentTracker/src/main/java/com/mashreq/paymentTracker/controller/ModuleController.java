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
import com.mashreq.paymentTracker.dto.ModuleDTO;
import com.mashreq.paymentTracker.dto.ModuleResponseDTO;
import com.mashreq.paymentTracker.service.ModuleService;

import jakarta.validation.Valid;

@RestController
@Component
@RequestMapping("/module")
public class ModuleController {

	@Autowired
	ModuleService moduleService;

	@GetMapping
	public ResponseEntity<List<ModuleResponseDTO>> fetchModule() {
		List<ModuleResponseDTO> moduleList = moduleService.fetchAllModule();
		return ResponseEntity.ok(moduleList);

	}

	@PostMapping
	public ResponseEntity<ModuleResponseDTO> saveModule(@Valid @RequestBody ModuleDTO moduleRequest) {
		 ModuleResponseDTO moduleDTOResponse = moduleService.saveModule(moduleRequest);
		return new ResponseEntity<ModuleResponseDTO>(moduleDTOResponse, HttpStatus.OK);
	}

	@PutMapping("/{moduleId}")
	public ResponseEntity<ModuleResponseDTO> updateReport(@Valid @RequestBody ModuleDTO moduleRequest,
			@PathVariable Long moduleId) {
		ModuleResponseDTO moduleDTOResponse = moduleService.updateModule(moduleRequest, moduleId);
		return new ResponseEntity<ModuleResponseDTO>(moduleDTOResponse, HttpStatus.OK);
	}

	@DeleteMapping("/{moduleId}")
	public ResponseEntity<String> deleteModule(@PathVariable long moduleId) {
		moduleService.deleteModule(moduleId);
		return ResponseEntity.ok(ApplicationConstants.MODULE_DELETION_MSG);
	}

	@GetMapping("/{moduleName}")
	public ResponseEntity<List<ModuleResponseDTO>> fetchModuleByName(@PathVariable("moduleName") String moduleName) {
		List<ModuleResponseDTO> moduleList = moduleService.fetchModuleByName(moduleName);
		return ResponseEntity.ok(moduleList);

	}

}