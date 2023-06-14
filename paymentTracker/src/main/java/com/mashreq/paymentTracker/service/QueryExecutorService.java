package com.mashreq.paymentTracker.service;

import java.util.List;

import com.mashreq.paymentTracker.dto.FlexReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.SourceQueryExecutionContext;
import com.mashreq.paymentTracker.exception.ReportException;

public interface QueryExecutorService {

	List<FlexReportExecuteResponseData> executeQuery(SourceQueryExecutionContext sourceQueryExecutionContext)
			throws ReportException;

}
