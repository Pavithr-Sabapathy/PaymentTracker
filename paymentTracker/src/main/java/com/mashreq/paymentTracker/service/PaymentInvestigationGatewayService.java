package com.mashreq.paymentTracker.service;

import com.mashreq.paymentTracker.dto.PaymentInvestigationReportInput;
import com.mashreq.paymentTracker.dto.ReportContext;

public interface PaymentInvestigationGatewayService {

	void processGateway(PaymentInvestigationReportInput paymentInvestigationReportInput, ReportContext reportContext);
}