package com.mashreq.paymentTracker.service;

import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.ReportInstanceDTO;

public interface FlexFederatedReportService {
	public ReportExecuteResponseData processFlexReport(ReportInstanceDTO reportInstanceDTO, ReportContext reportContext);
}