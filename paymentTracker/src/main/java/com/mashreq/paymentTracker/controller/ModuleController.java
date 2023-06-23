package com.mashreq.paymentTracker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.mashreq.paymentTracker.model.ApplicationModule;
import com.mashreq.paymentTracker.service.ModuleService;

@RestController
@Component
@RequestMapping("/module")
public class ModuleController {

	@Autowired
	ModuleService moduleService;

	@GetMapping
	public ResponseEntity<List<ApplicationModule>> fetchModule() {
		List<ApplicationModule> moduleList = moduleService.fetchAllModule();
		return ResponseEntity.ok(moduleList);

	}

	@PostMapping
	public ResponseEntity<String> saveModule(@RequestBody ApplicationModule moduleRequest) {
		moduleService.saveModule(moduleRequest);
		return ResponseEntity.ok(ApplicationConstants.MODULE_CREATION_MSG);
	}
	
	@DeleteMapping("/{moduleId}")
	public ResponseEntity<String> deleteModule(@PathVariable long moduleId){
		moduleService.deleteModule(moduleId);
		return ResponseEntity.ok(ApplicationConstants.MODULE_DELETION_MSG);
	}
	
	@GetMapping("{moduleName}")
	public ResponseEntity<ApplicationModule> fetchModuleByName(@PathVariable("moduleName") String moduleName) {
		ApplicationModule module = moduleService.fetchModuleByName(moduleName);
		return ResponseEntity.ok(module);

	}
	
}