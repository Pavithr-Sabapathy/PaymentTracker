package com.mashreq.paymentTracker.service;

import java.util.List;

import com.mashreq.paymentTracker.dto.PaymentInvestigationReportInput;
import com.mashreq.paymentTracker.dto.PaymentInvestigationReportOutput;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.model.Components;

public interface PaymentInvestigationGatewayService {

	public List<? extends ReportOutput> processComponent(PaymentInvestigationReportInput paymentInvestigationReportInput,
			List<Components> componentList, ReportContext reportContext, String componentKey,
			List<PaymentInvestigationReportOutput> reportOutputList);

	void processGateway(PaymentInvestigationReportInput paymentInvestigationReportInput, List<Components> componentList,
			ReportContext reportContext, List<PaymentInvestigationReportOutput> reportOutputList);

	public void processChannels(
			PaymentInvestigationReportInput paymentInvestigationReportInput, ReportContext reportContext, List<Components> componentList,
			List<PaymentInvestigationReportOutput> reportOutputList);

}