package com.mashreq.paymentTracker.service;

import com.mashreq.paymentTracker.dto.ComponentDetailsRequestDTO;
import com.mashreq.paymentTracker.dto.ComponentsRequestDTO;

public interface ComponentsService {

	void saveComponents(ComponentsRequestDTO componentsRequest);

	void deleteComponentById(long componentId);

	void saveComponentsDetails(ComponentDetailsRequestDTO componentDetailsRequest);

	void deleteComponentDetailsById(long componentDetailId);

}
