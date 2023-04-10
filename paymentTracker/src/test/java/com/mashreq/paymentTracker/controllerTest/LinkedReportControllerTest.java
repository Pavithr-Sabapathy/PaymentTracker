package com.mashreq.paymentTracker.controllerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

import com.mashreq.paymentTracker.TestUtils;
import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.controller.LinkedReportController;
import com.mashreq.paymentTracker.dto.LinkedReportRequestDTO;
import com.mashreq.paymentTracker.dto.LinkedReportResponseDTO;
import com.mashreq.paymentTracker.service.LinkReportService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(LinkedReportController.class)
public class LinkedReportControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	LinkReportService linkReportService;

	@Test
	public void testsaveOrUpdateLinkedReport() throws Exception {
		LinkedReportRequestDTO linkedReportRequestDTO = new LinkedReportRequestDTO(1, "sample Link",
				"Sample Link Description", 1, 4, 4, "y");
		linkReportService.saveOrUpdateLinkedReport(linkedReportRequestDTO);
		// execute
		MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.post("/linkReport").contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON).content(TestUtils.objectToJson(linkedReportRequestDTO)))
				.andReturn();

		// verify
		int status = result.getResponse().getStatus();
		assertEquals(HttpStatus.CREATED.value(), status, "Incorrect Response Status");

		// verify that service method was called once
		verify(linkReportService).saveOrUpdateLinkedReport(linkedReportRequestDTO);
		String linkReportCreationMsg = result.getResponse().getContentAsString();
		assertNotNull(linkReportCreationMsg);
		assertEquals(ApplicationConstants.LINK_REPORT_CREATION_MSG, linkReportCreationMsg);
	}

	@Test
	public void testfetchLinkedReport() throws Exception {
		LinkedReportResponseDTO linkReportResponseDTO = new LinkedReportResponseDTO(1, "sample Link",
				"Sample Link Description", "sample Report", "Sample linked Repot", "metrics deatils", "y");

		long linkedReportId = 1L;
		Mockito.when(linkReportService.fetchLinkedReportById(linkedReportId)).thenReturn(linkReportResponseDTO);

		mockMvc.perform(get("/linkReport/{linkedReportId}", linkedReportId)).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.linkDescription").value("Sample Link Description"));
	}

}