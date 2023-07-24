package com.mashreq.paymentTracker.service;

import java.util.List;

import com.mashreq.paymentTracker.dto.ModuleDTO;
import com.mashreq.paymentTracker.dto.ModuleResponseDTO;

import jakarta.validation.Valid;

public interface ModuleService{

	List<ModuleResponseDTO> fetchAllModule();

	ModuleResponseDTO saveModule(@Valid ModuleDTO moduleRequest);

	void deleteModule(long moduleId);

	List<ModuleResponseDTO> fetchModuleByName(String moduleName);

	ModuleResponseDTO updateModule(@Valid ModuleDTO moduleRequest, Long moduleId);
	
}