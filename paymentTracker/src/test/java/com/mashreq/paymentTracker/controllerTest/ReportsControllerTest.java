package com.mashreq.paymentTracker.controllerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matchers;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashreq.paymentTracker.TestUtils;
import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.controller.ReportsController;
import com.mashreq.paymentTracker.dto.ReportDTORequest;
import com.mashreq.paymentTracker.model.Reports;
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
		Reports mockReportsResponse = new Reports();
		mockReportsResponse.setActive("y");
		mockReportsResponse.setDisplayName("Reference Number");
		mockReportsResponse.setId(1L);
		mockReportsResponse.setReportCategory("Reference");
		mockReportsResponse.setReportDescription("Search");
		mockReportsResponse.setReportName("Refernce_No");
		mockReportsResponse.setValid("N");

		when(reportConfigurationService.saveReportConfiguration(any(ReportDTORequest.class))).thenReturn(mockReportsResponse);
		// execute
		MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.post("/reports/saveReport").contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON).content(TestUtils.objectToJson(mockReportsResponse)))
				.andReturn();

		// verify
		int status = result.getResponse().getStatus();
		assertEquals(HttpStatus.CREATED.value(), status, "Incorrect Response Status");

		// verify that service method was called once
		verify(reportConfigurationService).saveReportConfiguration(any(ReportDTORequest.class));
		String reportResponse = result.getResponse().getContentAsString();
		assertNotNull(reportResponse);
		assertEquals(ApplicationConstants.REPORT_CREATION_MSG, reportResponse);
	}

	@Test
	public void testfetchReports() throws Exception {

		Reports mockReportsResponse = new Reports();
		mockReportsResponse.setActive("y");
		mockReportsResponse.setDisplayName("Reference Number");
		mockReportsResponse.setId(1L);
		mockReportsResponse.setReportCategory("Reference");
		mockReportsResponse.setReportDescription("Search");
		mockReportsResponse.setReportName("Refernce_No");
		mockReportsResponse.setValid("N");

		List<Reports> mockReportsList = Arrays.asList(mockReportsResponse);

		Mockito.when(reportConfigurationService.fetchAllReports()).thenReturn(mockReportsList);

		mockMvc.perform(get("/reports")).andExpect(status().isOk())
				.andExpect(jsonPath("$", Matchers.hasSize(1)))
				.andExpect(jsonPath("$[0].reportName", Matchers.is("Refernce_No")));
	}
	
	@Test
	public void testdeleteReport() throws Exception {
		long reportId = 1L;
		mockMvc.perform(MockMvcRequestBuilders.delete("/reports/deleteReport/{reportId}", reportId))
				.andExpect(status().isAccepted());

	}

	@Test
	public void testupdateReport() throws Exception {
		long reportId = 1L;
		Reports mockReportsResponse = new Reports();
		mockReportsResponse.setActive("y");
		mockReportsResponse.setDisplayName("Reference Number");
		mockReportsResponse.setId(1L);
		mockReportsResponse.setReportCategory("Reference");
		mockReportsResponse.setReportDescription("Search");
		mockReportsResponse.setReportName("Refernce_No");
		mockReportsResponse.setValid("N");

		
		mockMvc.perform(MockMvcRequestBuilders.put("/reports/updateReport/{reportId}", reportId)
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