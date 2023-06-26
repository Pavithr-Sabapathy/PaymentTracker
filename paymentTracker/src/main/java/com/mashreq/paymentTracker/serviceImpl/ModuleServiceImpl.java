package com.mashreq.paymentTracker.serviceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.dto.ModuleDTO;
import com.mashreq.paymentTracker.dto.ReportDTO;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.ApplicationModule;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.repository.ModuleRepository;
import com.mashreq.paymentTracker.service.ModuleService;
import com.mashreq.paymentTracker.service.ReportConfigurationService;

@Component
public class ModuleServiceImpl implements ModuleService {

	private static final Logger log = LoggerFactory.getLogger(ModuleServiceImpl.class);
	private static final String FILENAME = "ModuleServiceImpl";

	@Autowired
	ModuleRepository moduleRepository;

	@Autowired
	ReportConfigurationService reportConfigurationService;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	public List<ApplicationModule> fetchAllModule() {
		List<ApplicationModule> moduleList = moduleRepository.findAll();
		return moduleList;
	}

	@Override
	public ApplicationModule saveModule(ModuleDTO moduleRequest) {
		ModuleDTO moduleDTOResponse = new ModuleDTO();
		ApplicationModule applicationModule = modelMapper.map(moduleRequest, ApplicationModule.class);
		List<ReportDTO> reportList = reportConfigurationService.fetchReportsByModule(moduleRequest.getName());
		applicationModule.setError((reportList.isEmpty()) ? ApplicationConstants.REPORT_ERROR_DESCRIPTION : null);
		List<ReportDTO> activeReportList = reportList.stream()
				.filter(report -> report.getActive().equalsIgnoreCase("Y")).collect(Collectors.toList());
		applicationModule
				.setWarning((activeReportList.isEmpty() ? ApplicationConstants.REPORT_WARNING_DESCRIPTION : null));
		log.info(FILENAME + "[saveModule]" + moduleDTOResponse.toString());
		applicationModule = moduleRepository.save(applicationModule);
		return applicationModule;
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

	@Override
	public ApplicationModule fetchModuleByName(String moduleName) {
		ApplicationModule module = moduleRepository.findByModuleName(moduleName);
		if (null == module) {
			log.error(FILENAME + "[fetchModuleByName]" + ApplicationConstants.MODULE_DOES_NOT_EXISTS + moduleName);
			throw new ResourceNotFoundException(ApplicationConstants.MODULE_DOES_NOT_EXISTS + moduleName);
		}
		log.info(FILENAME + "[fetchModuleByName]--->" + moduleName + "--->" + module.toString());
		return module;
	}

	@Override
	public ApplicationModule updateModule(ModuleDTO moduleRequest, Long moduleId) {
		ApplicationModule applicationModule = new ApplicationModule();

		Optional<ApplicationModule> moduleOptional = moduleRepository.findById(moduleId);
		if (moduleOptional.isEmpty()) {
			log.error(FILENAME + "[updateReportById]" + ApplicationConstants.MODULE_DOES_NOT_EXISTS + moduleId);
			throw new ResourceNotFoundException(ApplicationConstants.MODULE_DOES_NOT_EXISTS + moduleId);
		} else {
			applicationModule = modelMapper.map(moduleRequest, ApplicationModule.class);
			List<ReportDTO> reportList = reportConfigurationService
					.fetchReportsByModule(applicationModule.getModuleName());
			applicationModule.setError((reportList.isEmpty()) ? ApplicationConstants.REPORT_ERROR_DESCRIPTION : null);
			List<ReportDTO> activeReportList = reportList.stream()
					.filter(report -> report.getActive().equalsIgnoreCase("Y")).collect(Collectors.toList());
			applicationModule
					.setWarning((activeReportList.isEmpty() ? ApplicationConstants.REPORT_WARNING_DESCRIPTION : null));
			applicationModule.setId(moduleId);
			applicationModule = moduleRepository.save(applicationModule);
		}
		return applicationModule;
	}
}