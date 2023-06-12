package com.mashreq.paymentTracker.serviceImpl;

import java.math.BigInteger;
import java.util.*;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.dto.MetricsDTO;
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

	@Autowired
	MetricsRepository metricsRepository;

	@Autowired
	ReportConfigurationRepository reportConfigurationRepo;

	@Override
	public void saveMetrics(MetricsDTO metricsRequest) {
		// code goes here
		Optional<Report> optionalReport = reportConfigurationRepo.findById(metricsRequest.getReportId());
		
		if(optionalReport.isPresent()) {
			Metrics metric = new Metrics();
			metric.setDisplayName(metricsRequest.getDisplayName());
			metric.setMetricsOrder(metricsRequest.getMetricsOrder());
			
			BigInteger lastMetricsOrder = BigInteger.ZERO;
			Long metricOrder = metricsRepository.findMetricsOrderByReportId(metricsRequest.getReportId());
			if(metricOrder== null) {
					lastMetricsOrder = BigInteger.valueOf(1L);
			} 
			else {
				lastMetricsOrder = BigInteger.valueOf(metricOrder);
				lastMetricsOrder = lastMetricsOrder.add(BigInteger.ONE);
			}
			metric.setMetricsOrder(lastMetricsOrder);
			metric.setDisplay(metricsRequest.getDisplay());
			metric.setReport(optionalReport.get());
			metric.setEntityId(metricsRequest.getEntityId());

			metricsRepository.save(metric);
			
		}else {
			throw new ResourceNotFoundException(ApplicationConstants.METRICS_DOES_NOT_EXISTS + metricsRequest.getReportId());
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
		Optional<Report> reportOptional = reportConfigurationRepo.findById(metricsDTORequest.getReportId());
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
		// code goes here
     List<MetricsResponseDTO> metricsResponseDtoList = new ArrayList<MetricsResponseDTO>();
		
		List<Metrics> mertricsList = metricsRepository.findAll();
		
		if(mertricsList == null) {
			throw new ResourceNotFoundException(ApplicationConstants.METRICS_NOT_AVAILABLE);
		}
		
		HashMap<Report, List<Metrics>> metricsReportMap = (HashMap<Report, List<Metrics>>) mertricsList.
												stream().
												collect(Collectors.groupingBy(Metrics::getReport));
		
		metricsReportMap.forEach((report, metrics)-> {
			MetricsResponseDTO metricsResponseDto = new MetricsResponseDTO();
			
			ReportDTO reportResponseDto = new ReportDTO(report.getId(),
														report.getReportName(),
														report.getDisplayName(),
														report.getReportDescription(),
														report.getReportCategory(),
														report.getActive(),
														report.getValid(),
														report.getModuleId());
			reportResponseDto.setId(report.getId());
			
			
			List<MetricsDTO> metric = metrics.stream().map(obj -> new MetricsDTO(obj.getDisplayName(),
					                                                             obj.getMetricsOrder(),
					                                                             obj.getDisplay(),
					                                                             obj.getReport().getId(),
					                                                             obj.getEntityId()))
										                           .collect(Collectors.toList());
			metricsResponseDto.setReports(reportResponseDto);
			metricsResponseDto.setMetricsList(metric);
			metricsResponseDtoList.add(metricsResponseDto);
		});		
		
		return metricsResponseDtoList;	
		
		
		
	}

	@Override
	public List<MetricsDTO> fetchMetricsByReportId(long reportId) {
		// code goes here
		Optional<Report> reportOptional = reportConfigurationRepo.findById(reportId);
		if (reportOptional.isPresent()) {
          List<MetricsDTO> metricsDtoList = new ArrayList<MetricsDTO>();
			
			List<Metrics> metricsByReportId = metricsRepository.findMetricsByReportId(reportId);
			metricsByReportId.forEach(metric->{
				MetricsDTO metricsDto = new MetricsDTO();
				metricsDto.setDisplayName(metric.getDisplayName());
				metricsDto.setMetricsOrder(metric.getMetricsOrder());
				metricsDto.setDisplay(metric.getDisplay());
				metricsDto.setReportId(metric.getReport().getId());
				metricsDto.setEntityId(metric.getEntityId());
				metricsDtoList.add(metricsDto);
			});
			return metricsDtoList;
			
		}
		else {
			throw new ResourceNotFoundException(
					ApplicationConstants.METRICS_DOES_NOT_EXISTS + reportId);
			
		}
	}

}