package com.mashreq.paymentTracker.serviceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.dto.MetricsDTO;
import com.mashreq.paymentTracker.dto.PromptDTO;
import com.mashreq.paymentTracker.dto.ReportDTO;
import com.mashreq.paymentTracker.dto.ReportDTORequest;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.Metrics;
import com.mashreq.paymentTracker.model.Prompts;
import com.mashreq.paymentTracker.model.Reports;
import com.mashreq.paymentTracker.repository.ReportConfigurationRepository;
import com.mashreq.paymentTracker.service.ReportConfigurationService;
import com.mashreq.paymentTracker.utility.UtilityClass;

@Component
public class ReportConfigurationServiceImpl implements ReportConfigurationService {

	@Autowired
	ReportConfigurationRepository reportConfigurationRepo;

	@Autowired
	UtilityClass utility;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	public Reports saveReportConfiguration(ReportDTORequest reportDTORequest) {
		Reports reportConfigurationRequest = modelMapper.map(reportDTORequest, Reports.class);
		Reports reportsConfigurationResponse = reportConfigurationRepo.save(reportConfigurationRequest);
		return reportsConfigurationResponse;

	}

	@Override
	public List<Reports> fetchAllReports() {
		List<Reports> reportsList = reportConfigurationRepo.findAll();
		return reportsList;
	}
	
	@Override
	public List<Reports> fetchAllReportsByName(String reportName) {
		List<Reports> reportsList = reportConfigurationRepo.findByReportName(reportName);
		return reportsList;
	}

	@Override
	public void updateReportById(ReportDTORequest reportUpdateRequest, long reportId) {
		Optional<Reports> reportReponseOptional = reportConfigurationRepo.findById(reportId);
		if (reportReponseOptional.isEmpty()) {
			throw new ResourceNotFoundException(ApplicationConstants.REPORT_DOES_NOT_EXISTS + reportId);
		}
		Reports reportConfigurationRequest = modelMapper.map(reportUpdateRequest, Reports.class);
		reportConfigurationRequest.setId(reportId);
		reportConfigurationRepo.save(reportConfigurationRequest);

	}

	@Override
	public void deleteReportById(long reportId) {
		if (reportConfigurationRepo.existsById(reportId)) {
			reportConfigurationRepo.deleteById(reportId);
		} else {
			throw new ResourceNotFoundException(ApplicationConstants.REPORT_DOES_NOT_EXISTS + reportId);
		}

	}

	@Override
	public List<Reports> fetchReportsAsExcel() {
		List<Reports> reportsList = reportConfigurationRepo.findAll();

		List<ReportDTO> reportDTOList = new ArrayList<ReportDTO>();
		List<PromptDTO> promptDTOList = new ArrayList<PromptDTO>();
		List<MetricsDTO> metricDTOList = new ArrayList<MetricsDTO>();

		for (Reports report : reportsList) {

			ReportDTO reportDTO = new ReportDTO();

			reportDTO.setReportName(report.getReportName());
			reportDTO.setReportDescription(report.getReportDescription());
			reportDTO.setReportCategory(report.getReportCategory());
			reportDTO.setDisplayName(report.getDisplayName());
			reportDTO.setActive(report.getActive());
			reportDTO.setValid(report.getValid());
			reportDTO.setModuleId(report.getModuleId());
			reportDTOList.add(reportDTO);
			/** prompts **/
			List<Prompts> promptList = report.getPromptList();
			List<PromptDTO> promptsDTO = promptList.stream().map(Prompt -> modelMapper.map(Prompt, PromptDTO.class))
					.collect(Collectors.toList());
			promptDTOList.addAll(promptsDTO);

			List<Metrics> metricsList = report.getMetricsList();
			List<MetricsDTO> metricsDTO = metricsList.stream().map(metric -> modelMapper.map(metric, MetricsDTO.class))
					.collect(Collectors.toList());
			metricDTOList.addAll(metricsDTO);
		}

		String excelFileName = "ReportsExcel";

		Map<String, List<?>> sheetRowDataList = new HashMap<String, List<?>>();
		sheetRowDataList.put("reports", reportDTOList);
		sheetRowDataList.put("prompts", promptDTOList);
		sheetRowDataList.put("Metrics", metricDTOList);
		try {
			UtilityClass.writeDataSheetWise(excelFileName, sheetRowDataList);
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return reportsList;

	}
}