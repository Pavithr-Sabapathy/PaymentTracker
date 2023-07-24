package com.mashreq.paymentTracker.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.dao.ReportDAO;
import com.mashreq.paymentTracker.dto.ReportDTO;
import com.mashreq.paymentTracker.dto.ReportDTORequest;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.service.ReportConfigurationService;
import com.mashreq.paymentTracker.utility.UtilityClass;

@Component
@Transactional
public class ReportConfigurationServiceImpl implements ReportConfigurationService {

	private static final Logger log = LoggerFactory.getLogger(ReportConfigurationServiceImpl.class);
	private static final String FILENAME = "ReportConfigurationServiceImpl";

	@Autowired
	UtilityClass utility;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	ReportDAO reportDAO;

	@Override
	public ReportDTO saveReport(ReportDTORequest reportDTORequest) {
		ReportDTO reportDTO = new ReportDTO();
		Report reportResponse = new Report();
		Report reportRequest = populateReport(reportDTORequest);
		reportResponse = reportDAO.saveReport(reportRequest);
		if (null != reportResponse.getId()) {
			reportDTO = modelMapper.map(reportResponse, ReportDTO.class);
		}
		return reportDTO;

	}

	public Report fetchReportByName(String reportName) {
		Report report = reportDAO.findByReportName(reportName);
		if (null == report) {
			log.error(FILENAME + "[fetchReportByName]" + ApplicationConstants.REPORT_DOES_NOT_EXISTS + reportName);
			throw new ResourceNotFoundException(ApplicationConstants.REPORT_DOES_NOT_EXISTS + reportName);
		}
		return report;

	}

	public ReportDTO updateReportById(ReportDTORequest reportUpdateRequest, long reportId) {
		ReportDTO reportDTO = new ReportDTO();
		Report reportObject = reportDAO.getReportById(reportId);
		if (null == reportObject) {
			log.error(FILENAME + "[updateReportById]" + ApplicationConstants.REPORT_DOES_NOT_EXISTS + reportId);
			throw new ResourceNotFoundException(ApplicationConstants.REPORT_DOES_NOT_EXISTS + reportId);
		}
		Report reportConfigurationRequest = modelMapper.map(reportUpdateRequest, Report.class);
		reportConfigurationRequest.setId(reportId);
		Report reportResponse = reportDAO.updateReport(reportConfigurationRequest);
		if (null != reportResponse.getId()) {
			reportDTO = modelMapper.map(reportResponse, ReportDTO.class);
		}
		log.info(FILENAME + "[updateReportById]" + reportId + "-->" + reportDTO.toString());
		return reportDTO;
	}

	public void deleteReportById(long reportId) {
		reportDAO.deleteReport(reportId);
	}

	@Override
	public List<ReportDTO> fetchReportsByModule(String moduleName) {
		List<ReportDTO> reportDTOList = new ArrayList<ReportDTO>();
		List<Report> reportList = reportDAO.findReportByModule(moduleName);
		if (!reportList.isEmpty()) {
			reportDTOList = reportList.stream().map(report -> modelMapper.map(report, ReportDTO.class))
					.collect(Collectors.toList());

			log.info(FILENAME + "[fetchReportsByModule] " + reportDTOList.toString());
		}
		return reportDTOList;
	}

	@Override
	public List<ReportDTO> fetchReportsByModuleId(Long moduleId) {
		List<ReportDTO> reportDTOList = new ArrayList<ReportDTO>();
		List<Report> reportList = reportDAO.findByModuleId(moduleId);
		if (!reportList.isEmpty()) {
			reportDTOList = reportList.stream().map(report -> modelMapper.map(report, ReportDTO.class))
					.collect(Collectors.toList());

			log.info(FILENAME + "[fetchReportsByModuleId] " + reportDTOList.toString());
		} else {
			log.error(FILENAME + "[fetchReportsByModuleId]" + ApplicationConstants.MODULE_DOES_NOT_EXISTS + moduleId);
			throw new ResourceNotFoundException(ApplicationConstants.MODULE_DOES_NOT_EXISTS + moduleId);
		}
		return reportDTOList;
	}

	@Override
	public ReportDTO fetchReportById(Long reportId) {
		ReportDTO reportDTO = new ReportDTO();
		Report reportObject = reportDAO.getReportById(reportId);
		if (null != reportObject) {
			reportDTO = modelMapper.map(reportObject, ReportDTO.class);
			log.info(FILENAME + "[fetchReportById] " + reportId + "--->" + reportDTO.toString());
		} else {
			log.error(FILENAME + "[fetchReportById]" + ApplicationConstants.REPORT_DOES_NOT_EXISTS + reportId);
			throw new ResourceNotFoundException(ApplicationConstants.REPORT_DOES_NOT_EXISTS + reportId);
		}
		return reportDTO;
	}

	private Report populateReport(ReportDTORequest reportDTORequest) {
		Report report = new Report();
		report.setActive(reportDTORequest.getActive());
		report.setConnectorKey(reportDTORequest.getConnectorKey());
		report.setDisplayName(reportDTORequest.getDisplayName());
		report.setReportCategory(reportDTORequest.getReportCategory());
		report.setReportDescription(reportDTORequest.getReportDescription());
		report.setReportName(reportDTORequest.getReportName());
		report.setValid(reportDTORequest.getValid());
		report.setModuleId(reportDTORequest.getModuleId());
		return report;
	}

}