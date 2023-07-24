package com.mashreq.paymentTracker.serviceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.mashreq.paymentTracker.dao.LinkedReportDAO;
import com.mashreq.paymentTracker.dao.MetricsDAO;
import com.mashreq.paymentTracker.dao.ReportDAO;
import com.mashreq.paymentTracker.dto.LinkedReportRequestDTO;
import com.mashreq.paymentTracker.dto.LinkedReportResponseDTO;
import com.mashreq.paymentTracker.model.LinkedReportInfo;
import com.mashreq.paymentTracker.model.Metrics;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.serviceImpl.LinkedReportServiceImpl;

@ExtendWith(MockitoExtension.class)
public class LinkedReportServiceTest {

	@InjectMocks
	LinkedReportServiceImpl linkedReportServiceImpl;

	@Mock
	ModelMapper modelMapper;

	@MockBean
	ReportDAO mockReportDAO;

	@Mock
	MetricsDAO mockMetricsDAO;

	@Mock
	LinkedReportDAO mockLinkedReportDAO;

	@Test
	public void testSaveOrUpdateLinkedReport() {
		LinkedReportRequestDTO linkedReportRequestDTO = new LinkedReportRequestDTO();
		linkedReportRequestDTO.setId(1L);
		linkedReportRequestDTO.setLinkName("SampleReference");
		linkedReportRequestDTO.setLinkDescription("Referene Detail Report");
		linkedReportRequestDTO.setLinkedReportId(1L);
		linkedReportRequestDTO.setReportId(1L);
		linkedReportRequestDTO.setSourceMetricId(1L);
		linkedReportRequestDTO.setActive("Y");

		LinkedReportInfo linkedReportModel = modelMapper.map(linkedReportRequestDTO, LinkedReportInfo.class);
		when(mockLinkedReportDAO.save(linkedReportModel)).thenReturn(linkedReportModel);
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

		LinkedReportInfo linkedReportInfo = new LinkedReportInfo();
		linkedReportInfo.setId(1L);
		linkedReportInfo.setLinkName("SampleReference");
		linkedReportInfo.setLinkDescription("Referene Detail Report");
		/*
		 * linkedReportInfo.setReport(1L); linkedReportInfo.setLinkedReportId(1L);
		 */
		linkedReportInfo.setActive("y");

		Mockito.when(mockLinkedReportDAO.findById(linkedReportId)).thenReturn(linkedReportInfo);
		Mockito.when(mockMetricsDAO.getMetricsById(sourceId)).thenReturn(metricsMockObject);
		Mockito.when(mockReportDAO.getReportById(reportId)).thenReturn(mockReportsResponse);

		LinkedReportResponseDTO linkedReportResponseDTO = linkedReportServiceImpl.fetchLinkedReportById(linkedReportId);
		assertEquals(linkedReportResponseDTO.getLinkDescription(), "Referene Detail Report");
		verify(mockLinkedReportDAO, times(1)).findById(linkedReportId);
	}

	@Test
	public void testdeletelinkedReportById() {
		long linkedReportId = 1L;

		doNothing().when(mockLinkedReportDAO).deleteById(linkedReportId);

		linkedReportServiceImpl.deletelinkedReportById(linkedReportId);

		verify(mockLinkedReportDAO).deleteById(1L);
	}

}