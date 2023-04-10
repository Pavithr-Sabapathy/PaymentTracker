package com.mashreq.paymentTracker.controllerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
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

import com.mashreq.paymentTracker.TestUtils;
import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.controller.LinkMappingController;
import com.mashreq.paymentTracker.dto.LinkMappingResponseDTO;
import com.mashreq.paymentTracker.dto.LinkedReportMappingRequestDTO;
import com.mashreq.paymentTracker.service.LinkMappingService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(LinkMappingController.class)
public class LinkMappingControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	LinkMappingService linkMappingService;

	@Test
	public void testsaveOrUpdateLinkMapping() throws Exception {
		LinkedReportMappingRequestDTO linkedReportMappingRequestDTO = new LinkedReportMappingRequestDTO(1, 1, 4, 4,
				"y");
		linkMappingService.saveOrUpdateLinkMapping(linkedReportMappingRequestDTO);
		// execute
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/linkMapping")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.content(TestUtils.objectToJson(linkedReportMappingRequestDTO))).andReturn();

		// verify
		int status = result.getResponse().getStatus();
		assertEquals(HttpStatus.CREATED.value(), status, "Incorrect Response Status");

		// verify that service method was called once
		verify(linkMappingService).saveOrUpdateLinkMapping(linkedReportMappingRequestDTO);
		String linkReportCreationMsg = result.getResponse().getContentAsString();
		assertNotNull(linkReportCreationMsg);
		assertEquals(ApplicationConstants.LINK_MAPPING_REPORT_CREATION_MSG, linkReportCreationMsg);
	}

	@Test
	public void testfetchLinkMapping() throws Exception {
		List<LinkMappingResponseDTO> listMappingResponseDTOList = new ArrayList<LinkMappingResponseDTO>();
		LinkMappingResponseDTO linkReportResponseDTO = new LinkMappingResponseDTO(1, "M", 1, "Metrics", 1,
				"SampleReference");
		listMappingResponseDTOList.add(linkReportResponseDTO);
		long linkedReportId = 1L;
		Mockito.when(linkMappingService.fetchLinkMappingById(linkedReportId)).thenReturn(listMappingResponseDTOList);
		mockMvc.perform(get("/linkMapping/{linkReportId}", linkedReportId)).andExpect(status().isOk())
				.andExpect(jsonPath("$", Matchers.hasSize(1)))
				.andExpect(jsonPath("$[0].mappingType", Matchers.is("M")));
	}
}