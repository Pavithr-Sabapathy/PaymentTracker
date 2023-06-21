package com.mashreq.paymentTracker.service;

import java.util.List;

import com.mashreq.paymentTracker.model.ApplicationModule;

public interface ModuleService{

	List<ApplicationModule> fetchAllModule();

	void saveModule(ApplicationModule moduleRequest);

	void deleteModule(long moduleId);

	ApplicationModule fetchModuleByName(String moduleName);
	
}