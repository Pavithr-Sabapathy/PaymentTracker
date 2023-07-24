package com.mashreq.paymentTracker.serviceImpl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.dao.MetricsDAO;
import com.mashreq.paymentTracker.dao.ReportDAO;
import com.mashreq.paymentTracker.dto.MetricsRequestDTO;
import com.mashreq.paymentTracker.dto.MetricsResponse;
import com.mashreq.paymentTracker.dto.MetricsResponseDTO;
import com.mashreq.paymentTracker.dto.ReportDTO;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.Metrics;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.service.MetricsService;

@Component
public class MetricsServiceImpl implements MetricsService {

	private static final Logger log = LoggerFactory.getLogger(MetricsServiceImpl.class);
	private static final String FILENAME = "MetricsServiceImpl";

	@Autowired
	MetricsDAO metricsDAO;

	@Autowired
	ReportDAO reportDAO;

	@Autowired
	ModelMapper modelMapper;

	@Override
	public MetricsResponseDTO saveMetrics(MetricsRequestDTO metricsRequest) {
		MetricsResponseDTO metricsResponse = new MetricsResponseDTO();
		Report report = reportDAO.getReportById(metricsRequest.getReportId());

		if (null != report) {
			Metrics metric = new Metrics();
			metric.setDisplayName(metricsRequest.getDisplayName());
			metric.setMetricsOrder(metricsRequest.getMetricsOrder());
			BigInteger metricOrder = metricsDAO.findMetricsOrderByReportId(metricsRequest.getReportId());
			metric.setMetricsOrder(metricOrder != null ? metricOrder.add(BigInteger.ONE) : BigInteger.ONE);
			metric.setDisplay(metricsRequest.getDisplay());
			metric.setReport(report);
			metric.setEntityId(metricsRequest.getEntityId());
			Metrics metrics = metricsDAO.save(metric);
			if (null != metrics) {
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
		metricsDAO.deleteById(metricsId);
	}

	@Override
	public MetricsResponseDTO updateMetricsById(MetricsRequestDTO metricsDTORequest, long metricsId) {
		MetricsResponseDTO metricsResponse = new MetricsResponseDTO();
		Metrics metricsObject = new Metrics();
		Report report = reportDAO.getReportById(metricsDTORequest.getReportId());
		if (null == report) {
			log.error(FILENAME + "[updateMetricsById]" + ApplicationConstants.REPORT_DOES_NOT_EXISTS
					+ metricsDTORequest.getReportId());
			throw new ResourceNotFoundException(
					ApplicationConstants.REPORT_DOES_NOT_EXISTS + metricsDTORequest.getReportId());
		} else {
			Metrics metricsObj = metricsDAO.getMetricsById(metricsId);
			if (null == metricsObj) {
				log.error(FILENAME + "[updateMetricsById]" + ApplicationConstants.METRICS_DOES_NOT_EXISTS + metricsId);
				throw new ResourceNotFoundException(ApplicationConstants.METRICS_DOES_NOT_EXISTS + metricsId);
			}
			metricsObject.setDisplayName(metricsDTORequest.getDisplayName());
			metricsObject.setEntityId(metricsDTORequest.getEntityId());
			metricsObject.setId(metricsId);
			metricsObject.setDisplay(metricsDTORequest.getDisplay());
			metricsObject.setMetricsOrder(metricsDTORequest.getMetricsOrder());
			metricsObject.setReport(report);
			Metrics metrics = metricsDAO.update(metricsObject);
			if (null != metrics) {

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

		List<Metrics> mertricsList = metricsDAO.findAll();

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
		Report report = reportDAO.getReportById(reportId);
		if (null != report) {
			List<MetricsResponseDTO> metricsDtoList = new ArrayList<MetricsResponseDTO>();

			List<Metrics> metricsByReportId = metricsDAO.getMetricsByReportId(reportId);
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