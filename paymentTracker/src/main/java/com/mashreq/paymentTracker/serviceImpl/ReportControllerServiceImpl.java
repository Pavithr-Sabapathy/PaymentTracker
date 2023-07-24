package com.mashreq.paymentTracker.serviceImpl;

import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseData;
import com.mashreq.paymentTracker.service.ReportControllerService;
import com.mashreq.paymentTracker.service.ReportInput;

public abstract class ReportControllerServiceImpl implements ReportControllerService {

	@Override
	public final ReportExecuteResponseData executeReport(ReportContext reportContext) {
		
		ReportInput reportInput = populateBaseInputContext(reportContext);
		
		ReportExecuteResponseData reportExecuteResponseData = processReport(reportInput,reportContext);
		
		return reportExecuteResponseData;
	}

	protected abstract ReportInput populateBaseInputContext(ReportContext reportContext);

	protected abstract ReportExecuteResponseData processReport(ReportInput reportInput, ReportContext reportContext);

}