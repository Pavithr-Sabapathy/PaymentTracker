package com.mashreq.paymentTracker.service;

import java.util.List;

import com.mashreq.paymentTracker.dto.FederatedReportComponentDetailContext;
import com.mashreq.paymentTracker.dto.ReportComponentDetailDTO;
import com.mashreq.paymentTracker.dto.ReportOutput;

public interface QueryExecutorService {

	List<ReportOutput> executeQuery(ReportComponentDetailDTO componentDetail,
			FederatedReportComponentDetailContext context);

}
