package com.mashreq.paymentTracker.service;

import java.util.List;
import java.util.Map;

import com.mashreq.paymentTracker.dto.ComponentDTO;
import com.mashreq.paymentTracker.dto.ComponentDetailsRequestDTO;
import com.mashreq.paymentTracker.dto.ComponentsRequestDTO;

public interface ComponentsService {

	void saveComponents(ComponentsRequestDTO componentsRequest);

	void deleteComponentById(long componentId);

	void saveComponentsDetails(ComponentDetailsRequestDTO componentDetailsRequest);

	void deleteComponentDetailsById(long componentDetailId);

	List<ComponentDTO> fetchComponentsByReportId(long reportId);

	Map<String, Object> fetchComponentById(long componentId);

}
