package com.mashreq.paymentTracker.service;

import java.util.List;
import java.util.Set;

import com.mashreq.paymentTracker.dto.CannedReport;
import com.mashreq.paymentTracker.dto.MessageDetailsFederatedReportInput;
import com.mashreq.paymentTracker.dto.ReportComponentDTO;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportInstanceComponentDTO;
import com.mashreq.paymentTracker.dto.SWIFTMessageDetailsFederatedReportOutput;
import com.mashreq.paymentTracker.model.Components;
import com.mashreq.paymentTracker.model.Report;

public interface CannedReportService {
	public CannedReport populateCannedReportInstance(Report report);

	public Set<ReportComponentDTO> populateCannedReportComponent(List<Components> componentList);

	public List<SWIFTMessageDetailsFederatedReportOutput> processMessageDetailsReport(
			MessageDetailsFederatedReportInput reportInputContext,
			List<ReportInstanceComponentDTO> reportInstanceComponentDTO, ReportContext reportContext);
}
