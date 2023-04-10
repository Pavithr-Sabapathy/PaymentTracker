package com.mashreq.paymentTracker.controllerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashreq.paymentTracker.TestUtils;
import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.controller.PromptsController;
import com.mashreq.paymentTracker.dto.PromptDTO;
import com.mashreq.paymentTracker.dto.PromptResponseDTO;
import com.mashreq.paymentTracker.model.Prompts;
import com.mashreq.paymentTracker.service.promptService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PromptsController.class)
public class PromptsControllerTest {
	
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	promptService promptService;
	
	@Test
	public void testfetchPrompts() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String responseString = "[{\"reports\":{\"id\": 5,\"reportName\": \"search\",\"displayName\": \"Search\",\"reportDescription\": \"Search\",\"reportCategory\": \"searching\",\"active\": \"y\",\"valid\": \"y\"},\"promptsList\": [{\"promptKey\": \"currency\",\"displayName\": \"Currency\",\"promptOrder\": 1,\"promptRequired\": \"y\",\"reportId\": 5,\"entityId\": null},{\"promptKey\": \"sampleSearch1\",\"displayName\": \"sample Search1\",\"promptOrder\": 2,\"promptRequired\": \"y\",\"reportId\": 5,\"entityId\": null}]}]";
		
		PromptResponseDTO[] mockPromptResponseDTO = mapper.readValue(responseString, PromptResponseDTO[].class);
		
		List<PromptResponseDTO> mockPromptResponseDTOList = Arrays.asList(mockPromptResponseDTO);

		Mockito.when(promptService.fetchAllPrompts()).thenReturn(mockPromptResponseDTOList);

		mockMvc.perform(get("/prompt")).andExpect(status().isOk())
				.andExpect(jsonPath("$", Matchers.hasSize(1)))
				.andExpect(jsonPath("$[0].reports.reportName", Matchers.is("search")));
	}
	
	@Test
	public void testsavePrompt() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String promptResponseString = "{\"displayName\": \"Advanced Search\",\"entityId\": 0,\"promptKey\": \"1\",\"promptOrder\": 1,\"promptRequired\": \"y\"}";
		Prompts mockPromptResponse = mapper.readValue(promptResponseString, Prompts.class);
		
		promptService.savePrompt(any(PromptDTO.class));
		// execute
		MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.post("/prompt/savePrompt").contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON).content(TestUtils.objectToJson(mockPromptResponse)))
				.andReturn();
		// verify
		int status = result.getResponse().getStatus();
		assertEquals(HttpStatus.CREATED.value(), status, "Incorrect Response Status");

		// verify that service method was called once
		verify(promptService).savePrompt(any(PromptDTO.class));
		String reportResponse = result.getResponse().getContentAsString();
		assertNotNull(reportResponse);
		assertEquals(ApplicationConstants.PROMPTS_CREATION_MSG, reportResponse);
	}
	
	@Test
	public void testdeletePrompt() throws Exception {
		long promptId = 1L;
		mockMvc.perform(MockMvcRequestBuilders.delete("/prompt/{promptId}", promptId))
				.andExpect(status().isAccepted());

	}

	@Test
	public void testupdatePrompt() throws Exception {
		long promptId = 1L;
		mockMvc.perform(MockMvcRequestBuilders.put("/prompt/{promptId}", promptId)
				.content(asJsonString(new PromptDTO("Reference_Sample", "Reference Sample", BigInteger.ONE,
						"N", 1, BigInteger.ZERO)))
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