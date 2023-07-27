package com.mashreq.paymentTracker.serviceImpl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mashreq.paymentTracker.dao.ReportDataDAO;
import com.mashreq.paymentTracker.dao.ReportExecutionDAO;
import com.mashreq.paymentTracker.dto.ReportExecutionDTO;
import com.mashreq.paymentTracker.model.ApplicationModule;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.model.ReportData;
import com.mashreq.paymentTracker.model.ReportExecution;
import com.mashreq.paymentTracker.model.ReportInstance;
import com.mashreq.paymentTracker.model.Roles;
import com.mashreq.paymentTracker.model.Users;
import com.mashreq.paymentTracker.service.ReportExecutionService;
import com.mashreq.paymentTracker.type.ExecutionStatusType;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ReportExecutionServiceImpl implements ReportExecutionService {

	private static final Logger log = LoggerFactory.getLogger(ReportExecutionServiceImpl.class);
	private static final String FILENAME = "ReportExecutionServiceImpl";

	@Autowired
	ReportExecutionDAO reportExecutionDAO;

	@Autowired
	ReportDataDAO reportDataDAO;

	@Override
	public ReportExecution createReportExecution(ReportExecutionDTO reportExecutionDTO) {
		log.info(FILENAME + " inserting report excution with values " + reportExecutionDTO.toString());
		ReportExecution reportExecution = populateReportExecution(reportExecutionDTO);
		ReportExecution reportExecutionResponse = reportExecutionDAO.save(reportExecution);
		reportExecution.setId(reportExecutionResponse.getId());
		return reportExecution;
	}

	private ReportExecution populateReportExecution(ReportExecutionDTO reportExecutionDTO) {
		ReportExecution reportExecution = new ReportExecution();
		if (null != reportExecutionDTO.getId())
			reportExecution.setId(reportExecutionDTO.getId());
		if (null != reportExecutionDTO.getReportId()) {
			Report report = new Report();
			report.setId(reportExecutionDTO.getReportId());
			reportExecution.setReport(report);
		}
		if (null != reportExecutionDTO.getReportInstanceId()) {
			ReportInstance reportInstance = new ReportInstance();
			reportInstance.setId(reportExecutionDTO.getReportInstanceId());
			reportExecution.setReportInstance(reportInstance);
		}
		if (null != reportExecutionDTO.getModuleId()) {
			ApplicationModule module = new ApplicationModule();
			module.setId(reportExecutionDTO.getModuleId());
			reportExecution.setModule(module);
		}
		if (null != reportExecutionDTO.getUserId()) {
			Users user = new Users();
			user.setId(reportExecutionDTO.getUserId());
			user.setFirstName(reportExecutionDTO.getUserName());
			user.setFullName(reportExecutionDTO.getUserName());
			reportExecution.setUser(user);
		}
		if (null != reportExecutionDTO.getRoleId()) {
			Roles roles = new Roles();
			roles.setId(reportExecutionDTO.getRoleId());
			roles.setRoleName(reportExecutionDTO.getRoleName());
			reportExecution.setRole(roles);
		}
		if (null != reportExecutionDTO.getStatus())
			reportExecution.setExecutionStatus(reportExecutionDTO.getStatus().getValue());

		if (null != reportExecutionDTO.getLinkExecution() && reportExecutionDTO.getLinkExecution() == true)
			reportExecution.setLinkExecution("T");
		else
			reportExecution.setLinkExecution("F");
		if (null != reportExecutionDTO.getStartDate())
			reportExecution.setStartDate(reportExecutionDTO.getStartDate());
		if (null != reportExecutionDTO.getEndDate())
			reportExecution.setEndDate(reportExecutionDTO.getEndDate());
		if (null != reportExecutionDTO.getFailureCase())
			reportExecution.setFailureCause(reportExecutionDTO.getFailureCase());
		if (null != reportExecutionDTO.getUserName())
			reportExecution.setUserName(reportExecutionDTO.getUserName());
		if (null != reportExecutionDTO.getRoleName())
			reportExecution.setRoleName(reportExecutionDTO.getRoleName());
		return reportExecution;
	}

	@Override
	public void updateReportExecutionStatus(Long executionId, ExecutionStatusType status, Date endDate) {
		reportExecutionDAO.updateStatusById(executionId, status.getValue(), endDate);
	}

	@Override
	public void updateReportExecutionTimeByExecutionId(Long executionTime, Long executionId) {
		reportExecutionDAO.updateReportExecutionTimeByExecutionId(executionTime, executionId);
	}

	@Override
	public void saveReportData(ReportData reportData) {
		reportDataDAO.save(reportData);
	}

}