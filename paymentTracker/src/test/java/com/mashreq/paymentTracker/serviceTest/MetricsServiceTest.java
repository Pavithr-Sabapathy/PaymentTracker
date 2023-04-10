package com.mashreq.paymentTracker.serviceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.mashreq.paymentTracker.dto.MetricsDTO;
import com.mashreq.paymentTracker.dto.MetricsResponseDTO;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.Metrics;
import com.mashreq.paymentTracker.model.Reports;
import com.mashreq.paymentTracker.repository.MetricsRepository;
import com.mashreq.paymentTracker.repository.ReportConfigurationRepository;
import com.mashreq.paymentTracker.serviceImpl.MetricsServiceImpl;

@ExtendWith(MockitoExtension.class)
public class MetricsServiceTest {
	@InjectMocks
	MetricsServiceImpl metricsService;

	@Mock
	MetricsRepository mockMetricsRepository;

	@Mock
	ReportConfigurationRepository mockreportConfigurationRepo;

	@Test
	public void testsaveMetrics() {
		/*
		 * MetricsDTO mockMetricsDTO = new MetricsDTO(); Metrics metricsMockObject = new
		 * Metrics();
		 * 
		 * Reports mockReportsResponseOptional = new Reports(1L, "Advanced_Search",
		 * "Advanced Search", "Search", "Searching Category", "y", "y");
		 * 
		 * mockMetricsDTO.setDisplay("y");
		 * mockMetricsDTO.setDisplayName("SampleMetrics");
		 * mockMetricsDTO.setEntityId(BigInteger.ZERO);
		 * mockMetricsDTO.setMetricsOrder(BigInteger.ONE);
		 * mockMetricsDTO.setReportId(1L);
		 * 
		 * metricsMockObject.setDisplay("y");
		 * metricsMockObject.setDisplayName("sampleMetrics");
		 * metricsMockObject.setEntityId(BigInteger.ZERO); metricsMockObject.setId(1L);
		 * metricsMockObject.setMetricsOrder(BigInteger.ONE);
		 * metricsMockObject.setReport(mockReportsResponseOptional);
		 * 
		 * 
		 * when(mockreportConfigurationRepo.findById(1L)).thenReturn(Optional.of(
		 * mockReportsResponseOptional));
		 * when(mockMetricsRepository.findMetricsOrderByReportId(1L)).thenReturn(
		 * BigInteger.ONE);
		 * when(mockMetricsRepository.save(metricsMockObject)).thenReturn(
		 * metricsMockObject); Metrics metricsResponse =
		 * metricsService.saveMetrics(mockMetricsDTO);
		 * assertEquals(metricsResponse.getDisplayName(), "sampleMetrics Search");
		 * verify(mockMetricsRepository, times(1)).save(metricsMockObject);
		 */}

	@Test
	public void testFetchAllMetrics() throws JsonMappingException, JsonProcessingException {

		Reports mockReportsResponse = new Reports();
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
		List<MetricsResponseDTO> metricsMockResponse = metricsService.fetchAllMetrics();

		assertEquals(1, metricsMockResponse.size());
		verify(mockMetricsRepository, times(1)).findAll();
	}

	@Test
	public void testdeleteMetricsById() {
		long metricsId = 1L;

		when(mockMetricsRepository.existsById(metricsId)).thenReturn(true);
		doNothing().when(mockMetricsRepository).deleteById(metricsId);

		metricsService.deleteMetricsById(metricsId);

		verify(mockMetricsRepository).existsById(1L);
		verify(mockMetricsRepository).deleteById(1L);
	}

	@Test
	void testdeleteMetricsByIdNotExists() throws ResourceNotFoundException {
		long metricsId = 1L;

		when(mockMetricsRepository.existsById(metricsId)).thenReturn(false);

		ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class,
				() -> metricsService.deleteMetricsById(metricsId),
				"Expected dataSourceConfigService.deleteDataSourceConfigById to throw, but it didn't");
		assertNotNull(thrown);
		assertTrue(thrown.getMessage().contains("Metrics not exist with this id :"));
	}

	@Test
	public void testupdateMetricsById() throws JsonMappingException, JsonProcessingException {
		long metricsId = 1L;

		Reports mockReportsResponse = new Reports();
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

		MetricsDTO mockMetricsDTO = new MetricsDTO();
		mockMetricsDTO.setDisplay("y");
		mockMetricsDTO.setDisplayName("SampleMetrics");
		mockMetricsDTO.setEntityId(BigInteger.ZERO);
		mockMetricsDTO.setMetricsOrder(BigInteger.ONE);
		mockMetricsDTO.setReportId(1L);
		when(mockreportConfigurationRepo.findById(1L)).thenReturn(Optional.of(mockReportsResponse));
		when(mockMetricsRepository.findById(1L)).thenReturn(Optional.of(metricsMockObject));
		metricsService.updateMetricsById(mockMetricsDTO, metricsId);
		verify(mockMetricsRepository, times(1)).findById(metricsId);
	}

	@Test
	public void testupdateMetricsByIdNotExists() throws ResourceNotFoundException {
		long metricsId = 1L;
		Reports mockReportsResponse = null;

		MetricsDTO mockMetricsDTO = new MetricsDTO();
		mockMetricsDTO.setDisplay("y");
		mockMetricsDTO.setDisplayName("SampleMetrics");
		mockMetricsDTO.setEntityId(BigInteger.ZERO);
		mockMetricsDTO.setMetricsOrder(BigInteger.ONE);
		mockMetricsDTO.setReportId(1L);

		when(mockreportConfigurationRepo.findById(1L)).thenReturn(Optional.ofNullable(mockReportsResponse));
		ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class,
				() -> metricsService.updateMetricsById(mockMetricsDTO, metricsId),
				"Expected dataSourceConfigService.deleteDataSourceConfigById to throw, but it didn't");
		assertNotNull(thrown);
		assertTrue(thrown.getMessage().contains("Metrics not exist with this id :1"));
	}

}