package com.mashreq.paymentTracker.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mashreq.paymentTracker.dto.ReportQueryInfoDTO;
import com.mashreq.paymentTracker.model.ReportExecution;
import com.mashreq.paymentTracker.model.ReportQueryInfo;
import com.mashreq.paymentTracker.repository.ReportQueryInfoRepository;
import com.mashreq.paymentTracker.service.ReportQueryInfoService;

@Component
public class ReportQueryInfoServiceImpl implements ReportQueryInfoService {

	@Autowired
	ReportQueryInfoRepository reportQueryInfoRepository;
	
	@Override
	public ReportQueryInfoDTO insertReportQueryInfo(ReportQueryInfoDTO reportQueryInfoDTO) {
		ReportQueryInfo reportQueryInfo = populateReportQueryInfo(reportQueryInfoDTO);
		ReportQueryInfo reportQueryData = reportQueryInfoRepository.save(reportQueryInfo);
		if(null != reportQueryData.getId()) {
			reportQueryInfoDTO.setId(reportQueryData.getId());
		}
		return reportQueryInfoDTO;
	}

	private ReportQueryInfo populateReportQueryInfo(ReportQueryInfoDTO reportQueryInfoDTO) {

		ReportQueryInfo queryInfo = new ReportQueryInfo();
		if (null != reportQueryInfoDTO) {
			if (null != reportQueryInfoDTO.getId())
				queryInfo.setId(reportQueryInfoDTO.getId());
			if (null != reportQueryInfoDTO.getQueryExecutionTime())
				queryInfo.setQueryExecutionTime(reportQueryInfoDTO.getQueryExecutionTime());
			if (null != reportQueryInfoDTO.getExecutionId()) {
				ReportExecution reportExecution = new ReportExecution();
				reportExecution.setId(reportQueryInfoDTO.getExecutionId());
				queryInfo.setExecution(reportExecution);
			}
			if (null != reportQueryInfoDTO.getDataFound())
				queryInfo.setDataFound(reportQueryInfoDTO.getDataFound());
			if (null != reportQueryInfoDTO.getDataSourceName())
				queryInfo.setDataSourceName(reportQueryInfoDTO.getDataSourceName());
			if (null != reportQueryInfoDTO.getEndTime())
				queryInfo.setEndTime(reportQueryInfoDTO.getEndTime());
			if (null != reportQueryInfoDTO.getExecutedQuery())
				queryInfo.setExecutedQuery(reportQueryInfoDTO.getExecutedQuery());
			if (null != reportQueryInfoDTO.getFailureCause())
				queryInfo.setFailureCause(reportQueryInfoDTO.getFailureCause());
			if (null != reportQueryInfoDTO.getQueryKey())
				queryInfo.setQueryKey(reportQueryInfoDTO.getQueryKey());
			if (null != reportQueryInfoDTO.getStartTime())
				queryInfo.setStartTime(reportQueryInfoDTO.getStartTime());
		}
		return queryInfo;

	}

	@Override
	public void updateReportQueryInfo(ReportQueryInfoDTO reportQueryInfoDTO) {
		ReportQueryInfo reportQueryInfo = populateReportQueryInfo(reportQueryInfoDTO);
		ReportQueryInfo reportQueryData = reportQueryInfoRepository.save(reportQueryInfo);
		if(null != reportQueryData.getId()) {
			reportQueryInfoDTO.setId(reportQueryData.getId());
		}
		
	}

}