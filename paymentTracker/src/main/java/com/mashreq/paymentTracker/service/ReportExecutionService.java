package com.mashreq.paymentTracker.service;

import java.util.Date;

import com.mashreq.paymentTracker.dto.ReportExecutionDTO;
import com.mashreq.paymentTracker.model.ReportExecution;
import com.mashreq.paymentTracker.type.ExecutionStatusType;

public interface ReportExecutionService {

	public ReportExecution createReportExecution(ReportExecutionDTO reportExecutionDTO);

	public void updateReportExecutionStatus(Long executionId, ExecutionStatusType status, Date endTime);

	public void updateReportExecutionTimeByExecutionId(Long executionTime, Long executionId);

}
