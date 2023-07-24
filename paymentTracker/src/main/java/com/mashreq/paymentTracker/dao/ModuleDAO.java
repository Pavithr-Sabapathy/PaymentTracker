package com.mashreq.paymentTracker.dao;

import java.util.List;

import com.mashreq.paymentTracker.model.ApplicationModule;

public interface ModuleDAO{

	List<ApplicationModule> findAll();

	ApplicationModule save(ApplicationModule applicationModule);

	void deleteById(long moduleId);

	List<ApplicationModule> findByModuleName(String moduleName);

	ApplicationModule findById(Long moduleId);

	ApplicationModule update(ApplicationModule applicationModule);
	
}