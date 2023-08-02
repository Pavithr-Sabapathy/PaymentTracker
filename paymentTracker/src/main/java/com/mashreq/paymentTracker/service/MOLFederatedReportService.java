package com.mashreq.paymentTracker.service;

import java.util.List;

import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.ReportInstanceDTO;
import com.mashreq.paymentTracker.dto.ReportDefaultOutput;

public interface MOLFederatedReportService {
	List<ReportDefaultOutput> processMOLDetailedReport(ReportInstanceDTO reportInstanceDTO,
			ReportContext reportContext);

}
