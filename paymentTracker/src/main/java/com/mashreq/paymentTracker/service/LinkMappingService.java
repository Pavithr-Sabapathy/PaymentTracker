package com.mashreq.paymentTracker.service;

import java.util.List;

import com.mashreq.paymentTracker.dto.LinkMappingResponseDTO;
import com.mashreq.paymentTracker.dto.LinkedReportMappingRequestDTO;

public interface LinkMappingService {

	void saveOrUpdateLinkMapping(LinkedReportMappingRequestDTO linkedReportMappingRequestDTO);

	List<LinkMappingResponseDTO> fetchLinkMappingById(long id);

}
