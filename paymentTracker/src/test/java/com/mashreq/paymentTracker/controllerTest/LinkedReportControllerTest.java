
package com.mashreq.paymentTracker.controllerTest;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.mashreq.paymentTracker.TestUtils;
import com.mashreq.paymentTracker.dto.LinkedReportRequestDTO;
import com.mashreq.paymentTracker.dto.LinkedReportResponseDTO;
import com.mashreq.paymentTracker.service.LinkReportService;

@SpringBootTest
@AutoConfigureMockMvc
public class LinkedReportControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	LinkReportService linkReportService;

	@Test
	public void testsaveOrUpdateLinkedReport() throws Exception {
		LinkedReportRequestDTO linkedReportRequestDTO = new LinkedReportRequestDTO();
		linkedReportRequestDTO.setId(1L);
		linkedReportRequestDTO.setLinkName("Sample Link");
		linkedReportRequestDTO.setLinkDescription("Sample Link Description");
		linkedReportRequestDTO.setReportId(1L);
		linkedReportRequestDTO.setLinkedReportId(4);
		linkedReportRequestDTO.setSourceMetricId(4);
		linkedReportRequestDTO.setActive("y");
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
	}

	@Test
	public void testfetchLinkedReport() throws Exception {
		LinkedReportResponseDTO linkReportResponseDTO = new LinkedReportResponseDTO(1, "sample Link",
				"Sample Link Description", "sample Report", 1L, 1L, "Sample linked Repot", "metrics deatils", 0, "y",
				"sample", "sample", 0, 0, null, 0);

		long linkedReportId = 1L;
		Mockito.when(linkReportService.fetchLinkedReportById(linkedReportId)).thenReturn(linkReportResponseDTO);

		mockMvc.perform(get("/linkReport/{linkedReportId}", linkedReportId)).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.linkDescription").value("Sample Link Description"));
	}

	@Test
	public void testdeleteLinkedReport() throws Exception {

		long linkedReportId = 1L;
		mockMvc.perform(MockMvcRequestBuilders.delete("/linkReport/{linkedReportId}", linkedReportId))
				.andExpect(status().isAccepted());

	}

	@Test
	void testFetchLinkedReportByReportId() throws Exception {
		long reportId = 1L;
		List<LinkedReportResponseDTO> mockLinkedReportResponseDTOList = new ArrayList<LinkedReportResponseDTO>();
		LinkedReportResponseDTO linkReportResponseDTO = new LinkedReportResponseDTO(1, "sample Link",
				"Sample Link Description", "sample Report", 1L, 1L, "Sample linked Repot", "metrics deatils", 0, "y",
				"sample", "sample", 0, 0, null, 0);
		mockLinkedReportResponseDTOList.add(linkReportResponseDTO);
		Mockito.when(linkReportService.fetchLinkedReportByReportId(reportId))
				.thenReturn(mockLinkedReportResponseDTOList);

		mockMvc.perform(get("/linkReport/report/{reportId}", reportId)).andExpect(status().isOk()).andExpect(
				MockMvcResultMatchers.jsonPath("$.[0].linkDescription", startsWith("Sample Link Description")));
	}

	@Test
	void testFetchLinkedReportByModuleId() throws Exception {
		long moduleId = 1L;
		List<LinkedReportResponseDTO> mockLinkedReportResponseDTOList = new ArrayList<LinkedReportResponseDTO>();
		LinkedReportResponseDTO linkReportResponseDTO = new LinkedReportResponseDTO(1, "sample Link",
				"Sample Link Description", "sample Report", 1L, 1L, "Sample linked Repot", "metrics deatils", 0, "y",
				"sample", "sample", 0, 0, null, 0);
		mockLinkedReportResponseDTOList.add(linkReportResponseDTO);
		Mockito.when(linkReportService.fetchLinkedReportByModuleId(moduleId))
				.thenReturn(mockLinkedReportResponseDTOList);

		mockMvc.perform(get("/linkReport/module/{moduleId}", moduleId)).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.[0].linkDescription").value("Sample Link Description"));
	}
}