package com.mashreq.paymentTracker.serviceTest;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.dao.MetricsDAO;
import com.mashreq.paymentTracker.dao.ReportDAO;
import com.mashreq.paymentTracker.dto.MetricsRequestDTO;
import com.mashreq.paymentTracker.dto.MetricsResponse;
import com.mashreq.paymentTracker.dto.MetricsResponseDTO;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.Metrics;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.serviceImpl.MetricsServiceImpl;

@SpringBootTest
@AutoConfigureMockMvc
public class MetricsServiceTest {
	@InjectMocks
	MetricsServiceImpl metricsService;

	@Mock
	MetricsDAO mockMetricsRepository;

	@Mock
	ReportDAO mockreportConfigurationRepo;

	@Test
	public void testSaveMetrics() {
		// Create a MetricsDTO instance for the test
		MetricsRequestDTO metricsDto = new MetricsRequestDTO();
		metricsDto.setDisplayName("Test Metrics");
		metricsDto.setDisplay("y");
		metricsDto.setEntityId(BigInteger.ZERO);
		metricsDto.setMetricsOrder(BigInteger.ONE);
		metricsDto.setReportId(1L);


		Report report = new Report();
		when(mockreportConfigurationRepo.getReportById(metricsDto.getReportId())).thenReturn(report);

		when(mockMetricsRepository.findMetricsOrderByReportId(metricsDto.getReportId()))
				.thenReturn(BigInteger.valueOf(1));

		metricsService.saveMetrics(metricsDto);

		verify(mockreportConfigurationRepo).getReportById(metricsDto.getReportId());

		verify(mockMetricsRepository).findMetricsOrderByReportId(metricsDto.getReportId());

		ArgumentCaptor<Metrics> argumentCaptor = ArgumentCaptor.forClass(Metrics.class);
		verify(mockMetricsRepository).save(argumentCaptor.capture());
		Metrics savedMetrics = argumentCaptor.getValue();
		assertEquals(report, savedMetrics.getReport());
		assertEquals(metricsDto.getDisplayName(), savedMetrics.getDisplayName());
		assertEquals(metricsDto.getDisplay(), savedMetrics.getDisplay());
	}

	@Test()
	public void testSaveMetricsReportNotFound() {

		MetricsRequestDTO metricsDto = new MetricsRequestDTO();
		metricsDto.setReportId(1L);
		metricsDto.setDisplayName("Test Metrics");
		metricsDto.setDisplay("y");
		metricsDto.setEntityId(BigInteger.ZERO);
		metricsDto.setMetricsOrder(BigInteger.ONE);
		metricsDto.setReportId(1L);
		
		when(mockreportConfigurationRepo.getReportById(metricsDto.getReportId())).thenReturn(null);
		ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class,
				() -> metricsService.saveMetrics(metricsDto),
				"Expected dataSourceConfigService.deleteDataSourceConfigById to throw, but it didn't");
		assertNotNull(thrown);
		assertNotNull(thrown.getMessage().contains(ApplicationConstants.METRICS_DOES_NOT_EXISTS));

	}

	@Test
	public void testfetchMetricsByReportId() throws JsonMappingException, JsonProcessingException {
		// Setup
		Long reportId = 1L;
		Report report = new Report();
		report.setId(reportId);
		Metrics metric = new Metrics();
		metric.setDisplay("y");
		metric.setDisplayName("sampleMetrics");
		metric.setEntityId(BigInteger.ZERO);
		metric.setId(1L);
		metric.setMetricsOrder(BigInteger.ONE);
		metric.setReport(report);

		List<Metrics> metricsList = new ArrayList<>();
		metricsList.add(metric);
		Mockito.when(mockreportConfigurationRepo.getReportById(reportId)).thenReturn(report);
		Mockito.when(mockMetricsRepository.getMetricsByReportId(reportId)).thenReturn(metricsList);

		List<MetricsResponseDTO> result = metricsService.fetchMetricsByReportId(reportId);

		assertEquals(1, result.size());
		MetricsResponseDTO metricsDto = result.get(0);
		assertEquals(metric.getDisplayName(), metricsDto.getDisplayName());
		assertEquals(metric.getMetricsOrder(), metricsDto.getMetricsOrder());
		assertEquals(metric.getDisplay(), metricsDto.getDisplay());
		assertEquals(metric.getReport().getId(), metricsDto.getReportId());
		assertEquals(metric.getEntityId(), metricsDto.getEntityId());

	}

	@Test
	public void testFetchMetricsByReportIdNotExist() {
		// Setup
		Long reportId = 1L;
		Report mockReportsResponse = new Report();
		mockReportsResponse.setActive("y");
		mockReportsResponse.setDisplayName("Reference Number");
		mockReportsResponse.setId(1L);
		mockReportsResponse.setReportCategory("Reference");
		mockReportsResponse.setReportDescription("Search");
		mockReportsResponse.setReportName("Refernce_No");
		mockReportsResponse.setValid("N");
		Mockito.when(mockreportConfigurationRepo.getReportById(reportId)).thenReturn(null);
		ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class,
				() -> metricsService.fetchMetricsByReportId(reportId),
				"Expected dataSourceConfigService.deleteDataSourceConfigById to throw, but it didn't");
		assertNotNull(thrown);
		assertNotNull(thrown.getMessage().contains(ApplicationConstants.METRICS_DOES_NOT_EXISTS));

	}

	@Test
	public void testFetchAllMetrics() throws JsonMappingException, JsonProcessingException {

		Report mockReportsResponse = new Report();
		mockReportsResponse.setActive("y");
		mockReportsResponse.setDisplayName("Reference Number");
		mockReportsResponse.setId(1L);
		mockReportsResponse.setReportCategory("Reference");
		mockReportsResponse.setReportDescription("Search");
		mockReportsResponse.setReportName("Refernce_No");
		mockReportsResponse.setValid("N");

		Metrics metricsMockObject = new Metrics();
		metricsMockObject.setDisplay("y");
		metricsMockObject.setDisplayName("sampleMetrics");
		metricsMockObject.setEntityId(BigInteger.ZERO);
		metricsMockObject.setId(1L);
		metricsMockObject.setMetricsOrder(BigInteger.ONE);
		metricsMockObject.setReport(mockReportsResponse);
		List<Metrics> metricsMockList = Arrays.asList(metricsMockObject);

		when(mockMetricsRepository.findAll()).thenReturn(metricsMockList);

		// test
		List<MetricsResponse> metricsMockResponse = metricsService.fetchAllMetrics();

		assertEquals(1, metricsMockResponse.size());
		verify(mockMetricsRepository, times(1)).findAll();
	}

	@Test
	public void testdeleteMetricsById() {
		long metricsId = 1L;

		doNothing().when(mockMetricsRepository).deleteById(metricsId);

		metricsService.deleteMetricsById(metricsId);

		verify(mockMetricsRepository).deleteById(1L);
	}

	
	@Test
	public void testupdateMetricsById() throws JsonMappingException, JsonProcessingException {
		long metricsId = 1L;

		Report mockReportsResponse = new Report();
		mockReportsResponse.setActive("y");
		mockReportsResponse.setDisplayName("Reference Number");
		mockReportsResponse.setId(1L);
		mockReportsResponse.setReportCategory("Reference");
		mockReportsResponse.setReportDescription("Search");
		mockReportsResponse.setReportName("Refernce_No");
		mockReportsResponse.setValid("N");

		Metrics metricsMockObject = new Metrics();
		metricsMockObject.setDisplay("y");
		metricsMockObject.setDisplayName("sampleMetrics");
		metricsMockObject.setEntityId(BigInteger.ZERO);
		metricsMockObject.setId(1L);
		metricsMockObject.setMetricsOrder(BigInteger.ONE);
		metricsMockObject.setReport(mockReportsResponse);

		MetricsRequestDTO mockMetricsDTO = new MetricsRequestDTO();
		mockMetricsDTO.setDisplay("y");
		mockMetricsDTO.setDisplayName("SampleMetrics");
		mockMetricsDTO.setEntityId(BigInteger.ZERO);
		mockMetricsDTO.setMetricsOrder(BigInteger.ONE);
		mockMetricsDTO.setReportId(1L);
		when(mockreportConfigurationRepo.getReportById(1L)).thenReturn(mockReportsResponse);
		when(mockMetricsRepository.getMetricsById(1L)).thenReturn(metricsMockObject);
		metricsService.updateMetricsById(mockMetricsDTO, metricsId);
		verify(mockMetricsRepository, times(1)).getMetricsById(metricsId);
	}

	@Test
	public void testupdateMetricsByIdNotExists() throws ResourceNotFoundException {
		long metricsId = 1L;
		MetricsRequestDTO mockMetricsDTO = new MetricsRequestDTO();
		mockMetricsDTO.setDisplay("y");
		mockMetricsDTO.setDisplayName("SampleMetrics");
		mockMetricsDTO.setEntityId(BigInteger.ZERO);
		mockMetricsDTO.setMetricsOrder(BigInteger.ONE);
		mockMetricsDTO.setReportId(1L);

		when(mockreportConfigurationRepo.getReportById(1L)).thenReturn(null);
		ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class,
				() -> metricsService.updateMetricsById(mockMetricsDTO, metricsId),
				"Expected dataSourceConfigService.deleteDataSourceConfigById to throw, but it didn't");
		assertNotNull(thrown);
		assertNotNull(thrown.getMessage().contains("Metrics not exist with this id :1"));
	}

}