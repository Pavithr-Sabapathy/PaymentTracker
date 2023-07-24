package com.mashreq.paymentTracker.controllerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matchers;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashreq.paymentTracker.TestUtils;
import com.mashreq.paymentTracker.dto.MetricsRequestDTO;
import com.mashreq.paymentTracker.dto.MetricsResponse;
import com.mashreq.paymentTracker.dto.MetricsResponseDTO;
import com.mashreq.paymentTracker.service.MetricsService;

@SpringBootTest
@AutoConfigureMockMvc
public class MetricsControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	MetricsService metricsService;

	@Test
	public void testSaveMetrics() throws Exception {

		MetricsResponseDTO metricsResponse = new MetricsResponseDTO();
		metricsResponse.setReportId(1L);
		metricsResponse.setDisplayName("Test Metrics");
		metricsResponse.setDisplay("y");
		metricsResponse.setEntityId(BigInteger.ZERO);
		metricsResponse.setMetricsOrder(BigInteger.ONE);
		metricsResponse.setReportId(1L);
		when(metricsService.saveMetrics(any(MetricsRequestDTO.class))).thenReturn(metricsResponse);

		MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.post("/metrics/saveMetrics").contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON).content(TestUtils.objectToJson(metricsResponse)))
				.andReturn();

		assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());

	}

	@Test
	public void testfetchMetrics() throws Exception {
		String metricsStringresponse = "[{\"reports\": {\"id\": 3,\"reportName\": \"AdvanceSearch\",\"displayName\": \"Advance Search\",\"reportDescription\": \"Advance Search Information\",\"reportCategory\": \"Search\",\"active\": \"y\",\"valid\": \"y\"},\"metricsList\": [{\"display\": \"metrics sample\",\"displayName\": \"metrics\",\"entityId\": 0,\"metricsOrder\": 1,\"reportId\": 1}]}]";
		ObjectMapper mapper = new ObjectMapper();
		MetricsResponse[] mockMetricsResponseDTO = mapper.readValue(metricsStringresponse, MetricsResponse[].class);

		List<MetricsResponse> mockMetricsResponseDTOList = Arrays.asList(mockMetricsResponseDTO);
		Mockito.when(metricsService.fetchAllMetrics()).thenReturn(mockMetricsResponseDTOList);

		// Send a GET request to the endpoint with the "sampleModule" name in the URL
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/metrics"))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		TypeReference<List<MetricsResponse>> mapType = new TypeReference<List<MetricsResponse>>() {
		};
		// Check
		String response = result.getResponse().getContentAsString();
		List<MetricsResponse> responseReport = new ObjectMapper().readValue(response, mapType);
		// verify
		Mockito.verify(metricsService).fetchAllMetrics();

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
		mockMvc.perform(MockMvcRequestBuilders.put("/metrics/update/{metricsId}", metricsId)
				.content(asJsonString(new MetricsRequestDTO("Metrics_Sample", BigInteger.ONE, "y", 1, BigInteger.ZERO)))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isAccepted());
	}

	@Test
	public void testfetchMetricsByReportId() throws Exception {

		long reportId = 1L;

		MetricsResponseDTO metrics1 = new MetricsResponseDTO();
		metrics1.setReportId(reportId);
		metrics1.setDisplayName("Metrics 1");
		metrics1.setEntityId(BigInteger.ZERO);

		MetricsResponseDTO metrics2 = new MetricsResponseDTO();
		metrics1.setReportId(reportId);
		metrics2.setDisplayName("Metrics 2");
		metrics2.setEntityId(BigInteger.ZERO);

		List<MetricsResponseDTO> metricsList = new ArrayList<MetricsResponseDTO>();
		metricsList.add(metrics1);
		metricsList.add(metrics2);

		when(metricsService.fetchMetricsByReportId(reportId)).thenReturn(metricsList);

		mockMvc.perform(MockMvcRequestBuilders.get("/metrics/report/{reportId}", reportId).param("reportId",
				Long.toString(reportId))).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].displayName", Matchers.is("Metrics 1")));

		verify(metricsService).fetchMetricsByReportId(reportId);
	}

	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}