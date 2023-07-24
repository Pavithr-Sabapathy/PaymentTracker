package com.mashreq.paymentTracker.controllerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashreq.paymentTracker.TestUtils;
import com.mashreq.paymentTracker.dto.DataSourceRequestDTO;
import com.mashreq.paymentTracker.dto.DataSourceResponseDTO;
import com.mashreq.paymentTracker.model.DataSource;
import com.mashreq.paymentTracker.service.DataSourceConfigService;

@SpringBootTest
@AutoConfigureMockMvc
public class DataSourceConfigControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private DataSourceConfigService dataSourceConfigService;

	@Test
	public void testSaveDataSourceConfig() throws Exception {
		DataSourceRequestDTO mockDataSourceDTO = new DataSourceRequestDTO("sample", "testCase database", 1L, "sample",
				"12345", "Y", "11232", 1L, "sample", "yes", "Y");

		DataSourceResponseDTO mockdataSourceConfigValue = new DataSourceResponseDTO(1L, "sample", "oracle", 1L,
				"Oracle", "ReadOnly", "12345", "@!@#234", 1L, "123.13.34.56", "PT", "y");

		when(dataSourceConfigService.saveDataSourceConfiguration(mockDataSourceDTO))
				.thenReturn(mockdataSourceConfigValue);
		// execute
		MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.post("/dataSource/save").contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON).content(TestUtils.objectToJson(mockdataSourceConfigValue)))
				.andReturn();

		// verify
		int status = result.getResponse().getStatus();
		assertEquals(HttpStatus.CREATED.value(), status);

		// verify that service method was called once
		// verify(dataSourceConfigService).saveDataSourceConfiguration(mockDataSourceDTO);
		String reportResponse = result.getResponse().getContentAsString();
	}

	@Test
	public void testAllDataSourceConfig() throws Exception {

		DataSource dataSourceConfigValue = new DataSource(1L, "Oracle", "null", BigInteger.ZERO, "Oracle", "ReadOnly",
				"12345", "@!@#234", BigInteger.ZERO, "123.13.34.56", "PT", "y", "UAE");
		List<DataSource> mockDatasourceConfigList = Arrays.asList(dataSourceConfigValue);

		Map<String, Object> dataSourceMap = new HashMap<String, Object>();
		dataSourceMap.put("totalPage", 45);
		dataSourceMap.put("offSet", 10);
		dataSourceMap.put("dataSource", mockDatasourceConfigList);

		Mockito.when(dataSourceConfigService.allDataSourceConfig(0, 10, null)).thenReturn(dataSourceMap);

		mockMvc.perform(get("/dataSource//allDataSource")).andExpect(status().isOk());
	}

	@Test
	public void testGetDataSourceConfig() throws Exception {
		long dataSourceId = 1L;
		DataSourceResponseDTO mockdataSourceConfigValue = new DataSourceResponseDTO(1L, "sample", "oracle", 1L,
				"Oracle", "ReadOnly", "12345", "@!@#234", 1L, "123.13.34.56", "PT", "y");
		Mockito.when(dataSourceConfigService.getDataSourceConfigById(dataSourceId))
				.thenReturn(mockdataSourceConfigValue);

		mockMvc.perform(get("/dataSource/id/{dataSourceId}", dataSourceId)).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value("sample"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.dataSourceId").value(1));
	}

	@Test
	public void testDeleteDataSourceConfig() throws Exception {
		long dataSourceId = 1L;
		mockMvc.perform(MockMvcRequestBuilders.delete("/dataSource/{dataSourceId}", dataSourceId))
				.andExpect(status().isAccepted());

	}

	@Test
	public void testUpdateDataSourceConfig() throws Exception {
		long dataSourceId = 1L;
		// mockMvc.perform(MockMvcRequestBuilders.put("/dataSource/updateDataSourceConfig/{dataSourceId}",
		// dataSourceId) ---> to be check
		mockMvc.perform(MockMvcRequestBuilders.put("/dataSource/{dataSourceId}", dataSourceId)
				.content(asJsonString(new DataSource(1L, "Oracle1", "ReadValue", BigInteger.ZERO, "Oracle", "ReadOnly",
						"12345", "@!@#234", BigInteger.ZERO, "123.13.34.56", "PT", "y", "UAE")))
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