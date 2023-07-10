package com.mashreq.paymentTracker.service;

import com.mashreq.paymentTracker.dto.LinkMappingResponseDTO;
import com.mashreq.paymentTracker.dto.LinkedReportMappingRequestDTO;
import com.mashreq.paymentTracker.model.LinkedReportDetails;

public interface LinkMappingService {

	LinkedReportDetails saveOrUpdateLinkMapping(LinkedReportMappingRequestDTO linkedReportMappingRequestDTO);

	LinkMappingResponseDTO fetchLinkMappingById(long id);

}
