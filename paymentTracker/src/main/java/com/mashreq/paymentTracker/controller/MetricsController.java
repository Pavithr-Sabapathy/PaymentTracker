package com.mashreq.paymentTracker.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.dto.MetricsRequestDTO;
import com.mashreq.paymentTracker.dto.MetricsResponse;
import com.mashreq.paymentTracker.dto.MetricsResponseDTO;
import com.mashreq.paymentTracker.service.MetricsService;

import jakarta.validation.Valid;

@RestController
@Component
@RequestMapping("/metrics")
public class MetricsController {

	private static final Logger log = LoggerFactory.getLogger(MetricsController.class);
	private static final String FILENAME = "MetricsController";

	@Autowired
	MetricsService metricsService;

	@GetMapping
	public ResponseEntity<List<MetricsResponse>> fetchAllMetrics() {
	
		List<MetricsResponse> metrics = metricsService.fetchAllMetrics();
		log.info(FILENAME + "[fetchMetrics Reponse]--->" + metrics.toString());
		return ResponseEntity.ok(metrics);
		
	}
	
	@GetMapping("report/{reportId}")
	public ResponseEntity<List<MetricsResponseDTO>> fetchMetricsByReportId(@PathVariable("reportId") long reportId) {
		
		List<MetricsResponseDTO> metricsRIdResponse = metricsService.fetchMetricsByReportId(reportId);
		return ResponseEntity.ok(metricsRIdResponse);
	}

	@PostMapping("/saveMetrics")
	public ResponseEntity<MetricsResponseDTO> saveMetrics(@Valid @RequestBody MetricsRequestDTO metricsRequest) {
		
			log.info(FILENAME + "[saveMetrics Request]--->" + metricsRequest.toString());
			MetricsResponseDTO metricsReponse = metricsService.saveMetrics(metricsRequest);
			return new ResponseEntity<MetricsResponseDTO>(metricsReponse, HttpStatus.CREATED);
	}

	@DeleteMapping("/{metricsId}")
	public ResponseEntity<String> deleteMetrics(@PathVariable long metricsId) {
		log.info(FILENAME + "[deleteMetrics Request]--->" + metricsId);
		metricsService.deleteMetricsById(metricsId);
		return new ResponseEntity<String>(ApplicationConstants.METRICS_DELETION_MSG, HttpStatus.ACCEPTED);

	}

	@PutMapping("update/{metricsId}")
	public ResponseEntity<MetricsResponseDTO> updateMetrics(@RequestBody MetricsRequestDTO metricsDTORequest,
			@PathVariable long metricsId) {
		log.info(FILENAME + "[updateMetrics Request]--->" + metricsDTORequest.toString());
		MetricsResponseDTO metricsReponse = metricsService.updateMetricsById(metricsDTORequest, metricsId);
		return new ResponseEntity<MetricsResponseDTO>(metricsReponse, HttpStatus.ACCEPTED);

	}

}