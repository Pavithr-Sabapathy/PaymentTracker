package com.mashreq.paymentTracker.serviceImpl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.dto.MetricsRequestDTO;
import com.mashreq.paymentTracker.dto.MetricsResponse;
import com.mashreq.paymentTracker.dto.MetricsResponseDTO;
import com.mashreq.paymentTracker.dto.ReportDTO;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.Metrics;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.repository.MetricsRepository;
import com.mashreq.paymentTracker.repository.ReportConfigurationRepository;
import com.mashreq.paymentTracker.service.MetricsService;

@Component
public class MetricsServiceImpl implements MetricsService {

	private static final Logger log = LoggerFactory.getLogger(MetricsServiceImpl.class);
	private static final String FILENAME = "MetricsServiceImpl";

	@Autowired
	MetricsRepository metricsRepository;

	@Autowired
	ReportConfigurationRepository reportConfigurationRepo;
	
	@Autowired
	ModelMapper modelMapper;

	@Override
	public MetricsResponseDTO saveMetrics(MetricsRequestDTO metricsRequest) {
		MetricsResponseDTO metricsResponse = new MetricsResponseDTO();
		Optional<Report> optionalReport = reportConfigurationRepo.findById(metricsRequest.getReportId());

		if (optionalReport.isPresent()) {
			Metrics metric = new Metrics();
			metric.setDisplayName(metricsRequest.getDisplayName());
			metric.setMetricsOrder(metricsRequest.getMetricsOrder());

			BigInteger lastMetricsOrder = BigInteger.ZERO;
			Long metricOrder = metricsRepository.findMetricsOrderByReportId(metricsRequest.getReportId());
			if (metricOrder == null) {
				lastMetricsOrder = BigInteger.valueOf(1L);
			} else {
				lastMetricsOrder = BigInteger.valueOf(metricOrder);
				lastMetricsOrder = lastMetricsOrder.add(BigInteger.ONE);
			}
			metric.setMetricsOrder(lastMetricsOrder);
			metric.setDisplay(metricsRequest.getDisplay());
			metric.setReport(optionalReport.get());
			metric.setEntityId(metricsRequest.getEntityId());
			Metrics metrics = metricsRepository.save(metric);
			if(null != metrics) {
				metricsResponse.setMetricsId(metrics.getId());
				metricsResponse.setDisplay(metrics.getDisplay());
				metricsResponse.setDisplayName(metrics.getDisplayName());
				metricsResponse.setEntityId(metrics.getEntityId());
				metricsResponse.setMetricsOrder(metrics.getMetricsOrder());
				metricsResponse.setReportId(metrics.getReport().getId());
			}

		} else {
			log.error(FILENAME + "[saveMetrics]" + ApplicationConstants.REPORT_DOES_NOT_EXISTS
					+ metricsRequest.getReportId());
			throw new ResourceNotFoundException(
					ApplicationConstants.REPORT_DOES_NOT_EXISTS + metricsRequest.getReportId());
		}
		return metricsResponse;
	}

	@Override
	public void deleteMetricsById(long metricsId) {
		if (metricsRepository.existsById(metricsId)) {
			metricsRepository.deleteById(metricsId);
		} else {
			log.error(FILENAME + "[deleteMetricsById]" + ApplicationConstants.METRICS_DOES_NOT_EXISTS + metricsId);
			throw new ResourceNotFoundException(ApplicationConstants.METRICS_DOES_NOT_EXISTS + metricsId);
		}
	}

	@Override
	public MetricsResponseDTO updateMetricsById(MetricsRequestDTO metricsDTORequest, long metricsId) {
		MetricsResponseDTO metricsResponse = new MetricsResponseDTO();
		Metrics metricsObject = new Metrics();
		Optional<Report> reportOptional = reportConfigurationRepo.findById(metricsDTORequest.getReportId());
		if (reportOptional.isEmpty()) {
			log.error(FILENAME + "[updateMetricsById]" + ApplicationConstants.REPORT_DOES_NOT_EXISTS
					+ metricsDTORequest.getReportId());
			throw new ResourceNotFoundException(
					ApplicationConstants.REPORT_DOES_NOT_EXISTS + metricsDTORequest.getReportId());
		} else {
			Optional<Metrics> metricsResponseOptional = metricsRepository.findById(metricsId);
			if (metricsResponseOptional.isEmpty()) {
				log.error(FILENAME + "[updateMetricsById]" + ApplicationConstants.METRICS_DOES_NOT_EXISTS + metricsId);
				throw new ResourceNotFoundException(ApplicationConstants.METRICS_DOES_NOT_EXISTS + metricsId);
			}
			metricsObject.setDisplayName(metricsDTORequest.getDisplayName());
			metricsObject.setEntityId(metricsDTORequest.getEntityId());
			metricsObject.setId(metricsId);
			metricsObject.setDisplay(metricsDTORequest.getDisplay());
			metricsObject.setMetricsOrder(metricsDTORequest.getMetricsOrder());
			metricsObject.setReport(reportOptional.get());
			Metrics metrics = metricsRepository.save(metricsObject);
			if(null != metrics) {
				
				metricsResponse.setMetricsId(metrics.getId());
				metricsResponse.setDisplay(metrics.getDisplay());
				metricsResponse.setDisplayName(metrics.getDisplayName());
				metricsResponse.setEntityId(metrics.getEntityId());
				metricsResponse.setMetricsOrder(metrics.getMetricsOrder());
				metricsResponse.setReportId(metrics.getReport().getId());
			}
		}
		
		log.info(FILENAME + "[updateMetricsById]" + metricsResponse.toString());
		return metricsResponse;
		
	}

	@Override
	public List<MetricsResponse> fetchAllMetrics() {
		List<MetricsResponse> metricsResponseDtoList = new ArrayList<MetricsResponse>();

		List<Metrics> mertricsList = metricsRepository.findAll();

		if (mertricsList == null) {
			throw new ResourceNotFoundException(ApplicationConstants.METRICS_NOT_AVAILABLE);
		}

		HashMap<Report, List<Metrics>> metricsReportMap = (HashMap<Report, List<Metrics>>) mertricsList.stream()
				.collect(Collectors.groupingBy(Metrics::getReport));

		metricsReportMap.forEach((report, metrics) -> {
			MetricsResponse metricsResponseDto = new MetricsResponse();

			ReportDTO reportResponseDto = new ReportDTO(report.getId(), report.getReportName(), report.getDisplayName(),
					report.getReportDescription(), report.getReportCategory(), report.getActive(), report.getValid(),
					report.getModuleId(), report.getConnectorKey());
			reportResponseDto.setId(report.getId());

			List<MetricsResponseDTO> metric = metrics
					.stream().map(obj -> new MetricsResponseDTO(obj.getId(), obj.getDisplayName(),
							obj.getMetricsOrder(), obj.getDisplay(), obj.getReport().getId(), obj.getEntityId()))
					.collect(Collectors.toList());
			metricsResponseDto.setReports(reportResponseDto);
			metricsResponseDto.setMetricsList(metric);
			metricsResponseDtoList.add(metricsResponseDto);
		});

		return metricsResponseDtoList;

	}

	public List<MetricsResponseDTO> fetchMetricsByReportId(long reportId) {
		// code goes here
		Optional<Report> reportOptional = reportConfigurationRepo.findById(reportId);
		if (reportOptional.isPresent()) {
			List<MetricsResponseDTO> metricsDtoList = new ArrayList<MetricsResponseDTO>();

			List<Metrics> metricsByReportId = metricsRepository.findMetricsByReportId(reportId);
			if (!metricsByReportId.isEmpty()) {
				metricsByReportId.forEach(metric -> {
					MetricsResponseDTO metricsDto = new MetricsResponseDTO();
					metricsDto.setMetricsId(metric.getId());
					metricsDto.setDisplayName(metric.getDisplayName());
					metricsDto.setMetricsOrder(metric.getMetricsOrder());
					metricsDto.setDisplay(metric.getDisplay());
					metricsDto.setReportId(metric.getReport().getId());
					metricsDto.setEntityId(metric.getEntityId());
					metricsDtoList.add(metricsDto);
				});
			} else {
				log.error(FILENAME + "[fetchMetricsByReportId]"
						+ ApplicationConstants.METRICS_DOES_NOT_EXISTS_FOR_REPORT + reportId);
				throw new ResourceNotFoundException(ApplicationConstants.METRICS_DOES_NOT_EXISTS_FOR_REPORT + reportId);

			}
			return metricsDtoList;

		} else {
			log.error(FILENAME + "[fetchMetricsByReportId]" + ApplicationConstants.REPORT_DOES_NOT_EXISTS + reportId);
			throw new ResourceNotFoundException(ApplicationConstants.REPORT_DOES_NOT_EXISTS + reportId);

		}
	}

}