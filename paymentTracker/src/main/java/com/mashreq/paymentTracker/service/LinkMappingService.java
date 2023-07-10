package com.mashreq.paymentTracker.service;

import java.util.List;

import com.mashreq.paymentTracker.dto.LinkMappingResponseDTO;
import com.mashreq.paymentTracker.dto.LinkedReportMappingRequestDTO;
import com.mashreq.paymentTracker.model.LinkedReportDetails;

public interface LinkMappingService {

	LinkedReportDetails saveOrUpdateLinkMapping(LinkedReportMappingRequestDTO linkedReportMappingRequestDTO);

	List<LinkMappingResponseDTO> fetchLinkMappingById(long id);

}
