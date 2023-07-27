package com.mashreq.paymentTracker.controllerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.mashreq.paymentTracker.TestUtils;
import com.mashreq.paymentTracker.dto.ModuleDTO;
import com.mashreq.paymentTracker.dto.ModuleResponseDTO;
import com.mashreq.paymentTracker.serviceImpl.ModuleServiceImpl;

@SpringBootTest
@AutoConfigureMockMvc
public class ModuleControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ModuleServiceImpl mockModuleService;

	@Test
	public void testFetchModule() throws Exception {
		ModuleResponseDTO moduleMockObject = new ModuleResponseDTO();
		moduleMockObject.setModuleName("module1");
		moduleMockObject.setDisplayName("sampleModule");
		moduleMockObject.setModuleDescription("ModuleDesc");
		moduleMockObject.setActive("active");
		moduleMockObject.setValid("valid");

		List<ModuleResponseDTO> mockMetricsResponseDTOList = Arrays.asList(moduleMockObject);
		Mockito.when(mockModuleService.fetchAllModule()).thenReturn(mockMetricsResponseDTOList);

		mockMvc.perform(get("/module")).andExpect(status().isOk()).andExpect(jsonPath("$", Matchers.hasSize(1)))
				.andExpect(jsonPath("$[0].moduleName", Matchers.is("module1")));
	}

	@Test
	void testFetchModuleByName() throws Exception {
		List<ModuleResponseDTO> mockModuleResponseList = new ArrayList<ModuleResponseDTO>();
		ModuleResponseDTO mockModuleResponseDTO = new ModuleResponseDTO();
		mockModuleResponseDTO.setModuleName("module1");
		mockModuleResponseList.add(mockModuleResponseDTO);

		String moduleName = "Payment Tracker";
		when(mockModuleService.fetchModuleByName(moduleName)).thenReturn(mockModuleResponseList);
		mockMvc.perform(get("/module/{moduleName}", moduleName)).andExpect(status().isOk());

	}

	@Test
	public void testSaveModule() throws Exception {

		ModuleDTO moduleDTO = new ModuleDTO();
		moduleDTO.setName("module1");
		moduleDTO.setDisplayName("sampleModule");
		moduleDTO.setModuleDescription("ModuleDesc");
		moduleDTO.setActive("active");
		moduleDTO.setValid("valid");
		ModuleResponseDTO mockModuleResponseDTO = new ModuleResponseDTO();
		mockModuleResponseDTO.setModuleName("module1");
		when(mockModuleService.saveModule(moduleDTO)).thenReturn(mockModuleResponseDTO);
		MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.post("/module").contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON).content(TestUtils.objectToJson(moduleDTO)))
				.andReturn();

		int status = result.getResponse().getStatus();
		assertEquals(HttpStatus.OK.value(), status);
		String reportResponse = result.getResponse().getContentAsString();

	}

	@Test
	void testUpdateReport() throws Exception {
		ModuleDTO moduleDTO = new ModuleDTO();
		moduleDTO.setName("module1");
		moduleDTO.setDisplayName("sampleModule");
		moduleDTO.setModuleDescription("ModuleDesc");
		moduleDTO.setActive("active");
		moduleDTO.setValid("valid");
		ModuleResponseDTO mockModuleResponseDTO = new ModuleResponseDTO();
		mockModuleResponseDTO.setModuleName("module1");
		when(mockModuleService.updateModule(moduleDTO, 1L)).thenReturn(mockModuleResponseDTO);
		MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.put("/module/{moduleId}", 1).contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON).content(TestUtils.objectToJson(moduleDTO)))
				.andReturn();

		int status = result.getResponse().getStatus();
		assertEquals(HttpStatus.OK.value(), status);
		String reportResponse = result.getResponse().getContentAsString();
	}

	@Test
	public void testdeleteModule() throws Exception {
		long moduleId = 1L;
		mockMvc.perform(MockMvcRequestBuilders.delete("/module/{moduleId}", moduleId)).andExpect(status().isOk());
		verify(mockModuleService).deleteModule(moduleId);

	}

}
