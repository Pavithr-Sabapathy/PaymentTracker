package com.mashreq.paymentTracker.controllerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigInteger;
import java.util.Arrays;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashreq.paymentTracker.TestUtils;
import com.mashreq.paymentTracker.dto.PromptDTO;
import com.mashreq.paymentTracker.dto.PromptRequestDTO;
import com.mashreq.paymentTracker.dto.PromptResponseDTO;
import com.mashreq.paymentTracker.model.Prompts;
import com.mashreq.paymentTracker.service.promptService;

@SpringBootTest
@AutoConfigureMockMvc
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

		mockMvc.perform(get("/prompt")).andExpect(status().isOk());
	}

	@Test
	public void testFetchPromptByReportId() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String responseString = "[{\"promptKey\":\"prkey001\",\"displayName\":\"prdspl001\",\"promptOrder\":1001,\"promptRequired\":\"y\",\"reportId\":1,\"entityId\":1},{\"promptKey\":\"prkey002\",\"displayName\":\"prdspl002\",\"promptOrder\":1002,\"promptRequired\":\"y\",\"reportId\":1,\"entityId\":2}]";

		PromptDTO[] mockPromptDto = mapper.readValue(responseString, PromptDTO[].class);

		List<PromptDTO> mockPromptDtoList = Arrays.asList(mockPromptDto);

		long reportId = 1L;

		Mockito.when(promptService.fetchPromptsByReportId(reportId)).thenReturn(mockPromptDtoList);

		mockMvc.perform(
				MockMvcRequestBuilders.get("/prompt/{reportId}", reportId).param("reportId", Long.toString(reportId)))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testsavePrompt() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String promptResponseString = "{\"displayName\": \"Advanced Search\",\"promptKey\": \"1\",\"promptOrder\": 1,\"promptRequired\": \"y\"}";
		Prompts mockPromptResponse = mapper.readValue(promptResponseString, Prompts.class);

		promptService.savePrompt(any(PromptRequestDTO.class));
		// execute
		MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.post("/prompt/savePrompt").contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON).content(TestUtils.objectToJson(mockPromptResponse)))
				.andReturn();
		// verify
		int status = result.getResponse().getStatus();
		assertEquals(HttpStatus.CREATED.value(), status, "Incorrect Response Status");

		// verify that service method was called once
		verify(promptService).savePrompt(any(PromptRequestDTO.class));
		String reportResponse = result.getResponse().getContentAsString();
		assertNotNull(reportResponse);
	}

	@Test
	public void testdeletePrompt() throws Exception {
		long promptId = 1L;
		mockMvc.perform(MockMvcRequestBuilders.delete("/prompt/{promptId}", promptId)).andExpect(status().isAccepted());

	}

	@Test
	public void testupdatePrompt() throws Exception {
		long promptId = 1L;
		mockMvc.perform(MockMvcRequestBuilders.put("/prompt/{promptId}", promptId)
				.content(asJsonString(new PromptRequestDTO("Reference_Sample", "Reference Sample", BigInteger.ONE, "N",
						1, BigInteger.ZERO)))
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