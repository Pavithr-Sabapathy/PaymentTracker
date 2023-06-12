package com.mashreq.paymentTracker.service;

import com.mashreq.paymentTracker.dto.APIResponse;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.ReportExecutionRequest;
import com.mashreq.paymentTracker.exception.ReportException;

public interface ReportHandlerService{

	ReportExecuteResponseData executeReport(String reportName, ReportExecutionRequest reportExecutionRequest) throws ReportException;

	APIResponse populateSuccessAPIRespone(ReportExecuteResponseData flexList);
	
}