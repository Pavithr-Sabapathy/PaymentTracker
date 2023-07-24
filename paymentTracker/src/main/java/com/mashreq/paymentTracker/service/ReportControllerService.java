package com.mashreq.paymentTracker.service;

import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseData;

public interface ReportControllerService {

	ReportExecuteResponseData executeReport(ReportContext reportContext);
}
