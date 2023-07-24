package com.mashreq.paymentTracker.dao;

import com.mashreq.paymentTracker.model.LinkedReportDetails;

public interface LinkMappingDAO {

	LinkedReportDetails save(LinkedReportDetails linkReportDetails);

	LinkedReportDetails findByLinkReportPromptId(long linkPromptId);

	LinkedReportDetails update(LinkedReportDetails linkReportDetails);

}