package com.mashreq.paymentTracker.serviceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.mashreq.paymentTracker.dto.LinkedReportRequestDTO;
import com.mashreq.paymentTracker.dto.LinkedReportResponseDTO;
import com.mashreq.paymentTracker.model.LinkedReportInfo;
import com.mashreq.paymentTracker.model.Metrics;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.repository.LinkedReportRepository;
import com.mashreq.paymentTracker.repository.MetricsRepository;
import com.mashreq.paymentTracker.repository.ReportConfigurationRepository;
import com.mashreq.paymentTracker.serviceImpl.LinkedReportServiceImpl;

@ExtendWith(MockitoExtension.class)
public class LinkedReportServiceTest {

	@InjectMocks
	LinkedReportServiceImpl linkedReportServiceImpl;

	@Mock
    ModelMapper modelMapper;
	
	@Mock
	private ReportConfigurationRepository reportConfigurationRepo;

	@Mock
	MetricsRepository mockMetricsRepository;

	@Mock
	LinkedReportRepository mockLinkedReportRepo;

	@Test
	public void testSaveOrUpdateLinkedReport() {
		LinkedReportRequestDTO linkedReportRequestDTO = new LinkedReportRequestDTO(1, "SampleReference",
				"Referene Detail Report", 1, 1, 1, "y");

		LinkedReportInfo linkedReportModel = modelMapper.map(linkedReportRequestDTO, LinkedReportInfo.class);
		when(mockLinkedReportRepo.save(linkedReportModel)).thenReturn(linkedReportModel);
		linkedReportServiceImpl.saveOrUpdateLinkedReport(linkedReportRequestDTO);
	}

	@Test
	public void testfetchLinkedReportById() {

		long linkedReportId = 1L;
		long sourceId = 1L;
		long reportId = 1L;
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

		LinkedReportInfo linkedReportInfo = new LinkedReportInfo(1, "SampleReference", "Referene Detail Report", 1, 1,
				1, "y");
		Mockito.when(mockLinkedReportRepo.findById(linkedReportId)).thenReturn(Optional.of(linkedReportInfo));
		Mockito.when(mockMetricsRepository.findById(sourceId)).thenReturn(Optional.of(metricsMockObject));
		Mockito.when(reportConfigurationRepo.findById(reportId)).thenReturn(Optional.of(mockReportsResponse));

		LinkedReportResponseDTO linkedReportResponseDTO = linkedReportServiceImpl.fetchLinkedReportById(linkedReportId);
		assertEquals(linkedReportResponseDTO.getLinkDescription(), "Referene Detail Report");
		verify(mockLinkedReportRepo, times(1)).findById(linkedReportId);
	}

}