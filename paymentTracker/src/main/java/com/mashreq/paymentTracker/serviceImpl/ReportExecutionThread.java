package com.mashreq.paymentTracker.serviceImpl;

import java.util.Date;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.ReportExecutionDTO;
import com.mashreq.paymentTracker.exception.ReportException;
import com.mashreq.paymentTracker.service.LinkReportService;
import com.mashreq.paymentTracker.service.ReportControllerService;
import com.mashreq.paymentTracker.service.ReportExecutionService;
import com.mashreq.paymentTracker.type.ExecutionStatusType;

@Qualifier("reportExecutionThread")
@Component
@Scope("prototype")
public class ReportExecutionThread implements Callable<ReportExecuteResponseData> {

	@Autowired
	private ApplicationContext context;

	@Autowired
	ReportExecutionService reportExecutionService;

	@Autowired
	LinkReportService linkReportService;

	private ReportContext reportContext;

	public ReportExecutionThread(ReportContext reportContext) {
		this.reportContext = reportContext;
	}

	private static final Logger log = LoggerFactory.getLogger(ReportExecutionThread.class);

	@Override
	public ReportExecuteResponseData call() throws ReportException, DataAccessException {

		// Create report execution instance
		ReportExecutionDTO reportExecutionDTO = populateReportExecution();
		reportExecutionService.createReportExecution(reportExecutionDTO);
		reportContext.setExecutionId(reportExecutionDTO.getId());
		ReportControllerService reportController = (ReportControllerService) context
				.getBean(reportContext.getReportName());
		ReportExecuteResponseData reportExecuteResponseData = (ReportExecuteResponseData) reportController
				.executeReport(reportContext);
		Date endTime = new Date();
		reportExecutionService.updateReportExecutionStatus(reportExecutionDTO.getId(), ExecutionStatusType.COMPLETED, endTime);
		log.info(reportContext.getReportName()+"Data:  "+ reportExecuteResponseData.toString());
		return reportExecuteResponseData;
	}

	private ReportExecutionDTO populateReportExecution() {
		ReportExecutionDTO execution = new ReportExecutionDTO();
		execution.setReportId(reportContext.getReportId());
		execution.setLinkExecution(reportContext.isLinkedReport());
		execution.setModuleId(reportContext.getModuleId());
		execution.setReportInstanceId(reportContext.getReportInstance().getId());
		execution.setRoleId(reportContext.getRoleId());
		execution.setStartDate(new Date());
		execution.setStatus(ExecutionStatusType.INPROGRESS);
		execution.setUserId(reportContext.getUserId());
		execution.setUserName(reportContext.getUserName());
		execution.setRoleName(reportContext.getRoleName());
		execution.setEndDate(new Date());
		return execution;
	}

}