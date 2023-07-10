package com.mashreq.paymentTracker.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.dto.LinkedReportRequestDTO;
import com.mashreq.paymentTracker.dto.LinkedReportResponseDTO;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.ApplicationModule;
import com.mashreq.paymentTracker.model.ComponentDetails;
import com.mashreq.paymentTracker.model.Components;
import com.mashreq.paymentTracker.model.LinkedReportInfo;
import com.mashreq.paymentTracker.model.Metrics;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.repository.ComponentsDetailsRepository;
import com.mashreq.paymentTracker.repository.ComponentsRepository;
import com.mashreq.paymentTracker.repository.LinkedReportRepository;
import com.mashreq.paymentTracker.repository.MetricsRepository;
import com.mashreq.paymentTracker.repository.ModuleRepository;
import com.mashreq.paymentTracker.repository.ReportConfigurationRepository;
import com.mashreq.paymentTracker.service.LinkReportService;

@Component
public class LinkedReportServiceImpl implements LinkReportService {

	private static final Logger log = LoggerFactory.getLogger(LinkedReportServiceImpl.class);
	private static final String FILENAME = "LinkedReportServiceImpl";

	@Autowired
	LinkedReportRepository linkedReportRepo;

	@Autowired
	MetricsRepository metricsRepository;

	@Autowired
	ReportConfigurationRepository reportConfigurationRepo;

	@Autowired
	ModuleRepository moduleRepository;

	@Autowired
	private ComponentsRepository componentRepository;

	@Autowired
	private ComponentsDetailsRepository componentsDetailsRepository;

	@Override
	public LinkedReportResponseDTO saveOrUpdateLinkedReport(LinkedReportRequestDTO linkedReportRequestDTO) {
		LinkedReportResponseDTO linkedReportResponseDTO = new LinkedReportResponseDTO();
		LinkedReportInfo linkedReportModel = new LinkedReportInfo();
		if(linkedReportRequestDTO.getId() != 0L) {
			linkedReportModel.setId(linkedReportRequestDTO.getId());
		}
		/** Whether to save or update based on linked report id ***/
		Optional<ApplicationModule> moduleOptional = moduleRepository.findById(linkedReportRequestDTO.getModuleId());
		if (moduleOptional.isPresent()) {
			ApplicationModule moduleObj = moduleOptional.get();
			linkedReportModel.setModule(moduleObj);
		}
		Optional<Components> componentOptional = componentRepository.findById(linkedReportRequestDTO.getComponentId());
		if (componentOptional.isPresent()) {
			Components componentObj = componentOptional.get();
			linkedReportModel.setComponentId(componentObj);
		}
		Optional<ComponentDetails> componentDetailOptional = componentsDetailsRepository
				.findById(linkedReportRequestDTO.getComponentDetailId());
		if (componentDetailOptional.isPresent()) {
			ComponentDetails componentDetailsObj = componentDetailOptional.get();
			linkedReportModel.setComponentDetailId(componentDetailsObj);
		}
		Optional<Report> reportOptional = reportConfigurationRepo.findById(linkedReportRequestDTO.getReportId());
		if (reportOptional.isPresent()) {
			Report reportObj = reportOptional.get();
			linkedReportModel.setReport(reportObj);
		}
		Optional<Report> linkedReportOptioanl = reportConfigurationRepo
				.findById(linkedReportRequestDTO.getLinkedReportId());
		if (linkedReportOptioanl.isPresent()) {
			Report linkedReportObj = linkedReportOptioanl.get();
			linkedReportModel.setLinkedReport(linkedReportObj);
		}
		Optional<Metrics> metricsResponseOptional = metricsRepository
				.findById(linkedReportRequestDTO.getSourceMetricId());
		if (metricsResponseOptional.isPresent()) {
			linkedReportModel.setSourceMetrics(metricsResponseOptional.get());
		}
		OptionalLong linkedReportId = OptionalLong.of(linkedReportRequestDTO.getId());
		linkedReportModel.setActive(linkedReportRequestDTO.getActive());
		linkedReportModel.setLinkDescription(linkedReportRequestDTO.getLinkDescription());
		linkedReportModel.setLinkName(linkedReportRequestDTO.getLinkName());

		if (linkedReportId.isPresent()) {
			log.info(FILENAME + "[Updating Report Request]--->" + linkedReportRequestDTO.toString());
		} else {
			log.info(FILENAME + "[save Report Request]--->" + linkedReportRequestDTO.toString());
		}
		LinkedReportInfo linkReportResponse = linkedReportRepo.save(linkedReportModel);
		if (null != linkReportResponse) {
			linkedReportResponseDTO = populateLinkReport(linkReportResponse);
		}
		return linkedReportResponseDTO;
	}

	@Override
	public List<LinkedReportResponseDTO> fetchLinkedReportByReportId(long reportId) {
		List<LinkedReportResponseDTO> linkedReportDTOresponseList = new ArrayList<LinkedReportResponseDTO>();
		Optional<List<LinkedReportInfo>> linkedReportOptionalResponseList = linkedReportRepo
				.findAllByReportId(reportId);
		if (linkedReportOptionalResponseList.isEmpty()) {
			log.error(FILENAME + "[fetchLinkedReportByReportId] " + ApplicationConstants.LINK_REPORT_DOES_NOT_EXISTS
					+ reportId);
			throw new ResourceNotFoundException(ApplicationConstants.LINK_REPORT_DOES_NOT_EXISTS + reportId);
		} else {
			List<LinkedReportInfo> linkedReportResponseList = linkedReportOptionalResponseList.get();
			linkedReportResponseList.forEach(linkedReportResponse -> {
				LinkedReportResponseDTO linkedReportDTOresponse = new LinkedReportResponseDTO();
				linkedReportDTOresponse = populateLinkReport(linkedReportResponse);
				linkedReportDTOresponseList.add(linkedReportDTOresponse);
			});

		}
		return linkedReportDTOresponseList;
	}

	@Override
	public LinkedReportResponseDTO fetchLinkedReportById(long linkedReportId) {
		LinkedReportResponseDTO linkedReportDTOresponse = new LinkedReportResponseDTO();
		Optional<LinkedReportInfo> LinkedReportResponseDTO = linkedReportRepo.findById(linkedReportId);
		if (LinkedReportResponseDTO.isEmpty()) {
			log.error(FILENAME + "[fetchLinkedReportById] " + ApplicationConstants.LINK_REPORT_DOES_NOT_EXISTS
					+ linkedReportId);
			throw new ResourceNotFoundException(ApplicationConstants.LINK_REPORT_DOES_NOT_EXISTS + linkedReportId);
		} else {
			LinkedReportInfo linkedReportResponse = LinkedReportResponseDTO.get();
			linkedReportDTOresponse = populateLinkReport(linkedReportResponse);
		}
		return linkedReportDTOresponse;
	}

	@Override
	public Map<Long, String> fetchLinkedReportByModuleId(long moduleId) {
		Map<Long, String> linkedReportMapping = new HashMap<Long, String>();
		Optional<List<LinkedReportInfo>> LinkedReportResponseDTO = linkedReportRepo.findByAllModuleId(moduleId);
		if (LinkedReportResponseDTO.isPresent()) {
			List<LinkedReportInfo> linkReportResponseList = LinkedReportResponseDTO.get();
			linkedReportMapping = linkReportResponseList.stream()
					.collect(Collectors.toMap(LinkedReportInfo::getId, LinkedReportInfo::getLinkName));
		} else {
			log.error(FILENAME + "[fetchLinkedReportByModuleId] " + ApplicationConstants.LINK_REPORT_DOES_NOT_EXISTS);
			throw new ResourceNotFoundException(ApplicationConstants.LINK_REPORT_DOES_NOT_EXISTS);
		}
		return linkedReportMapping;
	}

	private LinkedReportResponseDTO populateLinkReport(LinkedReportInfo linkReportResponse) {
		LinkedReportResponseDTO linkedReportResponseDTO = new LinkedReportResponseDTO();
		linkedReportResponseDTO.setId(linkReportResponse.getId());
		linkedReportResponseDTO.setActive(linkReportResponse.getActive());
		linkedReportResponseDTO.setLinkName(linkReportResponse.getLinkName());
		linkedReportResponseDTO.setComponent(linkReportResponse.getComponentId().getComponentName());
		linkedReportResponseDTO.setComponentDetail(linkReportResponse.getComponentDetailId().getQueryKey());
		linkedReportResponseDTO.setLinkDescription(linkReportResponse.getLinkDescription());
		linkedReportResponseDTO.setLinkedReportName(linkReportResponse.getLinkedReport().getReportName());
		linkedReportResponseDTO.setLinkedReportID(linkReportResponse.getLinkedReport().getId());
		linkedReportResponseDTO.setReportName(linkReportResponse.getReport().getReportName());
		linkedReportResponseDTO.setReportId(linkReportResponse.getReport().getId());
		linkedReportResponseDTO.setSourceMetricName(linkReportResponse.getSourceMetrics().getDisplayName());
		linkedReportResponseDTO.setSourceMetricId(linkReportResponse.getSourceMetrics().getId());
		linkedReportResponseDTO.setModuleId(linkReportResponse.getModule().getId());
		linkedReportResponseDTO.setModuleName(linkReportResponse.getModule().getModuleName());
		return linkedReportResponseDTO;

	}
}
