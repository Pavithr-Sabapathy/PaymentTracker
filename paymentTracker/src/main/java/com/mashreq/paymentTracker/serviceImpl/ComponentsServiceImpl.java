package com.mashreq.paymentTracker.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.dto.ComponentDTO;
import com.mashreq.paymentTracker.dto.ComponentDetailsRequestDTO;
import com.mashreq.paymentTracker.dto.ComponentsRequestDTO;
import com.mashreq.paymentTracker.dto.ReportComponentDetailDTO;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.ComponentDetails;
import com.mashreq.paymentTracker.model.Components;
import com.mashreq.paymentTracker.model.ComponentsCountry;
import com.mashreq.paymentTracker.model.DataSource;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.repository.ComponentsCountryRepository;
import com.mashreq.paymentTracker.repository.ComponentsDetailsRepository;
import com.mashreq.paymentTracker.repository.ComponentsRepository;
import com.mashreq.paymentTracker.repository.DataSourceRepository;
import com.mashreq.paymentTracker.repository.ReportConfigurationRepository;
import com.mashreq.paymentTracker.service.ComponentsService;
import com.mashreq.paymentTracker.utility.CheckType;

@Component
public class ComponentsServiceImpl implements ComponentsService {

	private static final Logger log = LoggerFactory.getLogger(ComponentsServiceImpl.class);
	private static final String FILENAME = "ComponentsServiceImpl";

	@Autowired
	private ComponentsRepository componentRepository;

	@Autowired
	private ComponentsCountryRepository componentsCountryRepository;

	@Autowired
	private ReportConfigurationRepository reportConfigurationRepo;

	@Autowired
	private ComponentsDetailsRepository componentsDetailsRepository;

	@Autowired
	private DataSourceRepository dataSourceConfigRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	@Transactional
	public void saveComponents(ComponentsRequestDTO componentsRequest) {
		Components componentsObject = new Components();
		Optional<Report> reportOptional = reportConfigurationRepo.findById(componentsRequest.getReportId());
		if (reportOptional.isEmpty()) {
			throw new ResourceNotFoundException(
					ApplicationConstants.REPORT_DOES_NOT_EXISTS + componentsRequest.getReportId());
		} else {
			componentsObject.setActive(componentsRequest.getActive());
			componentsObject.setComponentKey(componentsRequest.getComponentKey());
			componentsObject.setComponentName(componentsRequest.getComponentName());
			componentsObject.setReport(reportOptional.get());
			Components componentsResponse = componentRepository.save(componentsObject);

			Optional<DataSource> dataSourceConfigurationOptional = dataSourceConfigRepository
					.findById(componentsRequest.getDataSourceId());
			if (dataSourceConfigurationOptional.isEmpty()) {
				log.error(FILENAME + "[saveComponents] " + ApplicationConstants.DATA_SOURCE_CONFIG_DOES_NOT_EXISTS
						+ componentsRequest.getDataSourceId());
				throw new ResourceNotFoundException(
						ApplicationConstants.DATA_SOURCE_CONFIG_DOES_NOT_EXISTS + componentsRequest.getDataSourceId());
			} else {
				saveComponentsCountryDetails(dataSourceConfigurationOptional.get(), componentsResponse);
			}

		}
	}

	@Transactional
	private void saveComponentsCountryDetails(DataSource dataSourceConfig, Components componentsResponse) {
		ComponentsCountry componentsCountryObject = new ComponentsCountry();
		componentsCountryObject.setCountry(dataSourceConfig.getCountry());
		componentsCountryObject.setDataSourceConfig(dataSourceConfig);
		componentsCountryObject.setComponents(componentsResponse);
		componentsCountryRepository.save(componentsCountryObject);
	}

	@Override
	public void deleteComponentById(long componentId) {
		if (componentRepository.existsById(componentId)) {
			componentRepository.deleteById(componentId);
		} else {
			log.error(
					FILENAME + "[deleteComponentById] " + ApplicationConstants.COMPONENT_DOES_NOT_EXISTS + componentId);

			throw new ResourceNotFoundException(ApplicationConstants.COMPONENT_DOES_NOT_EXISTS + componentId);
		}
		log.info(FILENAME + "[deleteComponentById]--->" + ApplicationConstants.COMPONENT_DELETION_MSG);
	}

	@Override
	public void saveComponentsDetails(ComponentDetailsRequestDTO componentDetailsRequest) {
		ComponentDetails componentDetailsObject = new ComponentDetails();
		Optional<Components> componentsOptional = componentRepository
				.findById(componentDetailsRequest.getCompReportId());
		if (!componentsOptional.isEmpty()) {
			componentDetailsObject.setQuery(componentDetailsRequest.getQuery());
			componentDetailsObject.setQueryKey(componentDetailsRequest.getQueryKey());
			componentDetailsObject.setComponents(componentsOptional.get());
			componentsDetailsRepository.save(componentDetailsObject);
		} else {
			log.error(FILENAME + "[saveComponentsDetails] " + ApplicationConstants.COMPONENT_DOES_NOT_EXISTS
					+ componentDetailsRequest.getCompReportId());

			throw new ResourceNotFoundException(
					ApplicationConstants.COMPONENT_DOES_NOT_EXISTS + componentDetailsRequest.getCompReportId());
		}

	}

	@Override
	public void deleteComponentDetailsById(long componentDetailId) {
		if (componentsDetailsRepository.existsById(componentDetailId)) {
			componentsDetailsRepository.deleteById(componentDetailId);
		} else {
			log.error(FILENAME + "[deleteComponentDetailsById] "
					+ ApplicationConstants.COMPONENT_DETAILS_DOES_NOT_EXISTS + componentDetailId);

			throw new ResourceNotFoundException(
					ApplicationConstants.COMPONENT_DETAILS_DOES_NOT_EXISTS + componentDetailId);
		}
		log.info(FILENAME + "[deleteComponentDetailsById]--->" + ApplicationConstants.COMPONENT_DETAILS_DELETION_MSG);
	}

	@Override
	public List<ComponentDTO> fetchComponentsByReportId(long reportId) {
		List<ComponentDTO> componentDTOList = new ArrayList<ComponentDTO>();
		Optional<List<Components>> componentOptional = componentRepository.findAllByreportId(reportId);
		if (componentOptional.isPresent()) {
			List<Components> componentList = componentOptional.get();
			componentList.stream().forEach(component -> {
				ComponentDTO componentDTO = new ComponentDTO();
				componentDTO.setComponentId(component.getId());
				componentDTO.setActive(component.getActive());
				componentDTO.setComponentKey(component.getComponentKey());
				componentDTO.setComponentName(component.getComponentName());
				if (null != component.getComponentsCountry()
						&& null != component.getComponentsCountry().getDataSourceConfig()) {
					componentDTO.setDataSourceId(component.getComponentsCountry().getDataSourceConfig().getId());
				}
				componentDTO.setReportId(component.getReport().getId());
				componentDTOList.add(componentDTO);
			});
		} else {
			log.error(FILENAME + "[findAllByreportId]" + ApplicationConstants.COMPONENT_REPORT_DOES_NOT_EXISTS
					+ reportId);
			throw new ResourceNotFoundException(ApplicationConstants.COMPONENT_REPORT_DOES_NOT_EXISTS);
		}
		log.info(FILENAME + "[findAllByreportId]" + componentDTOList.toString());
		return componentDTOList;
	}

	@Override
	public Map<String, Object> fetchComponentById(long componentId) {
		Map<String, Object> componentMap = new HashMap<String, Object>();
		ComponentDTO componentDTO = new ComponentDTO();
		List<ReportComponentDetailDTO> reportComponentDetails = new ArrayList<ReportComponentDetailDTO>();
		Optional<Components> componentsOptional = componentRepository.findById(componentId);
		if (componentsOptional.isPresent()) {
			Components components = componentsOptional.get();
			componentDTO.setComponentId(components.getId());
			componentDTO.setActive(components.getActive());
			componentDTO.setComponentKey(components.getComponentKey());
			componentDTO.setComponentName(components.getComponentName());
			if (null != components.getComponentsCountry()
					&& null != components.getComponentsCountry().getDataSourceConfig()) {
				componentDTO.setDataSourceId(components.getComponentsCountry().getDataSourceConfig().getId());
			}
			componentDTO.setReportId(components.getReport().getId());
			if (null != components.getComponentDetailsList()) {
				reportComponentDetails = modelMapper.map(components.getComponentDetailsList(),
						new TypeToken<List<ReportComponentDetailDTO>>() {
						}.getType());
			}
			componentMap.put("component", componentDTO);
			componentMap.put("ComponentDetails", reportComponentDetails);
		} else {
			log.error(FILENAME + "[fetchComponentById]" + ApplicationConstants.COMPONENT_DOES_NOT_EXISTS + componentId);
			throw new ResourceNotFoundException(ApplicationConstants.COMPONENT_DOES_NOT_EXISTS);
		}
		return componentMap;
	}
}