package com.mashreq.paymentTracker.service;

import java.util.List;

import com.mashreq.paymentTracker.dto.ReportContext;

public abstract class ReportConnector {

	public abstract List<? extends ReportOutput> processReportComponent(ReportInput reportInput, ReportContext reportContext);
}