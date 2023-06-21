package com.mashreq.paymentTracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mashreq.paymentTracker.model.DataSource;
import com.mashreq.paymentTracker.service.WebClientService;

import jakarta.validation.Valid;

@RestController
@Component
@RequestMapping("/WebClient")
public class WebClientController {
	
	@Autowired
	WebClientService webClientService;
	
	@GetMapping("/{dataSourceId}")
	public ResponseEntity<DataSource> getDataSourceConfig(@PathVariable Long dataSourceId) {
		DataSource dataSourceConfigResponse = webClientService.getDataSourceConfigById(dataSourceId);
		return ResponseEntity.ok(dataSourceConfigResponse);
	}
	
	@PostMapping("saveDataSource")
	public ResponseEntity<String> saveDataSourceConfig(
			@Valid @RequestBody DataSource dataSourceConfigurationRequest){
		String dataSourceConfigResponse = webClientService.saveDataSourceConfig(dataSourceConfigurationRequest);
		return ResponseEntity.ok(dataSourceConfigResponse);
		
	}
}