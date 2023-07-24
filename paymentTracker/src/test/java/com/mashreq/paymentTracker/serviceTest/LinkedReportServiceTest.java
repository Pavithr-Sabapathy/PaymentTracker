package com.mashreq.paymentTracker.serviceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import com.mashreq.paymentTracker.dao.ComponentDetailsDAO;
import com.mashreq.paymentTracker.dao.ComponentsDAO;
import com.mashreq.paymentTracker.dao.LinkedReportDAO;
import com.mashreq.paymentTracker.dao.MetricsDAO;
import com.mashreq.paymentTracker.dao.ModuleDAO;
import com.mashreq.paymentTracker.dao.ReportDAO;
import com.mashreq.paymentTracker.dto.LinkedReportRequestDTO;
import com.mashreq.paymentTracker.dto.LinkedReportResponseDTO;
import com.mashreq.paymentTracker.model.ApplicationModule;
import com.mashreq.paymentTracker.model.ComponentDetails;
import com.mashreq.paymentTracker.model.Components;
import com.mashreq.paymentTracker.model.LinkedReportInfo;
import com.mashreq.paymentTracker.model.Metrics;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.serviceImpl.LinkedReportServiceImpl;

@SpringBootTest
@AutoConfigureMockMvc
public class LinkedReportServiceTest {

	@InjectMocks
	LinkedReportServiceImpl linkedReportServiceImpl;

	@Mock
	ModelMapper modelMapper;

	@Mock
	ReportDAO mockReportDAO;

	@Mock
	MetricsDAO mockMetricsDAO;

	@Mock
	LinkedReportDAO mockLinkedReportDAO;

	@Mock
	ModuleDAO moduleDAO;
	
	@Mock
	private ComponentsDAO componentsDAO;

	@Mock
	private ComponentDetailsDAO componentsDetailsDAO;
	
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
		linkedReportRequestDTO.setModuleId(1L);
		linkedReportRequestDTO.setComponentDetailId(1L);
		linkedReportRequestDTO.setComponentId(2L);
		LinkedReportInfo linkedReportModel = modelMapper.map(linkedReportRequestDTO, LinkedReportInfo.class);
		
		ApplicationModule moduleMockObject = new ApplicationModule();
		moduleMockObject.setId(1L);
		moduleMockObject.setModuleName("module1");
		moduleMockObject.setDisplayName("sampleModule");
		moduleMockObject.setModuleDescription("ModuleDesc");
		moduleMockObject.setActive("active");
		moduleMockObject.setValid("valid");
		
		Report mockReportsResponse = new Report();
		mockReportsResponse.setActive("y");
		mockReportsResponse.setDisplayName("Reference Number");
		mockReportsResponse.setId(1L);
		mockReportsResponse.setReportCategory("Reference");
		mockReportsResponse.setReportDescription("Search");
		mockReportsResponse.setReportName("Refernce_No");
		mockReportsResponse.setValid("N");

		Report linkedReportResponse = new Report();
		linkedReportResponse.setActive("y");
		linkedReportResponse.setDisplayName("LinkedReportDisplay");
		linkedReportResponse.setId(1L);
		linkedReportResponse.setReportCategory("Reference");
		linkedReportResponse.setReportDescription("Search");
		linkedReportResponse.setReportName("Refernce_No");
		linkedReportResponse.setValid("N");
		
		Components mockComponents = new Components();
		mockComponents.setActive("active");
		mockComponents.setComponentKey("test-component");
		mockComponents.setComponentName("Test Component");
		
		ComponentDetails mockComponentDetails = new ComponentDetails();
		mockComponentDetails.setComponents(mockComponents);
		mockComponentDetails.setId(1L);
		mockComponentDetails.setQuery("sample Query key");
		mockComponentDetails.setQueryKey("query");
		
		Metrics metricsMockObject = new Metrics();
		metricsMockObject.setDisplay("y");
		metricsMockObject.setDisplayName("sampleMetrics");
		metricsMockObject.setEntityId(BigInteger.ZERO);
		metricsMockObject.setId(1L);
		metricsMockObject.setMetricsOrder(BigInteger.ONE);
		metricsMockObject.setReport(mockReportsResponse);
		
		Mockito.when(moduleDAO.findById(linkedReportRequestDTO.getModuleId())).thenReturn(moduleMockObject);
		Mockito.when(mockReportDAO.getReportById(1L)).thenReturn(mockReportsResponse);
		Mockito.when(componentsDAO.findById(1L)).thenReturn(mockComponents);
		Mockito.when(componentsDetailsDAO.findById(1L)).thenReturn(mockComponentDetails);
		
		when(mockLinkedReportDAO.save(linkedReportModel)).thenReturn(linkedReportModel);
		linkedReportServiceImpl.saveOrUpdateLinkedReport(linkedReportRequestDTO);
	}

	@Test
	public void testfetchLinkedReportById() {

		long linkedReportId = 1L;
		long sourceId = 1L;
		long reportId = 1L;
		ComponentDetails componentDetails = new ComponentDetails(1L, "Select * from conf_report", "sample Query", null);

		Components components = new Components();
		components.setActive("active");
		components.setComponentKey("test-component");
		components.setComponentName("Test Component");

		Report mockReportsResponse = new Report();
		mockReportsResponse.setActive("y");
		mockReportsResponse.setDisplayName("Reference Number");
		mockReportsResponse.setId(1L);
		mockReportsResponse.setReportCategory("Reference");
		mockReportsResponse.setReportDescription("Search");
		mockReportsResponse.setReportName("Refernce_No");
		mockReportsResponse.setValid("N");

		Report linkedReportResponse = new Report();
		linkedReportResponse.setActive("y");
		linkedReportResponse.setDisplayName("LinkedReportDisplay");
		linkedReportResponse.setId(1L);
		linkedReportResponse.setReportCategory("Reference");
		linkedReportResponse.setReportDescription("Search");
		linkedReportResponse.setReportName("Refernce_No");
		linkedReportResponse.setValid("N");

		Metrics metricsMockObject = new Metrics();
		metricsMockObject.setDisplay("y");
		metricsMockObject.setDisplayName("sampleMetrics");
		metricsMockObject.setEntityId(BigInteger.ZERO);
		metricsMockObject.setId(1L);
		metricsMockObject.setMetricsOrder(BigInteger.ONE);
		metricsMockObject.setReport(mockReportsResponse);

		ApplicationModule moduleMockObject = new ApplicationModule();
		moduleMockObject.setId(1L);
		moduleMockObject.setModuleName("module1");
		moduleMockObject.setDisplayName("sampleModule");
		moduleMockObject.setModuleDescription("ModuleDesc");
		moduleMockObject.setActive("active");
		moduleMockObject.setValid("valid");
		
		LinkedReportInfo linkedReportInfo = new LinkedReportInfo();
		linkedReportInfo.setId(1L);
		linkedReportInfo.setLinkName("SampleReference");
		linkedReportInfo.setLinkDescription("Referene Detail Report");
		linkedReportInfo.setReport(mockReportsResponse);
		linkedReportInfo.setLinkedReport(linkedReportResponse);
		linkedReportInfo.setSourceMetrics(metricsMockObject);
		linkedReportInfo.setActive("y");
		linkedReportInfo.setComponentDetailId(componentDetails);
		linkedReportInfo.setComponentId(components);
		linkedReportInfo.setModule(moduleMockObject);
		
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