package com.mashreq.paymentTracker.serviceImpl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.dto.ComponentDetailsRequestDTO;
import com.mashreq.paymentTracker.dto.ComponentsRequestDTO;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.ComponentDetails;
import com.mashreq.paymentTracker.model.Components;
import com.mashreq.paymentTracker.model.ComponentsCountry;
import com.mashreq.paymentTracker.model.DataSourceConfig;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.repository.ComponentsCountryRepository;
import com.mashreq.paymentTracker.repository.ComponentsDetailsRepository;
import com.mashreq.paymentTracker.repository.ComponentsRepository;
import com.mashreq.paymentTracker.repository.DataSourceConfigRepository;
import com.mashreq.paymentTracker.repository.ReportConfigurationRepository;
import com.mashreq.paymentTracker.service.ComponentsService;

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
	private DataSourceConfigRepository dataSourceConfigRepository;

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

			Optional<DataSourceConfig> dataSourceConfigurationOptional = dataSourceConfigRepository
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
	private void saveComponentsCountryDetails(DataSourceConfig dataSourceConfig, Components componentsResponse) {
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
}