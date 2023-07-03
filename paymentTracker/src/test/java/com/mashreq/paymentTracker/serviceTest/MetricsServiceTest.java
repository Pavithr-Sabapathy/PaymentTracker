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
import java.util.*;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.dto.MetricsRequestDTO;
import com.mashreq.paymentTracker.dto.MetricsResponse;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.Metrics;
import com.mashreq.paymentTracker.model.Report;
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
	    public void testSaveMetrics() {
	        // Create a MetricsDTO instance for the test
	        MetricsRequestDTO metricsDto = new MetricsRequestDTO();
	        metricsDto.setDisplayName("Test Metrics");
	        metricsDto.setDisplay("y");
	        metricsDto.setEntityId(BigInteger.ZERO);
	        metricsDto.setMetricsOrder(BigInteger.ONE);
	        metricsDto.setReportId(1L);

	        // Configure the reportConfigurationRepo.findById() 

	        Report report = new Report();
	        when(mockreportConfigurationRepo.findById(metricsDto.getReportId())).thenReturn(Optional.of(report));

	        // Configure the metricsRepository.findMetricsOrderByReportId()ss
	        when(mockMetricsRepository.findMetricsOrderByReportId(metricsDto.getReportId())).thenReturn(metricsDto.getReportId());

	        // Call the metricsService.saveMetrics() method 
	        metricsService.saveMetrics(metricsDto);

	        // Verify 
	        verify(mockreportConfigurationRepo).findById(metricsDto.getReportId());

	        // Verify 
	        verify(mockMetricsRepository).findMetricsOrderByReportId(metricsDto.getReportId());

	        // Verify that the metricsRepository.save() method was called with the correct Metrics instance
	        ArgumentCaptor<Metrics> argumentCaptor = ArgumentCaptor.forClass(Metrics.class);
	        verify(mockMetricsRepository).save(argumentCaptor.capture());
	        Metrics savedMetrics = argumentCaptor.getValue();
	        assertEquals(report, savedMetrics.getReport());
	        assertEquals(metricsDto.getDisplayName(), savedMetrics.getDisplayName());
	        assertEquals(metricsDto.getDisplay(), savedMetrics.getDisplay());
	    }

		@Test()
	    public void testSaveMetricsReportNotFound() {
			
	        // Create a MetricsDTO instance for the test
	        MetricsRequestDTO metricsDto = new MetricsRequestDTO();
	        metricsDto.setReportId(1L);
	        metricsDto.setDisplayName("Test Metrics");
	        metricsDto.setDisplay("y");
	        metricsDto.setEntityId(BigInteger.ZERO);
	        metricsDto.setMetricsOrder(BigInteger.ONE);
	        metricsDto.setReportId(1L);
	        // Configure the reportConfigurationRepo.findById() method 
	        when(mockreportConfigurationRepo.findById(metricsDto.getReportId())).thenReturn(Optional.empty());
	        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class,
					() -> metricsService.saveMetrics(metricsDto),
					"Expected dataSourceConfigService.deleteDataSourceConfigById to throw, but it didn't");
			assertNotNull(thrown);
			assertTrue(thrown.getMessage().contains(ApplicationConstants.METRICS_DOES_NOT_EXISTS));
		
	        
	    }
		@Test
		public void testfetchMetricsByReportId() throws JsonMappingException, JsonProcessingException{
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
	        Mockito.when(mockreportConfigurationRepo.findById(reportId)).thenReturn(Optional.of(report));
	        Mockito.when(mockMetricsRepository.findMetricsByReportId(reportId)).thenReturn(metricsList);

	        // Execution
	        List<MetricsRequestDTO> result = metricsService.fetchMetricsByReportId(reportId);

	        // Verification
	        assertEquals(1, result.size());
	        MetricsRequestDTO metricsDto = result.get(0);
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
	        Mockito.when(mockreportConfigurationRepo.findById(reportId)).thenReturn(Optional.empty());
	        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class,
					() -> metricsService.fetchMetricsByReportId(reportId),
					"Expected dataSourceConfigService.deleteDataSourceConfigById to throw, but it didn't");
			assertNotNull(thrown);
			assertTrue(thrown.getMessage().contains(ApplicationConstants.METRICS_DOES_NOT_EXISTS));
		
	        
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
		when(mockreportConfigurationRepo.findById(1L)).thenReturn(Optional.of(mockReportsResponse));
		when(mockMetricsRepository.findById(1L)).thenReturn(Optional.of(metricsMockObject));
		metricsService.updateMetricsById(mockMetricsDTO, metricsId);
		verify(mockMetricsRepository, times(1)).findById(metricsId);
	}

	@Test
	public void testupdateMetricsByIdNotExists() throws ResourceNotFoundException {
		long metricsId = 1L;
		Report mockReportsResponse = null;

		MetricsRequestDTO mockMetricsDTO = new MetricsRequestDTO();
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