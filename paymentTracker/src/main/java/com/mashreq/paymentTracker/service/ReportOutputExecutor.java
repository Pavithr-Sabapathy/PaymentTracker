package com.mashreq.paymentTracker.service;

import java.util.List;
import java.util.Map;

import com.mashreq.paymentTracker.dto.ReportExecuteResponseColumnDefDTO;
import com.mashreq.paymentTracker.dto.ReportOutput;
import com.mashreq.paymentTracker.model.Report;

public interface ReportOutputExecutor {
	List<ReportExecuteResponseColumnDefDTO> populateColumnDef(Report reportObject);

	List<Map<String, Object>> populateRowData(List<ReportOutput> flexReportExecuteResponseList, Report report);

	List<String> prepareLinkReportInfo(Report reportObject);
}
