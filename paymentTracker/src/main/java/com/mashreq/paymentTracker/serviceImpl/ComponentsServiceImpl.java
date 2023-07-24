package com.mashreq.paymentTracker.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.dao.ComponentDetailsDAO;
import com.mashreq.paymentTracker.dao.ComponentsCountryDAO;
import com.mashreq.paymentTracker.dao.ComponentsDAO;
import com.mashreq.paymentTracker.dao.ReportDAO;
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
import com.mashreq.paymentTracker.repository.DataSourceRepository;
import com.mashreq.paymentTracker.service.ComponentsService;

@Component
@Transactional
public class ComponentsServiceImpl implements ComponentsService {

	private static final Logger log = LoggerFactory.getLogger(ComponentsServiceImpl.class);
	private static final String FILENAME = "ComponentsServiceImpl";

	@Autowired
	private ComponentsDAO componentsDAO;

	@Autowired
	private ComponentsCountryDAO componentsCountryDAO;

	@Autowired
	private ComponentDetailsDAO componentDetailsDAO;

	@Autowired
	DataSourceRepository dataSourceRepo;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	ReportDAO reportDAO;

	@Override
	@Transactional
	public void saveComponents(ComponentsRequestDTO componentsRequest) {
		Components componentsResponse = new Components();
		Components componentsObject = new Components();
		Report report = reportDAO.getReportById(componentsRequest.getReportId());
		if (null == report) {
			throw new ResourceNotFoundException(
					ApplicationConstants.REPORT_DOES_NOT_EXISTS + componentsRequest.getReportId());
		} else {

			componentsObject.setActive(componentsRequest.getActive());
			componentsObject.setComponentKey(componentsRequest.getComponentKey());
			componentsObject.setComponentName(componentsRequest.getComponentName());
			componentsObject.setReport(report);
			if (null != componentsRequest.getComponentId()) {
				componentsObject.setId(componentsRequest.getComponentId());
				componentsResponse = componentsDAO.update(componentsObject);
			} else {
				componentsResponse = componentsDAO.save(componentsObject);
			}
			DataSource dataSource = dataSourceRepo.getDataSourceById(componentsRequest.getDataSourceId());
			if (null == dataSource) {
				log.error(FILENAME + "[saveComponents] " + ApplicationConstants.DATA_SOURCE_CONFIG_DOES_NOT_EXISTS
						+ componentsRequest.getDataSourceId());
				throw new ResourceNotFoundException(
						ApplicationConstants.DATA_SOURCE_CONFIG_DOES_NOT_EXISTS + componentsRequest.getDataSourceId());
			} else {
				saveComponentsCountryDetails(dataSource, componentsResponse);
			}

		}
	}

	@Transactional
	private void saveComponentsCountryDetails(DataSource dataSourceConfig, Components componentsResponse) {
		ComponentsCountry componentsCountryObject = new ComponentsCountry();
		componentsCountryObject.setCountry(dataSourceConfig.getCountry());
		componentsCountryObject.setDataSourceConfig(dataSourceConfig);
		componentsCountryObject.setComponents(componentsResponse);
		componentsCountryDAO.save(componentsCountryObject);
	}

	@Override
	public void deleteComponentById(long componentId) {
		componentsDAO.deleteById(componentId);
	}

	@Override
	public void saveComponentsDetails(ComponentDetailsRequestDTO componentDetailsRequest) {
		ComponentDetails componentDetailsObject = new ComponentDetails();
		Components componentObj = componentsDAO.findById(componentDetailsRequest.getCompReportId());
		if (null != componentObj) {
			componentDetailsObject.setQuery(componentDetailsRequest.getQuery());
			componentDetailsObject.setQueryKey(componentDetailsRequest.getQueryKey());
			componentDetailsObject.setComponents(componentObj);
			componentDetailsDAO.save(componentDetailsObject);
		} else {
			log.error(FILENAME + "[saveComponentsDetails] " + ApplicationConstants.COMPONENT_DOES_NOT_EXISTS
					+ componentDetailsRequest.getCompReportId());

			throw new ResourceNotFoundException(
					ApplicationConstants.COMPONENT_DOES_NOT_EXISTS + componentDetailsRequest.getCompReportId());
		}

	}

	@Override
	public void deleteComponentDetailsById(long componentDetailId) {
		componentDetailsDAO.deleteById(componentDetailId);
	}

	@Override
	public List<ComponentDTO> fetchComponentsByReportId(long reportId) {
		List<ComponentDTO> componentDTOList = new ArrayList<ComponentDTO>();
		List<Components> componentList = componentsDAO.findAllByreportId(reportId);
		if (!componentList.isEmpty()) {
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
		Components components = componentsDAO.findById(componentId);
		if (null != components) {
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