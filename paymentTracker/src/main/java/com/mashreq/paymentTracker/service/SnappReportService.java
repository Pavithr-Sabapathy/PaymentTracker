package com.mashreq.paymentTracker.service;

import java.util.List;

import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportInstanceDTO;
import com.mashreq.paymentTracker.dto.ReportOutput;

public interface SnappReportService {

	List<ReportOutput> processSnappDetailedReport(ReportInstanceDTO reportInstanceDTO,
			ReportContext reportContext);

}