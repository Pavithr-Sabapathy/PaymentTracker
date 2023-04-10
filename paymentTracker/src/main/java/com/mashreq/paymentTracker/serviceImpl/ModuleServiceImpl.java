package com.mashreq.paymentTracker.serviceImpl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.ApplicationModule;
import com.mashreq.paymentTracker.repository.ModuleRepository;
import com.mashreq.paymentTracker.service.ModuleService;

@Component
public class ModuleServiceImpl implements ModuleService {

	private static final Logger log = LoggerFactory.getLogger(ModuleServiceImpl.class);
	private static final String FILENAME = "ModuleServiceImpl";

	@Autowired
	ModuleRepository moduleRepository;

	@Override
	public List<ApplicationModule> fetchAllModule() {
		List<ApplicationModule> moduleList = moduleRepository.findAll();
		return moduleList;
	}

	@Override
	public void saveModule(ApplicationModule moduleRequest) {
		moduleRepository.save(moduleRequest);
	}

	@Override
	public void deleteModule(long moduleId) {
		if (moduleRepository.existsById(moduleId)) {
			moduleRepository.deleteById(moduleId);
		} else {
			log.error(FILENAME + "[deleteModule] " + ApplicationConstants.MODULE_DOES_NOT_EXISTS + moduleId);

			throw new ResourceNotFoundException(ApplicationConstants.MODULE_DOES_NOT_EXISTS + moduleId);
		}
		log.info(FILENAME + "[deleteModule]--->" + ApplicationConstants.MODULE_DELETION_MSG);

	}

}