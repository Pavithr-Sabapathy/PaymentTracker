package com.mashreq.paymentTracker.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.dao.ModuleDAO;
import com.mashreq.paymentTracker.dto.ModuleDTO;
import com.mashreq.paymentTracker.dto.ModuleResponseDTO;
import com.mashreq.paymentTracker.dto.ReportDTO;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.ApplicationModule;
import com.mashreq.paymentTracker.service.ModuleService;
import com.mashreq.paymentTracker.service.ReportConfigurationService;

@Component
public class ModuleServiceImpl implements ModuleService {

	private static final Logger log = LoggerFactory.getLogger(ModuleServiceImpl.class);
	private static final String FILENAME = "ModuleServiceImpl";

	@Autowired
	ModuleDAO moduleDAO;

	@Autowired
	ReportConfigurationService reportConfigurationService;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	public List<ModuleResponseDTO> fetchAllModule() {
		List<ApplicationModule> moduleList = moduleDAO.findAll();
		List<ModuleResponseDTO> moduleResponseDTOList = modelMapper.map(moduleList,
				new TypeToken<List<ModuleResponseDTO>>() {
				}.getType());
		return moduleResponseDTOList;
	}

	@Override
	public ModuleResponseDTO saveModule(ModuleDTO moduleRequest) {
		ModuleDTO moduleDTOResponse = new ModuleDTO();
		ApplicationModule applicationModule = modelMapper.map(moduleRequest, ApplicationModule.class);
		List<ReportDTO> reportList = reportConfigurationService.fetchReportsByModule(moduleRequest.getName());
		applicationModule.setError((reportList.isEmpty()) ? ApplicationConstants.REPORT_ERROR_DESCRIPTION : null);
		List<ReportDTO> activeReportList = reportList.stream()
				.filter(report -> report.getActive().equalsIgnoreCase("Y")).collect(Collectors.toList());
		applicationModule
				.setWarning((activeReportList.isEmpty() ? ApplicationConstants.REPORT_WARNING_DESCRIPTION : null));
		log.info(FILENAME + "[saveModule]" + moduleDTOResponse.toString());
		applicationModule = moduleDAO.save(applicationModule);
		ModuleResponseDTO moduleResponseDTO = modelMapper.map(applicationModule, ModuleResponseDTO.class);
		return moduleResponseDTO;
	}

	@Override
	public void deleteModule(long moduleId) {
		moduleDAO.deleteById(moduleId);
	}

	@Override
	public List<ModuleResponseDTO> fetchModuleByName(String moduleName) {
		List<ApplicationModule> moduleList = moduleDAO.findByModuleName(moduleName);
		if (null == moduleList) {
			log.error(FILENAME + "[fetchModuleByName]" + ApplicationConstants.MODULE_DOES_NOT_EXISTS + moduleName);
			throw new ResourceNotFoundException(ApplicationConstants.MODULE_DOES_NOT_EXISTS + moduleName);
		}
		log.info(FILENAME + "[fetchModuleByName]--->" + moduleName + "--->" + moduleList.toString());
		List<ModuleResponseDTO> moduleResponseDTOList = modelMapper.map(moduleList,
				new TypeToken<List<ModuleResponseDTO>>() {
				}.getType());
		return moduleResponseDTOList;
	}

	@Override
	public ModuleResponseDTO updateModule(ModuleDTO moduleRequest, Long moduleId) {
		ApplicationModule applicationModule = new ApplicationModule();

		ApplicationModule module = moduleDAO.findById(moduleId);
		if (null == module) {
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
			applicationModule = moduleDAO.update(applicationModule);
			ModuleResponseDTO moduleResponseDTO = modelMapper.map(applicationModule, ModuleResponseDTO.class);
			return moduleResponseDTO;
		}
	}
}