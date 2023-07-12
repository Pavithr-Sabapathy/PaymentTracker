package com.mashreq.paymentTracker.service;

import java.util.List;

import com.mashreq.paymentTracker.dto.AdvanceSearchReportInput;
import com.mashreq.paymentTracker.dto.AdvanceSearchReportOutput;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.ReportInstanceDTO;
import com.mashreq.paymentTracker.model.Components;

public interface UAEFTSReportService {

	List<AdvanceSearchReportOutput> processAdvanceSearchReport(AdvanceSearchReportInput advanceSearchReportInput, List<Components> componentList,
			ReportContext reportContext);

	ReportExecuteResponseData processUAEFTSReport(ReportInstanceDTO reportInstanceDTO, ReportContext reportContext);

}
