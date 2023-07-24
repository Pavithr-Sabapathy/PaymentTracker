package com.mashreq.paymentTracker.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.dao.ComponentDetailsDAO;
import com.mashreq.paymentTracker.dao.ComponentsDAO;
import com.mashreq.paymentTracker.dao.LinkedReportDAO;
import com.mashreq.paymentTracker.dao.MetricsDAO;
import com.mashreq.paymentTracker.dao.ModuleDAO;
import com.mashreq.paymentTracker.dao.ReportDAO;
import com.mashreq.paymentTracker.dto.LinkedReportRequestDTO;
import com.mashreq.paymentTracker.dto.LinkedReportResponseDTO;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.ApplicationModule;
import com.mashreq.paymentTracker.model.ComponentDetails;
import com.mashreq.paymentTracker.model.Components;
import com.mashreq.paymentTracker.model.LinkedReportInfo;
import com.mashreq.paymentTracker.model.Metrics;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.service.LinkReportService;

@Component
public class LinkedReportServiceImpl implements LinkReportService {

	private static final Logger log = LoggerFactory.getLogger(LinkedReportServiceImpl.class);
	private static final String FILENAME = "LinkedReportServiceImpl";

	@Autowired
	LinkedReportDAO linkedReportDAO;

	@Autowired
	MetricsDAO metricsDAO;

	@Autowired
	ReportDAO reportDAO;

	@Autowired
	ModuleDAO moduleDAO;

	@Autowired
	private ComponentsDAO componentsDAO;

	@Autowired
	private ComponentDetailsDAO componentsDetailsDAO;

	@Override
	public LinkedReportResponseDTO saveOrUpdateLinkedReport(LinkedReportRequestDTO linkedReportRequestDTO) {
		LinkedReportResponseDTO linkedReportResponseDTO = new LinkedReportResponseDTO();
		LinkedReportInfo linkReportResponse = new LinkedReportInfo();
		LinkedReportInfo linkedReportModel = new LinkedReportInfo();
		
		if (linkedReportRequestDTO.getId() != 0L) {
			linkedReportModel.setId(linkedReportRequestDTO.getId());
		}
		/** Whether to save or update based on linked report id ***/
		ApplicationModule moduleObj = moduleDAO.findById(linkedReportRequestDTO.getModuleId());
		linkedReportModel.setModule(moduleObj);

		Components componentObj = componentsDAO.findById(linkedReportRequestDTO.getComponentId());
		linkedReportModel.setComponentId(componentObj);

		ComponentDetails componentDetailsObj = componentsDetailsDAO
				.findById(linkedReportRequestDTO.getComponentDetailId());
		linkedReportModel.setComponentDetailId(componentDetailsObj);

		Report reportObj = reportDAO.getReportById(linkedReportRequestDTO.getReportId());
		linkedReportModel.setReport(reportObj);

		Report linkedReportObj = reportDAO.getReportById(linkedReportRequestDTO.getLinkedReportId());
		linkedReportModel.setLinkedReport(linkedReportObj);

		Metrics metricsObj = metricsDAO.getMetricsById(linkedReportRequestDTO.getSourceMetricId());
		linkedReportModel.setSourceMetrics(metricsObj);
		OptionalLong linkedReportId = OptionalLong.of(linkedReportRequestDTO.getId());
		linkedReportModel.setActive(linkedReportRequestDTO.getActive());
		linkedReportModel.setLinkDescription(linkedReportRequestDTO.getLinkDescription());
		linkedReportModel.setLinkName(linkedReportRequestDTO.getLinkName());

		if (linkedReportId.isPresent()) {
			log.info(FILENAME + "[Updating Report Request]--->" + linkedReportRequestDTO.toString());
			linkReportResponse = linkedReportDAO.update(linkedReportModel);
		} else {
			log.info(FILENAME + "[save Report Request]--->" + linkedReportRequestDTO.toString());
			linkReportResponse = linkedReportDAO.save(linkedReportModel);
		}
		
		if (null != linkReportResponse) {
			linkedReportResponseDTO = populateLinkReport(linkReportResponse);
		}
		return linkedReportResponseDTO;
	}

	@Override
	public List<LinkedReportResponseDTO> fetchLinkedReportByReportId(long reportId) {
		List<LinkedReportResponseDTO> linkedReportDTOresponseList = new ArrayList<LinkedReportResponseDTO>();
		List<LinkedReportInfo> linkedReportResponseList = linkedReportDAO.findAllByReportId(reportId);
		if (linkedReportResponseList.isEmpty()) {
			log.error(FILENAME + "[fetchLinkedReportByReportId] "
					+ ApplicationConstants.LINK_REPORT_DOES_NOT_EXISTS_REPORT_ID + reportId);
			throw new ResourceNotFoundException(ApplicationConstants.LINK_REPORT_DOES_NOT_EXISTS_REPORT_ID + reportId);
		} else {
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
		LinkedReportInfo linkedReportResponse = linkedReportDAO.findById(linkedReportId);
		if (null == linkedReportResponse) {
			log.error(FILENAME + "[fetchLinkedReportById] " + ApplicationConstants.LINK_REPORT_DOES_NOT_EXISTS
					+ linkedReportId);
			throw new ResourceNotFoundException(ApplicationConstants.LINK_REPORT_DOES_NOT_EXISTS + linkedReportId);
		} else {
			linkedReportDTOresponse = populateLinkReport(linkedReportResponse);
		}
		return linkedReportDTOresponse;
	}

	@Override
	public List<LinkedReportResponseDTO> fetchLinkedReportByModuleId(long moduleId) {
		List<LinkedReportResponseDTO> linkedReportResponseList = new ArrayList<LinkedReportResponseDTO>();
		List<LinkedReportInfo> linkReportResponseList = linkedReportDAO.findByAllModuleId(moduleId);
		if (!linkReportResponseList.isEmpty()) {
			linkReportResponseList.stream().forEach(linkReportResponse -> {
				LinkedReportResponseDTO linkedReportResponse = populateLinkReport(linkReportResponse);
				linkedReportResponseList.add(linkedReportResponse);
			});
		} else {
			log.error(FILENAME + "[fetchLinkedReportByModuleId] " + ApplicationConstants.LINK_REPORT_DOES_NOT_EXISTS);
			throw new ResourceNotFoundException(ApplicationConstants.LINK_REPORT_DOES_NOT_EXISTS);
		}
		return linkedReportResponseList;
	}

	private LinkedReportResponseDTO populateLinkReport(LinkedReportInfo linkReportResponse) {
		LinkedReportResponseDTO linkedReportResponseDTO = new LinkedReportResponseDTO();
		linkedReportResponseDTO.setId(linkReportResponse.getId());
		linkedReportResponseDTO.setActive(linkReportResponse.getActive());
		linkedReportResponseDTO.setLinkName(linkReportResponse.getLinkName());
		linkedReportResponseDTO.setComponent(linkReportResponse.getComponentId().getComponentName());
		linkedReportResponseDTO.setComponentId(linkReportResponse.getComponentId().getId());
		linkedReportResponseDTO.setComponentDetail(linkReportResponse.getComponentDetailId().getQueryKey());
		linkedReportResponseDTO.setComponentDetailId(linkReportResponse.getComponentDetailId().getId());
		linkedReportResponseDTO.setLinkDescription(linkReportResponse.getLinkDescription());
		linkedReportResponseDTO.setLinkedReportName(linkReportResponse.getLinkedReport().getReportName());
		linkedReportResponseDTO.setLinkedReportId(linkReportResponse.getLinkedReport().getId());
		linkedReportResponseDTO.setReportName(linkReportResponse.getReport().getReportName());
		linkedReportResponseDTO.setReportId(linkReportResponse.getReport().getId());
		linkedReportResponseDTO.setSourceMetricName(linkReportResponse.getSourceMetrics().getDisplayName());
		linkedReportResponseDTO.setSourceMetricId(linkReportResponse.getSourceMetrics().getId());
		linkedReportResponseDTO.setModuleId(linkReportResponse.getModule().getId());
		linkedReportResponseDTO.setModuleName(linkReportResponse.getModule().getModuleName());
		return linkedReportResponseDTO;

	}

	@Override
	public void deletelinkedReportById(long linkedReportId) {
		linkedReportDAO.deleteById(linkedReportId);
	}

}
