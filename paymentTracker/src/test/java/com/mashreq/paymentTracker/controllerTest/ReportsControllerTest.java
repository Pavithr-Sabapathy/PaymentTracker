package com.mashreq.paymentTracker.controllerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashreq.paymentTracker.TestUtils;
import com.mashreq.paymentTracker.dto.ReportDTO;
import com.mashreq.paymentTracker.dto.ReportDTORequest;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.service.ReportConfigurationService;

@SpringBootTest
@AutoConfigureMockMvc
public class ReportsControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	ReportConfigurationService reportConfigurationService;

	@Test
	public void testsaveReportConfiguration() throws Exception {
		ReportDTO mockReportsResponse = new ReportDTO();
		mockReportsResponse.setActive("y");
		mockReportsResponse.setDisplayName("Reference Number");
		mockReportsResponse.setId(1L);
		mockReportsResponse.setReportCategory("Reference");
		mockReportsResponse.setReportDescription("Search");
		mockReportsResponse.setReportName("Refernce_No");
		mockReportsResponse.setValid("N");

		when(reportConfigurationService.saveReport(any(ReportDTORequest.class))).thenReturn(mockReportsResponse);
		// execute
		MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.post("/report/save").contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON).content(TestUtils.objectToJson(mockReportsResponse)))
				.andReturn();

		// verify
		int status = result.getResponse().getStatus();
		assertEquals(HttpStatus.CREATED.value(), status);

		// verify that service method was called once
		verify(reportConfigurationService).saveReport(any(ReportDTORequest.class));
		String reportResponse = result.getResponse().getContentAsString();
		assertNotNull(reportResponse);
	}

	@Test
	public void testFetchReportByName() throws Exception {

		// Create a sample report
		Report report = new Report();
		report.setReportName("Test Report");

		Mockito.when(reportConfigurationService.fetchReportByName("Test Report")).thenReturn(report);

		// Send a GET request to the endpoint with the "Test Report" name in the URL
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/report/name/Test Report"))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		// Check
		String response = result.getResponse().getContentAsString();
		Report responseReport = new ObjectMapper().readValue(response, Report.class);
		assertEquals(report.getReportName(), responseReport.getReportName());

		// verify
		Mockito.verify(reportConfigurationService).fetchReportByName("Test Report");

	}

	@Test
	public void testFetchReportById() throws Exception {

		// Create a sample report
		Report report = new Report();
		report.setId(1L);
		report.setReportName("Test Sample Report");
		ReportDTO reportDTO = new ReportDTO(1L, "Test Sample Report", "Sample", "Mockito Test case descrption",
				"Testcase", "Y", "Y", 1L, "");
		Mockito.when(reportConfigurationService.fetchReportById(1L)).thenReturn(reportDTO);

		// Send a GET request to the endpoint with the "Test Report" name in the URL
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/report/{id}", 1L))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		// Check
		String response = result.getResponse().getContentAsString();
		ReportDTO responseReport = new ObjectMapper().readValue(response, ReportDTO.class);
		assertEquals(report.getReportName(), responseReport.getReportName());

		// verify
		Mockito.verify(reportConfigurationService).fetchReportById(1L);

	}

	@Test
	public void testFetchReportsByModuleId() throws Exception {

		// Create a sample report
		Report report = new Report();
		report.setId(1L);
		report.setReportName("Test Sample Report");
		List<ReportDTO> reportDTOList = new ArrayList<ReportDTO>();
		ReportDTO reportDTO = new ReportDTO(1L, "Test Sample Report", "Sample", "Mockito Test case descrption",
				"Testcase", "Y", "Y", 1L, "");
		reportDTOList.add(reportDTO);
		Mockito.when(reportConfigurationService.fetchReportsByModuleId(1L)).thenReturn(reportDTOList);

		// Send a GET request to the endpoint with the "Test Sample Report" name in the URL
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/report/moduleId/{moduleId}", 1L))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		TypeReference<List<ReportDTO>> mapType = new TypeReference<List<ReportDTO>>() {
		};
		// Check
		String response = result.getResponse().getContentAsString();
		List<ReportDTO> responseReport = new ObjectMapper().readValue(response, mapType);
		assertEquals(responseReport.get(0).getReportName(), "Test Sample Report");

		// verify
		Mockito.verify(reportConfigurationService).fetchReportsByModuleId(1L);

	}
	
	@Test
	public void testFetchReportsByModuleName() throws Exception {

		// Create a sample report
		Report report = new Report();
		report.setId(1L);
		report.setReportName("Test Sample Report");
		String moduleName = "sampleModule";
		
		List<ReportDTO> reportDTOList = new ArrayList<ReportDTO>();
		ReportDTO reportDTO = new ReportDTO(1L, "Test Sample Report", "Sample", "Mockito Test case descrption",
				"Testcase", "Y", "Y", 1L, "");
		reportDTOList.add(reportDTO);
		Mockito.when(reportConfigurationService.fetchReportsByModule(moduleName)).thenReturn(reportDTOList);

		// Send a GET request to the endpoint with the "sampleModule" name in the URL
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/report/moduleName/{moduleName}", moduleName))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		TypeReference<List<ReportDTO>> mapType = new TypeReference<List<ReportDTO>>() {
		};
		// Check
		String response = result.getResponse().getContentAsString();
		List<ReportDTO> responseReport = new ObjectMapper().readValue(response, mapType);
		assertEquals(responseReport.get(0).getReportName(), "Test Sample Report");

		// verify
		Mockito.verify(reportConfigurationService).fetchReportsByModule(moduleName);

	}

	@Test
	public void testdeleteReport() throws Exception {
		long reportId = 1L;
		ResultActions response = mockMvc.perform(MockMvcRequestBuilders.delete("/report/id/{reportId}", reportId));
		Mockito.verify(reportConfigurationService, times(1)).deleteReportById(eq(reportId));
	}

	@Test
	public void testupdateReport() throws Exception {
		long reportId = 1L;
		Report mockReportsResponse = new Report();
		mockReportsResponse.setActive("y");
		mockReportsResponse.setDisplayName("Reference Number");
		mockReportsResponse.setId(1L);
		mockReportsResponse.setReportCategory("Reference");
		mockReportsResponse.setReportDescription("Search");
		mockReportsResponse.setReportName("Refernce_No");
		mockReportsResponse.setValid("N");

		mockMvc.perform(
				MockMvcRequestBuilders.put("/report/{reportId}", reportId).content(asJsonString(mockReportsResponse))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isAccepted());
	}

	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}