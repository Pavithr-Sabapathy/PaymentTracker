package com.mashreq.paymentTracker.service;

import java.util.List;

import com.mashreq.paymentTracker.dto.AdvanceSearchReportInput;
import com.mashreq.paymentTracker.dto.AdvanceSearchReportOutput;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.model.Components;

public interface UAEFTSReportService {

	List<AdvanceSearchReportOutput> processUAEFTSReport(AdvanceSearchReportInput advanceSearchReportInput, List<Components> componentList,
			ReportContext reportContext);

}
