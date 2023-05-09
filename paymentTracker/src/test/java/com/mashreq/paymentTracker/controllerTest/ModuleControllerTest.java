package com.mashreq.paymentTracker.controllerTest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import java.util.*;

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
import com.mashreq.paymentTracker.controller.ModuleController;
import com.mashreq.paymentTracker.model.ApplicationModule;
import com.mashreq.paymentTracker.serviceImpl.ModuleServiceImpl;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ModuleController.class)
public class ModuleControllerTest {
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private ModuleServiceImpl mockModuleService;
	
	@Test
	public void testFetchModule() throws Exception{
		ApplicationModule moduleMockObject = new ApplicationModule();
		moduleMockObject.setModuleName("module1");
		moduleMockObject.setDisplayName("sampleModule");
		moduleMockObject.setModuleDescription("ModuleDesc");
		moduleMockObject.setActive("active");
		moduleMockObject.setValid("valid");

		List<ApplicationModule> mockMetricsResponseDTOList = Arrays.asList(moduleMockObject);
		Mockito.when(mockModuleService.fetchAllModule()).thenReturn(mockMetricsResponseDTOList);

		mockMvc.perform(get("/module")).andExpect(status().isOk())
		                                .andExpect(jsonPath("$", Matchers.hasSize(1)))
				.andExpect(jsonPath("$[0].moduleName", Matchers.is("module1")));
	}
	
	@Test
	public void testSaveModule() throws Exception {
		
		ApplicationModule moduleMockObject = new ApplicationModule();
		moduleMockObject.setModuleName("module1");
		moduleMockObject.setDisplayName("sampleModule");
		moduleMockObject.setModuleDescription("ModuleDesc");
		moduleMockObject.setActive("active");
		moduleMockObject.setValid("valid");
	        doNothing().when(mockModuleService).saveModule(moduleMockObject);;

	        MvcResult result =   mockMvc.perform(MockMvcRequestBuilders.post("/module")
	        		.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
	        		.content(TestUtils.objectToJson(moduleMockObject))).andReturn();

	        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

	
	}
	@Test
	public void testdeleteModule() throws Exception {
		long moduleId = 1L;
		mockMvc.perform(MockMvcRequestBuilders.delete("/module/{moduleId}", moduleId))
				.andExpect(status().isOk());
		verify(mockModuleService).deleteModule(moduleId);


	}
	
}
	


