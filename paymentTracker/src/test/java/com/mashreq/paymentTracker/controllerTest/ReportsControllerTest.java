package com.mashreq.paymentTracker.controllerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashreq.paymentTracker.TestUtils;
import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.controller.ReportsController;
import com.mashreq.paymentTracker.dto.ReportDTO;
import com.mashreq.paymentTracker.dto.ReportDTORequest;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.service.ReportConfigurationService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ReportsController.class)
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

		when(reportConfigurationService.saveReport(any(ReportDTORequest.class)))
				.thenReturn(mockReportsResponse);
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
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();

		// Check
		String response = result.getResponse().getContentAsString();
		Report responseReport = new ObjectMapper().readValue(response, Report.class);
		assertEquals(report.getReportName(), responseReport.getReportName());

		// verify
		Mockito.verify(reportConfigurationService).fetchReportByName("Test Report");

	}

	@Test
	public void testdeleteReport() throws Exception {
		long reportId = 1L;
		mockMvc.perform(MockMvcRequestBuilders.delete("/report/{reportId}", reportId))
				.andExpect(status().isAccepted());

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

		mockMvc.perform(MockMvcRequestBuilders.put("/report/{reportId}", reportId)
				.content(asJsonString(mockReportsResponse))
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