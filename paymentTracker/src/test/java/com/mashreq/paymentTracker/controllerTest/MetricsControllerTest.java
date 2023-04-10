package com.mashreq.paymentTracker.controllerTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashreq.paymentTracker.controller.MetricsController;
import com.mashreq.paymentTracker.dto.MetricsDTO;
import com.mashreq.paymentTracker.dto.MetricsResponseDTO;
import com.mashreq.paymentTracker.service.MetricsService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(MetricsController.class)
public class MetricsControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	MetricsService metricsService;

	@Test
	public void testfetchReports() throws Exception {
		String metricsStringresponse = "[{\"reports\": {\"id\": 3,\"reportName\": \"AdvanceSearch\",\"displayName\": \"Advance Search\",\"reportDescription\": \"Advance Search Information\",\"reportCategory\": \"Search\",\"active\": \"y\",\"valid\": \"y\"},\"metricsList\": [{\"display\": \"metrics sample\",\"displayName\": \"metrics\",\"entityId\": 0,\"metricsOrder\": 1,\"reportId\": 1}]}]";
		ObjectMapper mapper = new ObjectMapper();
		MetricsResponseDTO[] mockMetricsResponseDTO = mapper.readValue(metricsStringresponse,
				MetricsResponseDTO[].class);

		List<MetricsResponseDTO> mockMetricsResponseDTOList = Arrays.asList(mockMetricsResponseDTO);
		Mockito.when(metricsService.fetchAllMetrics()).thenReturn(mockMetricsResponseDTOList);

		mockMvc.perform(get("/metrics")).andExpect(status().isOk()).andExpect(jsonPath("$", Matchers.hasSize(1)))
				.andExpect(jsonPath("$[0].reports.reportName", Matchers.is("AdvanceSearch")));
	}

	
	@Test
	public void testdeleteMetrics() throws Exception {
		long metricsId = 1L;
		mockMvc.perform(MockMvcRequestBuilders.delete("/metrics/{metricsId}", metricsId))
				.andExpect(status().isAccepted());

	}

	@Test
	public void testupdateMetrics() throws Exception {
		long metricsId = 1L;
		mockMvc.perform(MockMvcRequestBuilders.put("/metrics/{metricsId}", metricsId)
				.content(asJsonString(new MetricsDTO("Metrics_Sample", BigInteger.ONE, "y",1, BigInteger.ZERO)))
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