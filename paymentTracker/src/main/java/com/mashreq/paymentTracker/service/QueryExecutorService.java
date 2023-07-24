package com.mashreq.paymentTracker.service;

import java.util.List;

import com.mashreq.paymentTracker.dto.FlexReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.ReportComponentDTO;
import com.mashreq.paymentTracker.dto.ReportComponentDetailDTO;
import com.mashreq.paymentTracker.dto.FederatedReportComponentDetailContext;
import com.mashreq.paymentTracker.dto.ReportOutput;
import com.mashreq.paymentTracker.exception.ReportException;

public interface QueryExecutorService {

	List<ReportOutput> executeQuery(ReportComponentDetailDTO componentDetail,
			FederatedReportComponentDetailContext context);

}
