package com.mashreq.paymentTracker.controllerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import com.mashreq.paymentTracker.controller.ComponentsController;
import com.mashreq.paymentTracker.dto.ComponentDetailsRequestDTO;
import com.mashreq.paymentTracker.dto.ComponentsRequestDTO;
import com.mashreq.paymentTracker.service.ComponentsService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ComponentsController.class)
public class ComponentControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ComponentsService componentService;

	@Test
	public void testSaveComponents() throws Exception {
		ComponentsRequestDTO componentRequestDTO = new ComponentsRequestDTO(0L, "sample_Tracker", "Sample Tracker", "Y", 1,5);

		componentService.saveComponents(componentRequestDTO);
		// execute
		MvcResult result = mockMvc.perform(
				MockMvcRequestBuilders.post("/Components/saveComponents").contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON).content(TestUtils.objectToJson(componentRequestDTO)))
				.andReturn();

		// verify
		int status = result.getResponse().getStatus();
		assertEquals(HttpStatus.CREATED.value(), status, "Incorrect Response Status");

		// verify that service method 
		verify(componentService).saveComponents(componentRequestDTO);
		String componetCreationMsg = result.getResponse().getContentAsString();
		assertNotNull(componetCreationMsg);
		assertEquals(ApplicationConstants.COMPONENT_CREATION_MSG, componetCreationMsg);
	}
   

	@Test
	public void testSaveComponentsDetails() throws Exception {
		ComponentDetailsRequestDTO mockcomponentDetailsDTO = new ComponentDetailsRequestDTO("rsMesg", "sampleEquery",1);

		componentService.saveComponentsDetails(mockcomponentDetailsDTO);
		// execute
		MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.post("/Components/saveDetails").contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON).content(TestUtils.objectToJson(mockcomponentDetailsDTO)))
				.andReturn();

		// verify
		int status = result.getResponse().getStatus();
		assertEquals(HttpStatus.CREATED.value(), status, "Incorrect Response Status");

		// verify that service method was called once
		verify(componentService).saveComponentsDetails(mockcomponentDetailsDTO);
		String ComponentDetailCreationMsg = result.getResponse().getContentAsString();
		assertNotNull(ComponentDetailCreationMsg);
		assertEquals(ApplicationConstants.COMPONENT_DETAILS_CREATION_MSG, ComponentDetailCreationMsg);
	}

	@Test
	public void testdeleteComponents() throws Exception {
		long componentId = 1L;
		mockMvc.perform(MockMvcRequestBuilders.delete("/Components/{componentId}", componentId))
				.andExpect(status().isAccepted());

	}

	@Test
	public void testdeleteComponentDetails() throws Exception {
		long componentDetailId = 1L;
		mockMvc.perform(
				MockMvcRequestBuilders.delete("/Components/componentDetail/{componentDetailId}", componentDetailId))
				.andExpect(status().isAccepted());

	}
}