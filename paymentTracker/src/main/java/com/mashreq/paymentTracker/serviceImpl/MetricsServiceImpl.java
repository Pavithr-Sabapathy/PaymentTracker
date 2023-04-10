package com.mashreq.paymentTracker.serviceImpl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.dto.MetricsDTO;
import com.mashreq.paymentTracker.dto.MetricsResponseDTO;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.Metrics;
import com.mashreq.paymentTracker.model.Reports;
import com.mashreq.paymentTracker.repository.MetricsRepository;
import com.mashreq.paymentTracker.repository.ReportConfigurationRepository;
import com.mashreq.paymentTracker.service.MetricsService;

@Component
public class MetricsServiceImpl implements MetricsService {

	@Autowired
	MetricsRepository metricsRepository;

	@Autowired
	ReportConfigurationRepository reportConfigurationRepo;

	@Override
	public Metrics saveMetrics(MetricsDTO metricsRequest) {

		Metrics metricsObject = new Metrics();
		Optional<Reports> reportOptional = reportConfigurationRepo.findById(metricsRequest.getReportId());
		if (reportOptional.isEmpty()) {
			throw new ResourceNotFoundException(
					ApplicationConstants.REPORT_DOES_NOT_EXISTS + metricsRequest.getReportId());
		} else {
			Long metricsOrderId = metricsRepository.findMetricsOrderByReportId(metricsRequest.getReportId());
			metricsObject.setMetricsOrder(
					metricsOrderId != null ? BigInteger.valueOf(metricsOrderId).add(BigInteger.ONE) : BigInteger.ONE );
			metricsObject.setDisplayName(metricsRequest.getDisplayName());
			metricsObject.setEntityId(null);
			metricsObject.setDisplay(metricsRequest.getDisplay());
			metricsObject.setReport(reportOptional.get());
			return metricsRepository.save(metricsObject);
		}

	}

	@Override
	public void deleteMetricsById(long metricsId) {
		if (metricsRepository.existsById(metricsId)) {
			metricsRepository.deleteById(metricsId);
		} else {
			throw new ResourceNotFoundException(ApplicationConstants.METRICS_DOES_NOT_EXISTS + metricsId);
		}
	}

	@Override
	public void updateMetricsById(MetricsDTO metricsDTORequest, long metricsId) {
		Metrics metricsObject = new Metrics();
		Optional<Reports> reportOptional = reportConfigurationRepo.findById(metricsDTORequest.getReportId());
		if (reportOptional.isEmpty()) {
			throw new ResourceNotFoundException(
					ApplicationConstants.METRICS_DOES_NOT_EXISTS + metricsDTORequest.getReportId());
		} else {
			Optional<Metrics> metricsResponseOptional = metricsRepository.findById(metricsId);
			if (metricsResponseOptional.isEmpty()) {
				throw new ResourceNotFoundException(ApplicationConstants.METRICS_DOES_NOT_EXISTS + metricsId);
			}
			metricsObject.setDisplayName(metricsDTORequest.getDisplayName());
			metricsObject.setEntityId(metricsDTORequest.getEntityId());
			metricsObject.setId(metricsId);
			metricsObject.setMetricsOrder(metricsDTORequest.getMetricsOrder());
			metricsRepository.save(metricsObject);
		}

	}

	@Override
	public List<MetricsResponseDTO> fetchAllMetrics() {

		List<MetricsResponseDTO> metricsResponseDTOList = new ArrayList<MetricsResponseDTO>();

		List<Metrics> metricsList = metricsRepository.findAll();

		List<Long> reportId = metricsList.stream().map(Metrics::getReport).map(metrics -> metrics.getId())
				.collect(Collectors.toList());

		Map<Reports, List<Metrics>> MetricsReportMap = metricsList.stream()
				.filter(metrics -> reportId.contains(metrics.getReport().getId()))
				.collect(Collectors.groupingBy(Metrics::getReport));

		MetricsReportMap.forEach((reports, MetricsMap) -> {
			MetricsResponseDTO metricsResponseDTO = new MetricsResponseDTO();
			List<MetricsDTO> metricsDTOList = new ArrayList<MetricsDTO>();
			MetricsMap.forEach(metrics -> {
				MetricsDTO metricsDTO = new MetricsDTO();
				metricsDTO.setDisplayName(metrics.getDisplayName());
				metricsDTO.setEntityId(metrics.getEntityId());
				metricsDTO.setMetricsOrder(metrics.getMetricsOrder());
				metricsDTO.setDisplay(metrics.getDisplay());
				metricsDTO.setReportId(metrics.getReport().getId());
				metricsDTOList.add(metricsDTO);
			});
			metricsResponseDTO.setReports(reports);
			metricsResponseDTO.setMetricsList(metricsDTOList);
			metricsResponseDTOList.add(metricsResponseDTO);
		});

		return metricsResponseDTOList;

	}

	@Override
	public List<MetricsDTO> fetchMetricsByReportId(long reportId) {
		List<MetricsDTO> metricsDTOList = new ArrayList<MetricsDTO>();
		List<Metrics> metricsDTOListResponse = metricsRepository.findMetricsByReportId(reportId);
		if (!CollectionUtils.isEmpty(metricsDTOListResponse)) {
			MetricsDTO metricsDTO = new MetricsDTO();
			metricsDTOListResponse.stream().forEach(metricsResponse -> {
				metricsDTO.setDisplayName(metricsResponse.getDisplayName());
				metricsDTO.setDisplay(metricsResponse.getDisplay());
				metricsDTO.setEntityId(metricsResponse.getEntityId());
				metricsDTO.setMetricsOrder(metricsResponse.getMetricsOrder());
				metricsDTO.setReportId(metricsResponse.getReport().getId());
				metricsDTOList.add(metricsDTO);
			});
		}
		return metricsDTOList;
	}

}