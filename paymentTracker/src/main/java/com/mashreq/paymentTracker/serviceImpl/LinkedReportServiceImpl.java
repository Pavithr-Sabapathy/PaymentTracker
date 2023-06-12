package com.mashreq.paymentTracker.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.dto.LinkedReportRequestDTO;
import com.mashreq.paymentTracker.dto.LinkedReportResponseDTO;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.LinkedReportInfo;
import com.mashreq.paymentTracker.model.Metrics;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.repository.LinkedReportRepository;
import com.mashreq.paymentTracker.repository.MetricsRepository;
import com.mashreq.paymentTracker.repository.ReportConfigurationRepository;
import com.mashreq.paymentTracker.service.LinkReportService;

@Component
public class LinkedReportServiceImpl implements LinkReportService {

	private static final Logger log = LoggerFactory.getLogger(LinkedReportServiceImpl.class);
	private static final String FILENAME = "LinkedReportServiceImpl";

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	LinkedReportRepository linkedReportRepo;

	@Autowired
	MetricsRepository metricsRepository;

	@Autowired
	ReportConfigurationRepository reportConfigurationRepo;

	@Override
	public void saveOrUpdateLinkedReport(LinkedReportRequestDTO linkedReportRequestDTO) {
		/** Whether to save or update based on linked report id ***/
		OptionalLong linkedReportId = OptionalLong.of(linkedReportRequestDTO.getId());
		LinkedReportInfo linkedReportModel = modelMapper.map(linkedReportRequestDTO, LinkedReportInfo.class);
		if (linkedReportId.isPresent()) {
			log.info(FILENAME + "[Updating Report Request]--->" + linkedReportRequestDTO.toString());
		} else {
			log.info(FILENAME + "[save Report Request]--->" + linkedReportRequestDTO.toString());
		}
		linkedReportRepo.save(linkedReportModel);

	}

	@Override
	public List<LinkedReportResponseDTO>  fetchLinkedReportByReportId(long reportId) {
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
				linkedReportDTOresponse.setId(linkedReportResponse.getId());
				linkedReportDTOresponse.setLinkedReportID(linkedReportResponse.getLinkedReportId());
				linkedReportDTOresponse.setReportId(linkedReportResponse.getReportId());
				linkedReportDTOresponse.setSourceMetrics(linkedReportResponse.getSourceMetrics());
				linkedReportDTOresponse.setActive(linkedReportResponse.getActive());
				linkedReportDTOresponse.setLinkDescription(linkedReportResponse.getLinkDescription());
				linkedReportDTOresponse.setLinkName(linkedReportResponse.getLinkName());
				linkedReportDTOresponseList.add(linkedReportDTOresponse);
			});
			
		}
		return linkedReportDTOresponseList;
	}

	@Override
	public LinkedReportResponseDTO fetchLinkedReportById(long linkedReportId) {
		LinkedReportResponseDTO linkedReportDTOresponse = new LinkedReportResponseDTO();
		Optional<LinkedReportInfo> linkedReportOptionalResponse = linkedReportRepo.findById(linkedReportId);
		if (linkedReportOptionalResponse.isEmpty()) {
			log.error(FILENAME + "[fetchLinkedReportById] " + ApplicationConstants.LINK_REPORT_DOES_NOT_EXISTS
					+ linkedReportId);
			throw new ResourceNotFoundException(ApplicationConstants.LINK_REPORT_DOES_NOT_EXISTS + linkedReportId);
		} else {
			LinkedReportInfo linkedReportResponse = linkedReportOptionalResponse.get();
			linkedReportDTOresponse.setId(linkedReportResponse.getId());
			linkedReportDTOresponse.setLinkName(linkedReportResponse.getLinkName());
			linkedReportDTOresponse.setLinkDescription(linkedReportResponse.getLinkDescription());
			linkedReportDTOresponse.setActive(linkedReportResponse.getActive());
			/**
			 * Map the metrics and source report name from metrics table based on mapping
			 **/
			long sourceMetricId = linkedReportResponse.getSourceMetrics().getId();
			Optional<Metrics> metricsOptional = metricsRepository.findById(sourceMetricId);
			if (linkedReportOptionalResponse.isEmpty()) {
				log.error(FILENAME + "[fetchLinkedReportById] " + ApplicationConstants.METRICS_DOES_NOT_EXISTS
						+ sourceMetricId);
				throw new ResourceNotFoundException(ApplicationConstants.METRICS_DOES_NOT_EXISTS + sourceMetricId);
			} else {
				Metrics metricsDeatils = metricsOptional.get();
				linkedReportDTOresponse.setSourceMetricName(metricsDeatils.getDisplayName());
				linkedReportDTOresponse.setReportName(metricsDeatils.getReport().getReportName());
			}
			/**
			 * Map the linked report name from the report table based on linked report id
			 **/
			Optional<Report> reportReponseOptional = reportConfigurationRepo
					.findById(linkedReportResponse.getLinkedReportId());
			if (reportReponseOptional.isEmpty()) {
				throw new ResourceNotFoundException(
						ApplicationConstants.REPORT_DOES_NOT_EXISTS + linkedReportResponse.getLinkedReportId());
			} else {
				Report reportRepsonse = reportReponseOptional.get();
				linkedReportDTOresponse.setLinkedReportName(reportRepsonse.getReportName());
			}

		}
		return linkedReportDTOresponse;
	}
}
