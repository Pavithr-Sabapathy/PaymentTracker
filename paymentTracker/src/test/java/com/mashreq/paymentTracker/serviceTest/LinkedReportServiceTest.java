package com.mashreq.paymentTracker.serviceTest;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
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

	LinkedReportRequestDTO linkedReportRequestDTO = new LinkedReportRequestDTO();
	ApplicationModule moduleMockObject = new ApplicationModule();
	Report mockReportsResponse = new Report();
	Components mockComponents = new Components();
	ComponentDetails mockComponentDetails = new ComponentDetails();
	LinkedReportInfo mocklinkedReportInfo = new LinkedReportInfo();
	Metrics metricsMockObject = new Metrics();

	@BeforeEach
	public void setUp() {

		linkedReportRequestDTO.setLinkName("SampleReference");
		linkedReportRequestDTO.setLinkDescription("Referene Detail Report");
		linkedReportRequestDTO.setLinkedReportId(1L);
		linkedReportRequestDTO.setReportId(1L);
		linkedReportRequestDTO.setSourceMetricId(1L);
		linkedReportRequestDTO.setActive("Y");
		linkedReportRequestDTO.setModuleId(1L);
		linkedReportRequestDTO.setComponentDetailId(1L);
		linkedReportRequestDTO.setComponentId(2L);

		moduleMockObject.setId(1L);
		moduleMockObject.setModuleName("module1");
		moduleMockObject.setDisplayName("sampleModule");
		moduleMockObject.setModuleDescription("ModuleDesc");
		moduleMockObject.setActive("active");
		moduleMockObject.setValid("valid");

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

		mockComponents.setActive("active");
		mockComponents.setComponentKey("test-component");
		mockComponents.setComponentName("Test Component");

		mockComponentDetails.setComponents(mockComponents);
		mockComponentDetails.setId(1L);
		mockComponentDetails.setQuery("sample Query key");
		mockComponentDetails.setQueryKey("query");

		metricsMockObject.setDisplay("y");
		metricsMockObject.setDisplayName("sampleMetrics");
		metricsMockObject.setEntityId(BigInteger.ZERO);
		metricsMockObject.setId(1L);
		metricsMockObject.setMetricsOrder(BigInteger.ONE);
		metricsMockObject.setReport(mockReportsResponse);

		mocklinkedReportInfo.setId(1L);
		mocklinkedReportInfo.setLinkName("SampleReference");
		mocklinkedReportInfo.setLinkDescription("Referene Detail Report");
		mocklinkedReportInfo.setReport(mockReportsResponse);
		mocklinkedReportInfo.setLinkedReport(linkedReportResponse);
		mocklinkedReportInfo.setSourceMetrics(metricsMockObject);
		mocklinkedReportInfo.setActive("y");
		mocklinkedReportInfo.setComponentDetailId(mockComponentDetails);
		mocklinkedReportInfo.setComponentId(mockComponents);
		mocklinkedReportInfo.setModule(moduleMockObject);
	}

	@Test
	public void testUpdateLinkedReport() {
		linkedReportRequestDTO.setId(1L);
		LinkedReportInfo linkedReportModel = modelMapper.map(linkedReportRequestDTO, LinkedReportInfo.class);
		Mockito.when(moduleDAO.findById(linkedReportRequestDTO.getModuleId())).thenReturn(moduleMockObject);
		Mockito.when(mockReportDAO.getReportById(1L)).thenReturn(mockReportsResponse);
		Mockito.when(componentsDAO.findById(1L)).thenReturn(mockComponents);
		Mockito.when(componentsDetailsDAO.findById(1L)).thenReturn(mockComponentDetails);

		when(mockLinkedReportDAO.update(linkedReportModel)).thenReturn(linkedReportModel);
		linkedReportServiceImpl.saveOrUpdateLinkedReport(linkedReportRequestDTO);
	}

	@Test
	public void testSaveLinkedReport() {
		LinkedReportInfo linkedReportModel = modelMapper.map(linkedReportRequestDTO, LinkedReportInfo.class);

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

		Mockito.when(mockLinkedReportDAO.findById(linkedReportId)).thenReturn(mocklinkedReportInfo);
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

	@Test
	void testFetchLinkedReportByReportId() {
		List<LinkedReportInfo> mockList = new ArrayList<LinkedReportInfo>();
		mockList.add(mocklinkedReportInfo);
		long reportId = 1;
		Mockito.when(mockLinkedReportDAO.findAllByReportId(reportId)).thenReturn(mockList);
		List<LinkedReportResponseDTO> linkedReportResponseDTO = linkedReportServiceImpl
				.fetchLinkedReportByReportId(reportId);
		assertNotNull(linkedReportResponseDTO);
		verify(mockLinkedReportDAO, times(1)).findAllByReportId(reportId);
	}

	@Test
	void testFetchLinkedReportByModuleId() {
		long moduleId = 1;
		List<LinkedReportInfo> mockList = new ArrayList<LinkedReportInfo>();
		mockList.add(mocklinkedReportInfo);
		Mockito.when(mockLinkedReportDAO.findByAllModuleId(moduleId)).thenReturn(mockList);
		List<LinkedReportResponseDTO> linkedReportResponseDTO = linkedReportServiceImpl
				.fetchLinkedReportByModuleId(moduleId);
		assertNotNull(linkedReportResponseDTO);
		verify(mockLinkedReportDAO, times(1)).findByAllModuleId(moduleId);
	}
}