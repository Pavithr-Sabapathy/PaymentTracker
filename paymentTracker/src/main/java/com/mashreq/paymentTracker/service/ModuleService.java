package com.mashreq.paymentTracker.service;

import java.util.List;

import com.mashreq.paymentTracker.dto.ModuleDTO;
import com.mashreq.paymentTracker.model.ApplicationModule;

import jakarta.validation.Valid;

public interface ModuleService{

	List<ApplicationModule> fetchAllModule();

	ApplicationModule saveModule(@Valid ModuleDTO moduleRequest);

	void deleteModule(long moduleId);

	ApplicationModule fetchModuleByName(String moduleName);

	ApplicationModule updateModule(@Valid ModuleDTO moduleRequest, Long moduleId);
	
}