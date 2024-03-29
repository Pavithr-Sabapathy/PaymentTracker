package com.mashreq.paymentTracker.serviceImpl;

import java.util.OptionalLong;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.constants.MashreqFederatedReportConstants;
import com.mashreq.paymentTracker.dao.LinkMappingDAO;
import com.mashreq.paymentTracker.dao.MetricsDAO;
import com.mashreq.paymentTracker.dao.PromptsDAO;
import com.mashreq.paymentTracker.dto.LinkMappingResponseDTO;
import com.mashreq.paymentTracker.dto.LinkedReportMappingRequestDTO;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.LinkedReportDetails;
import com.mashreq.paymentTracker.model.Metrics;
import com.mashreq.paymentTracker.model.Prompts;
import com.mashreq.paymentTracker.service.LinkMappingService;

@Component
public class LinkMappingServiceImpl implements LinkMappingService {

	private static final Logger log = LoggerFactory.getLogger(LinkMappingServiceImpl.class);
	private static final String FILENAME = "LinkMappingServiceImpl";

	@Autowired
	ModelMapper modelMapper;

	@Autowired
	LinkMappingDAO linkMappingDAO;

	@Autowired
	MetricsDAO metricsDAO;

	@Autowired
	PromptsDAO promptsDAO;

	@Override
	public LinkedReportDetails saveOrUpdateLinkMapping(LinkedReportMappingRequestDTO linkedReportMappingRequestDTO) {
		LinkedReportDetails linkMappingResponse = new LinkedReportDetails();
		/** Whether to save or update based on link Mapping id ***/
		OptionalLong linkedReportId = OptionalLong.of(linkedReportMappingRequestDTO.getId());
		LinkedReportDetails linkReportDetails = modelMapper.map(linkedReportMappingRequestDTO,
				LinkedReportDetails.class);
		if (linkedReportId.isPresent()) {
			linkReportDetails.setId(linkedReportMappingRequestDTO.getId());
			log.info(FILENAME + "[Updating Link Mapping Request]--->" + linkedReportMappingRequestDTO.toString());
			linkMappingResponse = linkMappingDAO.update(linkReportDetails);
		} else {
			log.info(FILENAME + "[save Link Mapping Request]--->" + linkedReportMappingRequestDTO.toString());
			linkMappingResponse = linkMappingDAO.save(linkReportDetails);
		}

		return linkMappingResponse;
	}

	@Override
	public LinkMappingResponseDTO fetchLinkMappingById(long linkPromptId) {

		LinkedReportDetails linkMappingDetailResponse = linkMappingDAO.findByLinkReportPromptId(linkPromptId);
		LinkMappingResponseDTO linkMappingResponseDTO = new LinkMappingResponseDTO();
		linkMappingResponseDTO.setMappingType(linkMappingDetailResponse.getMappingType());
		linkMappingResponseDTO.setId(linkMappingDetailResponse.getId());
		linkMappingResponseDTO.setLinkReportPromptId(linkMappingDetailResponse.getLinkReportPromptId());
		/**
		 * Get the metrics entity from metrics table based on mapped id from link report
		 * detail table. If Mapping type is 'M' then get from Metric table else if
		 * Mapping type is 'P' then get from Prompts table
		 **/
		if (MashreqFederatedReportConstants.METRIC.equalsIgnoreCase(linkMappingResponseDTO.getMappingType())) {
			Metrics metricResponse = metricsDAO.getMetricsById(linkMappingDetailResponse.getMappedId());
			if (null == metricResponse) {
				throw new ResourceNotFoundException(
						ApplicationConstants.METRICS_DOES_NOT_EXISTS + linkMappingDetailResponse.getMappedId());
			} else {
				linkMappingResponseDTO.setMappedEntityId(metricResponse.getId());
				linkMappingResponseDTO.setMappedEntity(metricResponse.getDisplayName());
			}
		} else if (MashreqFederatedReportConstants.PROMPT
				.equalsIgnoreCase(linkMappingDetailResponse.getMappingType())) {
			Prompts promptsResponse = promptsDAO.getPromptById(linkMappingDetailResponse.getMappedId());
			if (null == promptsResponse) {
				throw new ResourceNotFoundException(
						ApplicationConstants.PROMPTS_DOES_NOT_EXISTS + linkMappingDetailResponse.getMappedId());
			} else {
				linkMappingResponseDTO.setMappedEntityId(promptsResponse.getId());
				linkMappingResponseDTO.setMappedEntity(promptsResponse.getDisplayName());
			}
		}
		return linkMappingResponseDTO;
	}

}
