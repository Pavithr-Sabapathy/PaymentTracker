package com.mashreq.paymentTracker.controllerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
import com.mashreq.paymentTracker.controller.DataSourceConfigController;
import com.mashreq.paymentTracker.model.DataSourceConfig;
import com.mashreq.paymentTracker.service.DataSourceConfigService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(DataSourceConfigController.class)
public class DataSourceConfigControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private DataSourceConfigService dataSourceConfigService;

	@Test
	public void testSaveDataSourceConfig() throws Exception {
		DataSourceConfig mockDataSourceConfig = new DataSourceConfig(1L, "Oracle", "null", BigInteger.ZERO, "Oracle",
				"ReadOnly", "12345", "@!@#234", BigInteger.ZERO, "123.13.34.56", "PT", "y","UAE");

		when(dataSourceConfigService.saveDataSourceConfiguration(any(DataSourceConfig.class)))
				.thenReturn(mockDataSourceConfig);
		// execute
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/dataSource/saveDataSourceConfig")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.content(TestUtils.objectToJson(mockDataSourceConfig))).andReturn();

		// verify
		int status = result.getResponse().getStatus();
		assertEquals(HttpStatus.CREATED.value(), status, "Incorrect Response Status");

		// verify that service method was called once
		verify(dataSourceConfigService).saveDataSourceConfiguration(mockDataSourceConfig);
		String dataSourceConfigResponse = result.getResponse().getContentAsString();
		assertNotNull(dataSourceConfigResponse);
		assertEquals(ApplicationConstants.DATA_SOURCE_CREATION_MSG, dataSourceConfigResponse);
	}

	@Test
	public void testAllDataSourceConfig() throws Exception {

		DataSourceConfig dataSourceConfigValue = new DataSourceConfig(1L, "Oracle", "null",BigInteger.ZERO, "Oracle",
				"ReadOnly", "12345", "@!@#234", BigInteger.ZERO, "123.13.34.56", "PT", "y","UAE");
		List<DataSourceConfig> mockDatasourceConfigList = Arrays.asList(dataSourceConfigValue);

		Mockito.when(dataSourceConfigService.allDataSourceConfig()).thenReturn(mockDatasourceConfigList);

		mockMvc.perform(get("/dataSource/allDataSourceConfig")).andExpect(status().isOk())
				.andExpect(jsonPath("$", Matchers.hasSize(1)))
				.andExpect(jsonPath("$[0].dataSourceName", Matchers.is("Oracle")));
	}

	@Test
	public void testGetDataSourceConfig() throws Exception {
		long dataSourceId = 1L;
		DataSourceConfig mockDataSourceConfigValue = new DataSourceConfig(1L, "Oracle", "null", BigInteger.ZERO, "Oracle",
				"ReadOnly", "12345", "@!@#234", BigInteger.ZERO, "123.13.34.56", "PT", "y","UAE");
		Mockito.when(dataSourceConfigService.getDataSourceConfigById(dataSourceId))
				.thenReturn(mockDataSourceConfigValue);

		mockMvc.perform(get("/dataSource/getDataSourceConfig/1")).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.dataSourceName").value("Oracle"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1));
	}

	@Test
	public void testDeleteDataSourceConfig() throws Exception {
		long dataSourceId = 1L;
		mockMvc.perform(MockMvcRequestBuilders.delete("/dataSource/deleteDataSource/{dataSourceId}", dataSourceId))
				.andExpect(status().isAccepted());

	}

	@Test
	public void testUpdateDataSourceConfig() throws Exception {
		long dataSourceId = 1L;
		mockMvc.perform(MockMvcRequestBuilders.put("/dataSource/updateDataSourceConfig/{dataSourceId}", dataSourceId)
				.content(asJsonString(new DataSourceConfig(1L, "Oracle1", "ReadValue", BigInteger.ZERO, "Oracle",
						"ReadOnly", "12345", "@!@#234", BigInteger.ZERO, "123.13.34.56", "PT", "y","UAE")))
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
